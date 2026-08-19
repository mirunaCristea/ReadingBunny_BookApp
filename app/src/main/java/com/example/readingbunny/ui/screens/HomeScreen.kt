package com.example.readingbunny.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.readingbunny.model.Book
import com.example.readingbunny.ui.theme.DarkBrown
import com.example.readingbunny.ui.theme.SoftCream
import com.example.readingbunny.ui.theme.SoftSage
import com.example.readingbunny.ui.theme.Terracotta
import com.example.readingbunny.ui.theme.WarmCream


@Composable
fun HomeScreen(
    book: Book?,
    onStartReading: (Book) -> Unit,
    todayReadingSeconds: Long,
    dailyGoalMinutes: Int,
) {

    val todayReadingMinutes =
        todayReadingSeconds / 60

    val goalProgress =
        if (dailyGoalMinutes > 0) {
            (todayReadingMinutes.toFloat() / dailyGoalMinutes)
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    val remainingMinutes =
        maxOf(
            0L,
            dailyGoalMinutes.toLong() - todayReadingMinutes
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .safeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ReadingBunny",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown
            )

            Surface(
                color = SoftSage,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "7 zile 🔥",
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 8.dp
                    ),
                    fontWeight = FontWeight.Medium,
                    color = DarkBrown
                )
            }
        }

        Text(
            text = "Un colț liniștit pentru fiecare poveste.",
            color = DarkBrown.copy(alpha = 0.7f)
        )

        Text(
            text = "Citesc acum",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = DarkBrown
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SoftCream)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 84.dp, height = 124.dp)
                        .background(
                            color = Terracotta,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if(!book?.coverUrl.isNullOrBlank())
                    {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription =  "Cover ${book.title}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                }

                val progress = if (book != null && book.totalPages > 0) {
                    book.currentPage.toFloat() / book.totalPages.toFloat()
                } else {
                    0f
                }
                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book?.title ?: "No book in progress",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )

                    Text(
                        text = book?.author ?: "Choose a book to start reading",
                        color = DarkBrown.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = Terracotta,
                        trackColor = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (book != null ) {
                            "${book.currentPage} din ${book.totalPages} pages · ${(progress * 100).toInt()} % "
                            } else {
                            "0% progress"
                        },
                        fontSize = 12.sp,
                        color = DarkBrown.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            book?.let {
                                onStartReading(it)
                             }
                        },
                        enabled = book != null,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Terracotta
                        )
                    ) {
                        Text("Start reading")
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = SoftCream
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Today's goal",
                        fontWeight = FontWeight.SemiBold,
                        color = DarkBrown
                    )

                    Text(
                        text = "$todayReadingMinutes / $dailyGoalMinutes min",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = DarkBrown
                    )
                }

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                LinearProgressIndicator(
                    progress = { goalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Terracotta,
                    trackColor = Color.White
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                Text(
                    text = if (remainingMinutes > 0) {
                        "$remainingMinutes min remaining"
                    } else {
                        "Daily goal completed! 📚"
                    },
                    fontSize = 13.sp,
                    color = DarkBrown.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 412,
    heightDp = 915
)

@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        book = null,
        onStartReading = {},
        todayReadingSeconds = 0L,
        dailyGoalMinutes = 30
    )
}