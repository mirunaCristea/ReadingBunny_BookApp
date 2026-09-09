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
import com.example.readingbunny.model.JournalEntryType
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.res.stringResource
import com.example.readingbunny.R
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun ReadingSessionScreen(
    book: Book,
    elapsedSeconds: Long,
    isRunning: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onFinish: (Int) -> Unit,
    onCancel: () -> Unit,
    onAddJournalEntry: (
        JournalEntryType,
            String,
            Int?
    ) -> Unit,
) {
    var isFinishing by rememberSaveable {
        mutableStateOf(false)
    }

    var showDiscardDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var quickJournalType by rememberSaveable {
        mutableStateOf<JournalEntryType?>(null)
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
                Text(
                    stringResource(R.string.discard_session_question)
                )
            },
            text = {
                Text(
                    stringResource(R.string.discard_session_warning)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onCancel()
                    }
                ) {
                    Text(
                        stringResource(R.string.discard_session)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                    }
                ) {
                    Text(
                        stringResource(R.string.keep_reading)
                    )
                }
            }
        )
    }

    quickJournalType?.let { journalType ->
        QuickJournalEntryDialog(
            type = journalType,
            totalPages = book.totalPages,

            onDismiss = {
                quickJournalType = null
            },

            onSave = { content, page ->
                onAddJournalEntry(
                    journalType,
                    content,
                    page
                )

                quickJournalType = null
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
                Text(
                    stringResource(R.string.close_action)
                )
            }
        }

        Spacer(
            modifier = Modifier.height(32.dp)
        )

        Text(
            text = book.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = book.author,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                stringResource(R.string.reading_time)
            } else {
                stringResource(R.string.session_paused)
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
                    text = stringResource(R.string.started_at_page),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = stringResource(
                        R.string.page_progress,
                        book.currentPage,
                        book.totalPages
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (!isFinishing) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        quickJournalType = JournalEntryType.NOTE
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        stringResource(R.string.note_action)
                    )
                }

                OutlinedButton(
                    onClick = {
                        quickJournalType = JournalEntryType.QUOTE
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        stringResource(R.string.quote_action)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

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
                        stringResource(R.string.pause_action)
                    } else {
                        stringResource(R.string.resume_action)
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
                Text(
                    stringResource(R.string.stop_session)
                )
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
                        text = stringResource(R.string.where_did_you_stop),
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
                            Text(
                                stringResource(R.string.end_page)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = hasPageError,
                        supportingText = {
                            if (hasPageError) {
                                Text(
                                    text = stringResource(
                                        R.string.page_range_error,
                                        book.currentPage,
                                        book.totalPages
                                    ),
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
                        Text(
                            stringResource(R.string.save_session)
                        )
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
                        Text(
                            stringResource(R.string.continue_reading)
                        )
                    }
                }
            }
        }
    }
}



@Composable
private fun QuickJournalEntryDialog(
    type: JournalEntryType,
    totalPages: Int,
    onDismiss: () -> Unit,
    onSave: (
        content: String,
        page: Int?
    ) -> Unit
) {

    var content by rememberSaveable(type) {
        mutableStateOf("")
    }

    var pageText by rememberSaveable(type) {
        mutableStateOf("")
    }


    val page =
        pageText.toIntOrNull()


    val hasPageError =
        pageText.isNotBlank() &&
                (
                        page == null ||
                                page <= 0 ||
                                page > totalPages
                        )


    val title =
        when (type) {
            JournalEntryType.NOTE ->
                stringResource(R.string.add_note)

            JournalEntryType.QUOTE ->
                stringResource(R.string.add_quote)
        }


    val contentLabel =
        when (type) {
            JournalEntryType.NOTE ->
                stringResource(R.string.your_note)

            JournalEntryType.QUOTE ->
                stringResource(R.string.quote_label)
        }


    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(title)
        },

        text = {

            Column {

                OutlinedTextField(
                    value = content,

                    onValueChange = { newValue ->
                        content = newValue
                    },

                    label = {
                        Text(contentLabel)
                    },

                    minLines = 3,

                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                OutlinedTextField(
                    value = pageText,

                    onValueChange = { newValue ->

                        if (
                            newValue.all { character ->
                                character.isDigit()
                            }
                        ) {
                            pageText = newValue
                        }
                    },

                    label = {
                        Text(
                            stringResource(R.string.page_optional)
                        )
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    isError = hasPageError,

                    supportingText = {

                        if (hasPageError) {

                            Text(
                                text =
                                    stringResource(
                                        R.string.journal_page_range_error,
                                        totalPages
                                    )
                            )
                        }
                    },

                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {

                    onSave(
                        content.trim(),
                        page
                    )
                },

                enabled =
                    content.isNotBlank() &&
                            !hasPageError
            ) {
                Text(
                    stringResource(R.string.save_action)
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    stringResource(R.string.cancel_action)
                )
            }
        }
    )
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