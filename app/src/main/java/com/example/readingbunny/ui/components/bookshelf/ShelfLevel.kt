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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShelfLevel(
    woodColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BookshelfTokens.LevelHeight)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = 0.90f),
                        backgroundColor,
                        backgroundColor
                    )
                )
                )
    ) {

        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            BookshelfSide(
                color = woodColor
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                content()
            }

            BookshelfSide(
                color = woodColor
            )
        }

        ShelfPlank(
            color = woodColor,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Color.Black.copy(alpha = 0.06f)
                )
        )
    }
}