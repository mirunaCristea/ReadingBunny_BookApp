package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.readingbunny.ui.theme.BookshelfColors

@Composable
fun ShelfLevel(
    colors: BookshelfColors,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BookshelfTokens.LevelHeight)
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.00f to colors.innerShelfShadow,
                        0.08f to colors.innerShelfShadow,
                        0.22f to colors.shelfBackground,
                        1.00f to colors.shelfBackground
                    )
                )
            )
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            BookshelfSide(
                colors = colors
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                content()
            }

            BookshelfSide(
                colors = colors
            )
        }

        ShelfPlank(
            colors = colors,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}