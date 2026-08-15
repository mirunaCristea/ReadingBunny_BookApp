package com.example.readingbunny.ui.scanner

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import java.util.concurrent.Executor

class SpineTextRecognizer(
    private val recognizer: TextRecognizer,
    private val callbackExecutor: Executor
) {

    @OptIn(ExperimentalGetImage::class)
    fun recognize(
        imageProxy: ImageProxy,
        onTextRecognized: (String) -> Unit
    ) {

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        recognizer.process(inputImage)
            .addOnSuccessListener(callbackExecutor) { result ->

                Log.d(
                    "SpineScanner",
                    "Recognized text: ${result.text}"
                )

                onTextRecognized(result.text)
            }
            .addOnFailureListener(callbackExecutor) { exception ->

                Log.e(
                    "SpineScanner",
                    "Text recognition failed",
                    exception
                )
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun close() {
        recognizer.close()
    }
}