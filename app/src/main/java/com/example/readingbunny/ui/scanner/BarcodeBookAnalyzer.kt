package com.example.readingbunny.ui.scanner

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

    override fun analyze(imageProxy: ImageProxy) {

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

                val barcodeValue =
                    barcodes
                        .firstOrNull { barcode ->
                            barcode.format == Barcode.FORMAT_EAN_13
                        }
                        ?.rawValue

                if (
                    barcodeValue != null &&
                    barcodeValue != lastDetectedValue
                ) {
                    lastDetectedValue = barcodeValue

                    onBarcodeDetected(barcodeValue)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}