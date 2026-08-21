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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.readingbunny.model.Book
import androidx.compose.material3.AlertDialog

@Composable
fun ReadingSessionScreen(
    book: Book,
    elapsedSeconds: Long,
    isRunning: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: (Int) -> Unit,
    onCancel: () -> Unit
) {
    var isFinishing by rememberSaveable {
        mutableStateOf(false)
    }

    var showDiscardDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var endPageText by rememberSaveable {
        mutableStateOf(book.currentPage.toString())
    }

    val endPage =
        endPageText.toIntOrNull()

    val hasPageError =
        endPageText.isNotBlank() &&
                (
                        endPage == null ||
                                endPage < book.currentPage ||
                                endPage > book.totalPages
                        )

    val formattedTime =
        formatReadingTime(elapsedSeconds)


    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = {
                showDiscardDialog = false
            },
            title = {
                Text("Discard session?")
            },
            text = {
                Text("If you close now, this reading session will be lost.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onCancel()
                    }
                ) {
                    Text("Discard session")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                    }
                ) {
                    Text("Keep reading")
                }
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = {
                    showDiscardDialog = true
                }
            ) {
                Text("Close")
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = book.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = book.author,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(48.dp)
        )

        Text(
            text = formattedTime,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = if (isRunning) {
                "Reading time"
            } else {
                "Session paused"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Started at page",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${book.currentPage} / ${book.totalPages}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        if (!isFinishing) {
            Button(
                onClick = {
                    if (isRunning) {
                        onPause()
                    } else {
                        onResume()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isRunning) {
                        "Pause"
                    } else {
                        "Resume"
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    if (isRunning) {
                        onPause()
                    }

                    endPageText =
                        book.currentPage.toString()

                    isFinishing = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Stop session")
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Where did you stop?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    OutlinedTextField(
                        value = endPageText,
                        onValueChange = { newValue ->
                            if (newValue.all { character ->
                                    character.isDigit()
                                }
                            ) {
                                endPageText = newValue
                            }
                        },
                        label = {
                            Text("End page")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = hasPageError,
                        supportingText = {
                            if (hasPageError) {
                                Text(
                                    text = "Page must be between " +
                                            "${book.currentPage} and ${book.totalPages}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            val page =
                                endPage ?: return@Button

                            if (
                                page >= book.currentPage &&
                                page <= book.totalPages
                            ) {
                                onFinish(page)
                            }
                        },
                        enabled =
                            endPage != null &&
                                    !hasPageError,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save session")
                    }

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextButton(
                        onClick = {
                            isFinishing = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue reading")
                    }
                }
            }
        }
    }
}

private fun formatReadingTime(
    totalSeconds: Long
): String {
    val hours =
        totalSeconds / 3600

    val minutes =
        (totalSeconds % 3600) / 60

    val seconds =
        totalSeconds % 60

    return String.format(
        "%02d:%02d:%02d",
        hours,
        minutes,
        seconds
    )
}