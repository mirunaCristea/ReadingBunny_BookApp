package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BookshelfSide(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(BookshelfTokens.SideWidth)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF9B6948),
                        color,
                        Color(0xFFB07A54)
                    )
                )
            )
    )
}