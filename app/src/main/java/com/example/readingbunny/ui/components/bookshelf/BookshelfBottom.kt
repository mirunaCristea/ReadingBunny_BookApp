package com.example.readingbunny.ui.components.bookshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.readingbunny.ui.theme.BookshelfColors

@Composable
fun BookshelfBottom(
    colors: BookshelfColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(28.dp)
    ) {

        // very thin bottom rail
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.woodHighlight,
                            colors.wood,
                            colors.woodShadow
                        )
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.Bottom
        ) {

            // left foot
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(20.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colors.wood,
                                colors.woodShadow
                            )
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 6.dp,
                            bottomEnd = 6.dp
                        )
                    )
            )

            Box(
                modifier = Modifier.weight(1f)
            )

            // right foot
            Box(
                modifier = Modifier
                    .width(22.dp)
                    .height(20.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colors.wood,
                                colors.woodShadow
                            )
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 6.dp,
                            bottomEnd = 6.dp
                        )
                    )
            )
        }
    }
}