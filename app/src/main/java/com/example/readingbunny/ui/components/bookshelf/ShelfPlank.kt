package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShelfPlank(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp)
    ) {

        // top surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
                .background(
                    Color(0xFFC58E65)
                )
        )

        // front face
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .align(Alignment.Center)
                .background(color)
        )

        // darker lower edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Color(0xFF8E6042)
                )
        )
    }
}