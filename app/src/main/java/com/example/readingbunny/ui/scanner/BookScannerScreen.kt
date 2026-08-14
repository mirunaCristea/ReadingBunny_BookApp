package com.example.readingbunny.ui.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import java.util.concurrent.Executors

@Composable
fun BookScannerScreen(
    mode: BookScannerMode,
    onModeChange: (BookScannerMode) -> Unit,
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ScannerModeSelector(
            selectedMode = mode,
            onModeChange = onModeChange
        )

        if (hasCameraPermission) {

            CameraPreview(
                mode = mode,
                onBarcodeDetected = onBarcodeDetected,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Camera permission is required to scan books."
                )

                Button(
                    onClick = {
                        permissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text("Allow camera")
                }
            }
        }
    }
}


@Composable
private fun ScannerModeSelector(
    selectedMode: BookScannerMode,
    onModeChange: (BookScannerMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilterChip(
            selected = selectedMode == BookScannerMode.BARCODE,
            onClick = {
                onModeChange(BookScannerMode.BARCODE)
            },
            label = {
                Text("Barcode")
            },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = selectedMode == BookScannerMode.SPINE,
            onClick = {
                onModeChange(BookScannerMode.SPINE)
            },
            label = {
                Text("Book spine")
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CameraPreview(
    mode: BookScannerMode,
    onBarcodeDetected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    val barcodeScanner = remember {
        BarcodeScanning.getClient()
    }



    var cameraProvider by remember {
        mutableStateOf<ProcessCameraProvider?>(null)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
            barcodeScanner.close()
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->

            val previewView = PreviewView(viewContext).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(viewContext)

            cameraProviderFuture.addListener({

                try {

                    val provider =
                        cameraProviderFuture.get()

                    cameraProvider = provider

                    val preview =
                        Preview.Builder()
                            .build()
                            .also { cameraPreview ->

                                cameraPreview.surfaceProvider =
                                    previewView.surfaceProvider
                            }




                    val imageAnalysis =
                        androidx.camera.core.ImageAnalysis.Builder()
                            .setBackpressureStrategy(
                                androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                            )
                            .build()

                    Log.d(
                        "BookScanner",
                        "Attaching barcode analyzer"
                    )

                    imageAnalysis.setAnalyzer(
                        cameraExecutor,
                        BarcodeBookAnalyzer(
                            scanner = barcodeScanner,
                            onBarcodeDetected = onBarcodeDetected
                        )
                    )

                    provider.unbindAll()

                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )

                    Log.d(
                        "BookScanner",
                        "Preview + ImageAnalysis successfully bound"
                    )



                } catch (exception: Exception) {

                    Log.e(
                        "BookScanner",
                        "Could not start camera",
                        exception
                    )
                }

            }, ContextCompat.getMainExecutor(viewContext))

            previewView
        }
    )
}