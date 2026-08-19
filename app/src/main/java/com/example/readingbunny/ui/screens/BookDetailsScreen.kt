package com.example.readingbunny.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.JournalEntryType
import com.example.readingbunny.model.ReadingJournalEntry
import com.example.readingbunny.model.ReadingStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    book: Book,
    journalEntries: List<ReadingJournalEntry>,
    onBackClick: () -> Unit,
    onUpdateBook: (Book) -> Unit,
    onDeleteBook: (Book) -> Unit,
    onAddJournalEntry: (
        JournalEntryType,
        String,
        Int?
    ) -> Unit,
    onDeleteJournalEntry:
        (ReadingJournalEntry) -> Unit
){
    var currentPageText by rememberSaveable(book.id) {
        mutableStateOf(book.currentPage.toString())
    }

    var selectedStatus by rememberSaveable(book.id) {
        mutableStateOf(book.status)
    }

    val newPage = currentPageText.toIntOrNull()

    val isPageValid =
        newPage != null &&
                newPage >= 0 &&
                newPage <= book.totalPages


    var isStatusMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var journalText by rememberSaveable(book.id) {
        mutableStateOf("")
    }

    var journalPageText by rememberSaveable(book.id) {
        mutableStateOf("")
    }

    var selectedJournalType by rememberSaveable(
        book.id
    ) {
        mutableStateOf(JournalEntryType.NOTE)
    }

    val journalPage =
        journalPageText
            .toIntOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "Book Details"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = book.title
        )

        Text(
            text = book.author
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth()

        ){
            Text("Progress")
        }

        OutlinedTextField(
            value = currentPageText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    currentPageText = newValue
                }
            },
            label = {
                Text("Current page")
            },
            modifier = Modifier.fillMaxWidth(),
            isError = !isPageValid
        )

        Text(
            text = "of ${book.totalPages} pages"
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = isStatusMenuExpanded,
            onExpandedChange = {
                isStatusMenuExpanded = !isStatusMenuExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedStatus.displayName(),
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Reading status")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isStatusMenuExpanded
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
            )

            ExposedDropdownMenu(
                expanded = isStatusMenuExpanded,
                onDismissRequest = {
                    isStatusMenuExpanded = false
                }
            ) {
                ReadingStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Text(status.displayName())
                        },
                        onClick = {
                            selectedStatus = status
                            isStatusMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                val page = newPage?: return@Button

                val updatedBook = book.copy(
                    currentPage = page,
                    status = selectedStatus
                )

                onUpdateBook(updatedBook)

            },

            enabled = isPageValid
        ) {
            Text("Save Progress")
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        Text(
            text = "Reading Journal"
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    selectedJournalType =
                        JournalEntryType.NOTE
                }
            ) {
                Text("📝 Note")
            }

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Button(
                onClick = {
                    selectedJournalType =
                        JournalEntryType.QUOTE
                }
            ) {
                Text("💬 Quote")
            }
        }

        OutlinedTextField(
            value = journalText,
            onValueChange = {
                journalText = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {

                Text(
                    if (
                        selectedJournalType ==
                        JournalEntryType.QUOTE
                    ) {
                        "Quote"
                    } else {
                        "Note"
                    }
                )
            },
            minLines = 3
        )

        OutlinedTextField(
            value = journalPageText,
            onValueChange = { value ->

                if (
                    value.all { character ->
                        character.isDigit()
                    }
                ) {
                    journalPageText = value
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Page (optional)")
            },
            singleLine = true
        )

        Button(
            onClick = {

                onAddJournalEntry(
                    selectedJournalType,
                    journalText,
                    journalPage
                )

                journalText = ""
                journalPageText = ""
            },
            enabled =
                journalText.isNotBlank()
        ) {

            Text("Save to journal")
        }

        journalEntries.forEach { entry ->

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Text(
                    text =
                        when (entry.type) {
                            JournalEntryType.NOTE ->
                                "📝 Note"

                            JournalEntryType.QUOTE ->
                                "💬 Quote"
                        }
                )

                entry.page?.let { page ->

                    Text(
                        text = "Page $page"
                    )
                }

                Text(
                    text = entry.content
                )

                TextButton(
                    onClick = {
                        onDeleteJournalEntry(entry)
                    }
                ) {
                    Text("Delete")
                }
            }
        }

        Button(
            onClick = {
                onDeleteBook(book)
            }
        ) {
            Text("Delete Book")
        }

    }

}

private fun ReadingStatus.displayName(): String {
    return when (this) {
        ReadingStatus.READING -> "Currently Reading"
        ReadingStatus.WANT_TO_READ -> "Want to Read"
        ReadingStatus.FINISHED -> "Finished"
        ReadingStatus.DNF -> "Did Not Finish"
        ReadingStatus.UNREAD -> "Unread"
    }
}