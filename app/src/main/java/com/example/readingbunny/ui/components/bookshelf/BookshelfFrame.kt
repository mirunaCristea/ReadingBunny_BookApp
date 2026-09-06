package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun BookshelfFrame(
    shelfCount: Int,
    woodColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    shelfContent: @Composable (Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        BookshelfTop(
            color = woodColor
        )

        repeat(shelfCount) { shelfIndex ->
            ShelfLevel(
                woodColor = woodColor,
                backgroundColor = backgroundColor
            ) {
                shelfContent(shelfIndex)
            }
        }
    }
}