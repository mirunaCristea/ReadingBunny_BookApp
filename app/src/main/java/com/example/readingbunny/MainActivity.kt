package com.example.readingbunny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.readingbunny.ui.screens.ReadingBunnyApp
import com.example.readingbunny.ui.theme.ReadingBunnyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ReadingBunnyTheme {
                ReadingBunnyApp()
            }
        }
    }
}