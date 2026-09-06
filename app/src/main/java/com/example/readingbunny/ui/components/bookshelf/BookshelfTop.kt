package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun BookshelfTop(
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BookshelfTokens.ShelfHeight)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFC58E65),
                        color,
                        Color(0xFF956343)
                    )
                ),
                shape = RoundedCornerShape(
                    topStart = BookshelfTokens.CornerRadius,
                    topEnd = BookshelfTokens.CornerRadius
                )
            )
    )
}