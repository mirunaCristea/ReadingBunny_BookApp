package com.example.readingbunny.ui.scanner

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeBookAnalyzer(
    private val scanner: BarcodeScanner,
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastDetectedValue: String? = null
    private var frameCounter = 0

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        frameCounter++


        Log.d(
            "BarcodeAnalyzer",
            "Frame received: $frameCounter"
        )

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->

                barcodes.forEach { barcode ->

                    Log.d(
                        "BarcodeAnalyzer",
                        "Detected format=${barcode.format}, value=${barcode.rawValue}"
                    )
                }

                val isbn =
                    barcodes
                        .mapNotNull { barcode ->
                            barcode.rawValue
                                ?.filter { character ->
                                    character.isDigit()
                                }
                        }
                        .firstOrNull { value ->
                            value.length == 13 &&
                                    (
                                            value.startsWith("978") ||
                                                    value.startsWith("979")
                                            )
                        }

                if (
                    isbn != null &&
                    isbn != lastDetectedValue
                ) {

                    lastDetectedValue = isbn

                    Log.d(
                        "BarcodeAnalyzer",
                        "ISBN FOUND: $isbn"
                    )

                    onBarcodeDetected(isbn)
                }
            }

            .addOnFailureListener { exception ->

                Log.e(
                    "BarcodeAnalyzer",
                    "ML Kit barcode analysis failed",
                    exception
                )
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}