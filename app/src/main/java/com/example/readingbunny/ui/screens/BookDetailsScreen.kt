package com.example.readingbunny.ui.screens

import android.graphics.drawable.ColorDrawable
import android.view.WindowManager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

import coil3.compose.AsyncImage

import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookOwnership
import com.example.readingbunny.model.JournalEntryType
import com.example.readingbunny.model.ReadingJournalEntry
import com.example.readingbunny.model.ReadingStatus

import com.example.readingbunny.ui.theme.DarkBrown
import com.example.readingbunny.ui.theme.Terracotta


private val PageBackground = Color(0xFFFFF9F3)
private val CreamCard = Color(0xFFFFF1E6)
private val LightCream = Color(0xFFFFF7F0)
private val MutedBrown = Color(0xFF78645D)
private val SoftBorder = Color(0xFFE9D8CC)
private val SoftSage = Color(0xFF94A98B)
private val DangerRed = Color(0xFFB64A45)


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
    onDeleteJournalEntry: (ReadingJournalEntry) -> Unit,
) {
    var isEditSectionVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {

            BookDetailsHeader(
                onBackClick = onBackClick,
                onSettingsClick = {
                    isEditSectionVisible = true
                }
            )

            BookHeader(
                coverUrl = book.coverUrl,
                title = book.title,
                author = book.author,
                status = book.status,
                ownership = book.ownership,
                isbn = book.isbn
            )

            BookInfoSection(
                currentPage = book.currentPage,
                totalPages = book.totalPages,
                description = book.description,
            )

            ReadingJournalSection(
                bookId = book.id,
                journalEntries = journalEntries,
                totalPages = book.totalPages,
                onAddJournalEntry = onAddJournalEntry,
                onDeleteJournalEntry = onDeleteJournalEntry
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            OutlinedButton(
                onClick = {
                    onDeleteBook(book)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DangerRed
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Delete Book",
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        if (isEditSectionVisible) {
            BookSettingDialog(
                bookId = book.id,
                coverUrl = book.largeCoverUrl ?: book.coverUrl,
                title = book.title,
                author = book.author,
                status = book.status,
                ownership = book.ownership,
                currentPage = book.currentPage,
                totalPages = book.totalPages,

                onDismiss = {
                    isEditSectionVisible = false
                },

                onTitleChange = { newTitle ->
                    onUpdateBook(
                        book.copy(title = newTitle)
                    )
                },

                onAuthorChange = { newAuthor ->
                    onUpdateBook(
                        book.copy(author = newAuthor)
                    )
                },

                onStatusChange = { newStatus ->
                    onUpdateBook(
                        book.copy(status = newStatus)
                    )
                },

                onOwnershipChange = { newOwnership ->
                    onUpdateBook(
                        book.copy(ownership = newOwnership)
                    )
                },

                onCurrentPageChange = { newCurrentPage ->
                    onUpdateBook(
                        book.copy(currentPage = newCurrentPage)
                    )
                },

                onTotalPagesChange = { newTotalPages ->
                    onUpdateBook(
                        book.copy(totalPages = newTotalPages)
                    )
                }
            )
        }
    }
}


@Composable
private fun BookDetailsHeader(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Surface(
            shape = CircleShape,
            color = CreamCard,
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkBrown,
                )
            }
        }

        Text(
            text = "Book Details",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown,
        )

        Surface(
            shape = CircleShape,
            color = CreamCard,
        ) {
            IconButton(
                onClick = onSettingsClick
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Edit Book",
                    tint = Terracotta,
                )
            }
        }
    }
}


@Composable
private fun BookHeader(
    coverUrl: String?,
    title: String,
    author: String,
    status: ReadingStatus,
    ownership: BookOwnership,
    isbn: String?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = CreamCard
        ),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Box(
                    modifier = Modifier
                        .size(
                            width = 110.dp,
                            height = 160.dp
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .background(Terracotta),
                    contentAlignment = Alignment.Center,
                ) {

                    if (!coverUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = coverUrl,
                            contentDescription = "Book cover of $title",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(42.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {

                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = author,
                        fontSize = 15.sp,
                        color = MutedBrown,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    BookInfoPill(
                        text = status.displayName(),
                        backgroundColor = Terracotta.copy(alpha = 0.14f),
                        textColor = Terracotta,
                    )

                    BookInfoPill(
                        text = ownership.displayName(),
                        backgroundColor = SoftSage.copy(alpha = 0.20f),
                        textColor = Color(0xFF66785F),
                    )
                }
            }

            if (!isbn.isNullOrBlank()) {
                Text(
                    text = "ISBN  •  $isbn",
                    fontSize = 13.sp,
                    color = MutedBrown,
                )
            }
        }
    }
}


@Composable
private fun BookInfoPill(
    text: String,
    backgroundColor: Color,
    textColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = backgroundColor,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


@Composable
private fun BookInfoSection(
    currentPage: Int,
    totalPages: Int,
    description: String? = null,
) {
    val progress =
        if (totalPages > 0) {
            (
                currentPage.toFloat() /
                    totalPages.toFloat()
                ).coerceIn(0f, 1f)
        } else {
            0f
        }

    val progressPercentage =
        (progress * 100).toInt()

    var isDescriptionExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            Text(
                text = "Reading Progress",
                color = DarkBrown,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Column {
                    Text(
                        text = "$currentPage / $totalPages pages",
                        color = DarkBrown,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(
                        text = "Keep going ✨",
                        color = MutedBrown,
                        fontSize = 12.sp,
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Terracotta.copy(alpha = 0.12f),
                ) {
                    Text(
                        text = "$progressPercentage%",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        color = Terracotta,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Terracotta,
                trackColor = Terracotta.copy(alpha = 0.13f),
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "About this book",
                color = DarkBrown,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            if (description.isNullOrBlank()) {

                Text(
                    text = "No description available.",
                    color = MutedBrown,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                )

            } else {

                Text(
                    text = description,
                    color = MutedBrown,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    maxLines =
                        if (isDescriptionExpanded) {
                            Int.MAX_VALUE
                        } else {
                            4
                        },
                    overflow = TextOverflow.Ellipsis,
                )

                TextButton(
                    onClick = {
                        isDescriptionExpanded =
                            !isDescriptionExpanded
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text =
                            if (isDescriptionExpanded) {
                                "Show less"
                            } else {
                                "Show more"
                            },
                        color = Terracotta,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}


@Composable
private fun ReadingJournalSection(
    bookId: Int,
    journalEntries: List<ReadingJournalEntry>,
    totalPages: Int,
    onAddJournalEntry: (
        JournalEntryType,
        String,
        Int?
    ) -> Unit,
    onDeleteJournalEntry: (ReadingJournalEntry) -> Unit,
) {
    var journalText by rememberSaveable(bookId) {
        mutableStateOf("")
    }

    var journalPageText by rememberSaveable(bookId) {
        mutableStateOf("")
    }

    var selectedJournalType by rememberSaveable(bookId) {
        mutableStateOf(JournalEntryType.NOTE)
    }

    val journalPage =
        journalPageText.toIntOrNull()

    val isJournalPageValid =
        journalPageText.isBlank() ||
            (
                journalPage != null &&
                    journalPage > 0 &&
                    journalPage <= totalPages
                )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Text(
            text = "Reading Journal",
            color = DarkBrown,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Save thoughts, favourite quotes and little moments from your reading.",
            color = MutedBrown,
            fontSize = 13.sp,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = CreamCard
            ),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {

                    JournalTypeButton(
                        text = "📝 Note",
                        selected =
                            selectedJournalType ==
                                JournalEntryType.NOTE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedJournalType =
                                JournalEntryType.NOTE
                        }
                    )

                    JournalTypeButton(
                        text = "💬 Quote",
                        selected =
                            selectedJournalType ==
                                JournalEntryType.QUOTE,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedJournalType =
                                JournalEntryType.QUOTE
                        }
                    )
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
                                "Favourite quote"
                            } else {
                                "Your note"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            if (
                                selectedJournalType ==
                                JournalEntryType.QUOTE
                            ) {
                                "Write the quote here..."
                            } else {
                                "What are you thinking?"
                            }
                        )
                    },
                    minLines = 3,
                    shape = RoundedCornerShape(14.dp),
                )

                OutlinedTextField(
                    value = journalPageText,
                    onValueChange = { value ->

                        if (
                            value.all {
                                character ->
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
                    singleLine = true,
                    isError = !isJournalPageValid,
                    supportingText = {
                        if (!isJournalPageValid) {
                            Text(
                                text = "Page must be between 1 and $totalPages."
                            )
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
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
                    modifier = Modifier.fillMaxWidth(),
                    enabled =
                        journalText.isNotBlank() &&
                            isJournalPageValid,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta
                    )
                ) {
                    Text(
                        text = "Save to Journal",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        if (journalEntries.isNotEmpty()) {

            Text(
                text = "Your entries",
                color = DarkBrown,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            journalEntries.forEach { entry ->

                JournalEntryCard(
                    entry = entry,
                    onDelete = onDeleteJournalEntry
                )
            }
        }
    }
}


@Composable
private fun JournalTypeButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor =
                if (selected) {
                    Terracotta
                } else {
                    LightCream
                },
            contentColor =
                if (selected) {
                    Color.White
                } else {
                    DarkBrown
                }
        ),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


@Composable
private fun JournalEntryCard(
    entry: ReadingJournalEntry,
    onDelete: (ReadingJournalEntry) -> Unit,
) {
    var showDeleteConfirmation by rememberSaveable {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                if (entry.type == JournalEntryType.QUOTE) {
                    Color(0xFFF7EFE4)
                } else {
                    Color.White
                }
        ),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text =
                        when (entry.type) {
                            JournalEntryType.NOTE ->
                                "📝 Note"

                            JournalEntryType.QUOTE ->
                                "💬 Quote"
                        },
                    color = DarkBrown,
                    fontWeight = FontWeight.Bold,
                )

                entry.page?.let { page ->

                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Terracotta.copy(alpha = 0.10f),
                    ) {
                        Text(
                            text = "Page $page",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            color = Terracotta,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Text(
                text = entry.content,
                color = DarkBrown.copy(alpha = 0.86f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontStyle =
                    if (
                        entry.type ==
                        JournalEntryType.QUOTE
                    ) {
                        FontStyle.Italic
                    } else {
                        FontStyle.Normal
                    },
            )

            TextButton(
                onClick = {
                    showDeleteConfirmation = true
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Delete",
                    color = DangerRed,
                )
            }
        }
    }

    if (showDeleteConfirmation) {

        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },

            title = {
                Text(
                    text = "Delete journal entry?",
                    color = DarkBrown,
                )
            },

            text = {
                Text(
                    text = "This action cannot be undone.",
                    color = MutedBrown,
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(entry)
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = DangerRed,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = DarkBrown,
                    )
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookSettingDialog(
    bookId: Int,
    coverUrl: String?,
    title: String,
    author: String,
    status: ReadingStatus,
    ownership: BookOwnership,
    currentPage: Int,
    totalPages: Int,
    onDismiss: () -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onAuthorChange: (String) -> Unit = {},
    onStatusChange: (ReadingStatus) -> Unit = {},
    onOwnershipChange: (BookOwnership) -> Unit = {},
    onCurrentPageChange: (Int) -> Unit = {},
    onTotalPagesChange: (Int) -> Unit = {},
) {
    var currentTitleText by rememberSaveable(bookId) {
        mutableStateOf(title)
    }

    var currentAuthorText by rememberSaveable(bookId) {
        mutableStateOf(author)
    }

    var currentPageText by rememberSaveable(bookId) {
        mutableStateOf(currentPage.toString())
    }

    var totalPagesText by rememberSaveable(bookId) {
        mutableStateOf(totalPages.toString())
    }

    var selectedStatus by rememberSaveable(bookId) {
        mutableStateOf(status)
    }

    var selectedOwnership by rememberSaveable(bookId) {
        mutableStateOf(ownership)
    }

    var isStatusMenuExpanded by rememberSaveable(bookId) {
        mutableStateOf(false)
    }

    var isOwnershipMenuExpanded by rememberSaveable(bookId) {
        mutableStateOf(false)
    }

    BookBackdropDialog(
        coverUrl = coverUrl,
        onDismiss = onDismiss,
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFBF7)
            ),
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Edit Book",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkBrown,
                        )

                        Text(
                            text = "Changes are saved automatically",
                            fontSize = 12.sp,
                            color = MutedBrown,
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = CreamCard,
                    ) {
                        IconButton(
                            onClick = onDismiss
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = DarkBrown,
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = "Book information",
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                )

                OutlinedTextField(
                    value = currentTitleText,
                    onValueChange = { newTitle ->

                        currentTitleText = newTitle

                        if (newTitle.isNotBlank()) {
                            onTitleChange(newTitle)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Title")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                )

                OutlinedTextField(
                    value = currentAuthorText,
                    onValueChange = { newAuthor ->

                        currentAuthorText = newAuthor

                        if (newAuthor.isNotBlank()) {
                            onAuthorChange(newAuthor)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Author")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                )

                Text(
                    text = "Reading",
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                )

                ExposedDropdownMenuBox(
                    expanded = isStatusMenuExpanded,
                    onExpandedChange = {
                        isStatusMenuExpanded =
                            !isStatusMenuExpanded
                    },
                ) {

                    OutlinedTextField(
                        value = selectedStatus.displayName(),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type =
                                    ExposedDropdownMenuAnchorType
                                        .PrimaryNotEditable,
                                enabled = true,
                            ),
                        label = {
                            Text("Reading Status")
                        },
                        readOnly = true,
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        isStatusMenuExpanded
                                )
                        },
                    )

                    ExposedDropdownMenu(
                        expanded = isStatusMenuExpanded,
                        onDismissRequest = {
                            isStatusMenuExpanded = false
                        },
                    ) {

                        ReadingStatus.entries.forEach {
                            statusOption ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        statusOption
                                            .displayName()
                                    )
                                },
                                onClick = {
                                    selectedStatus =
                                        statusOption

                                    onStatusChange(
                                        statusOption
                                    )

                                    isStatusMenuExpanded =
                                        false
                                }
                            )
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = isOwnershipMenuExpanded,
                    onExpandedChange = {
                        isOwnershipMenuExpanded =
                            !isOwnershipMenuExpanded
                    },
                ) {

                    OutlinedTextField(
                        value =
                            selectedOwnership
                                .displayName(),
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type =
                                    ExposedDropdownMenuAnchorType
                                        .PrimaryNotEditable,
                                enabled = true,
                            ),
                        label = {
                            Text("Ownership")
                        },
                        readOnly = true,
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        isOwnershipMenuExpanded
                                )
                        },
                    )

                    ExposedDropdownMenu(
                        expanded =
                            isOwnershipMenuExpanded,
                        onDismissRequest = {
                            isOwnershipMenuExpanded =
                                false
                        },
                    ) {

                        BookOwnership.entries.forEach {
                            ownershipOption ->

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        ownershipOption
                                            .displayName()
                                    )
                                },
                                onClick = {

                                    selectedOwnership =
                                        ownershipOption

                                    onOwnershipChange(
                                        ownershipOption
                                    )

                                    isOwnershipMenuExpanded =
                                        false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Pages",
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp),
                ) {

                    OutlinedTextField(
                        value = currentPageText,
                        onValueChange = { value ->

                            if (
                                value.all {
                                    character ->
                                    character.isDigit()
                                }
                            ) {
                                currentPageText = value

                                val newCurrentPage =
                                    value.toIntOrNull()

                                if (
                                    newCurrentPage != null &&
                                    newCurrentPage >= 0 &&
                                    newCurrentPage <=
                                    totalPages
                                ) {
                                    onCurrentPageChange(
                                        newCurrentPage
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        label = {
                            Text("Current")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                    )

                    OutlinedTextField(
                        value = totalPagesText,
                        onValueChange = { value ->

                            if (
                                value.all {
                                    character ->
                                    character.isDigit()
                                }
                            ) {
                                totalPagesText = value

                                val newTotalPages =
                                    value.toIntOrNull()

                                if (
                                    newTotalPages != null &&
                                    newTotalPages > 0 &&
                                    newTotalPages >=
                                    currentPage
                                ) {
                                    onTotalPagesChange(
                                        newTotalPages
                                    )
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        label = {
                            Text("Total")
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                    )
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta
                    )
                ) {
                    Text(
                        text = "Done",
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}


@Composable
private fun BookBackdropDialog(
    coverUrl: String?,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        )
    ) {

        val dialogWindow =
            (
                LocalView.current.parent
                    as? DialogWindowProvider
                )?.window

        SideEffect {

            dialogWindow?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            dialogWindow?.setBackgroundDrawable(
                ColorDrawable(
                    android.graphics.Color.TRANSPARENT
                )
            )

            dialogWindow?.setDimAmount(0f)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {

            if (!coverUrl.isNullOrBlank()) {

                AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.82f),
                    contentScale = ContentScale.Crop,
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.14f)
                        )
                )
            }

            content()
        }
    }
}


private fun ReadingStatus.displayName(): String {
    return when (this) {
        ReadingStatus.READING ->
            "Currently Reading"

        ReadingStatus.WANT_TO_READ ->
            "Want to Read"

        ReadingStatus.FINISHED ->
            "Finished"

        ReadingStatus.DNF ->
            "Did Not Finish"

        ReadingStatus.UNREAD ->
            "Unread"
    }
}


private fun BookOwnership.displayName(): String {
    return when (this) {
        BookOwnership.OWNED ->
            "Owned"

        BookOwnership.BORROWED ->
            "Borrowed"

        BookOwnership.WISHLIST ->
            "Wishlist"
    }
}