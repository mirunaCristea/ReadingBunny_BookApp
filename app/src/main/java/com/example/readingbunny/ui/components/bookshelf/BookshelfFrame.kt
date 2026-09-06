package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.readingbunny.ui.theme.BookshelfColors

@Composable
fun BookshelfFrame(
    shelfCount: Int,
    colors: BookshelfColors,
    modifier: Modifier = Modifier,
    shelfContent: @Composable (Int) -> Unit
) {
    Column(
        modifier = modifier
    ) {

        BookshelfTop(
            colors = colors
        )

        repeat(shelfCount) { shelfIndex ->

            ShelfLevel(
                colors = colors
            ) {
                shelfContent(shelfIndex)
            }
        }
    }
}