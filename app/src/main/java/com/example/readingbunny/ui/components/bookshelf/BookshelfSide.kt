package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.readingbunny.ui.theme.BookshelfColors

@Composable
fun BookshelfSide(
    colors: BookshelfColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(BookshelfTokens.SideWidth)
            .fillMaxHeight()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        colors.woodOuter,
                        colors.wood,
                        colors.woodInner
                    )
                )
            )
    )
}