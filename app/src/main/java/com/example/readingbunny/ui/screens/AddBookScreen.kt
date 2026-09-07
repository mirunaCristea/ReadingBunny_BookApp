package com.example.readingbunny.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.readingbunny.R
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookOwnership
import com.example.readingbunny.model.BookSearchResult
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.ui.scanner.BookScannerMode
import com.example.readingbunny.ui.scanner.BookScannerScreen
import com.example.readingbunny.ui.viewmodel.BookSearchViewModel

private enum class AddBookMethod {
    SEARCH,
    SCAN,
    MANUAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    onBackClick: () -> Unit,
    onSaveBook: (Book) -> Unit,
    bookSearchViewModel: BookSearchViewModel
) {
    val searchUiState by
    bookSearchViewModel.uiState.collectAsStateWithLifecycle()

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var selectedMethod by rememberSaveable {
        mutableStateOf<AddBookMethod?>(null)
    }

    val handleBack: () -> Unit = {
        if (selectedMethod == null) {
            onBackClick()
        } else {
            selectedMethod = null
        }
    }

    BackHandler(
        onBack = handleBack
    )

    var scannerMode by rememberSaveable {
        mutableStateOf(BookScannerMode.BARCODE)
    }

    var bookTitle by rememberSaveable {
        mutableStateOf("")
    }

    var bookAuthor by rememberSaveable {
        mutableStateOf("")
    }

    var bookTotalPages by rememberSaveable {
        mutableStateOf("")
    }

    var selectedIsbn by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var selectedCoverUrl by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var selectedLargeCoverUrl by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var selectedDescription by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val totalPages =
        bookTotalPages.toIntOrNull()

    val hasPagesError =
        bookTotalPages.isNotBlank() &&
                (
                        totalPages == null ||
                                totalPages <= 0
                        )

    var selectedStatus by rememberSaveable {
        mutableStateOf<ReadingStatus?>(null)
    }

    var selectedOwnership by rememberSaveable {
        mutableStateOf<BookOwnership?>(null)
    }

    var isOwnershipMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    var isStatusMenuExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    val isFormValid =
        bookTitle.isNotBlank() &&
                bookAuthor.isNotBlank() &&
                totalPages != null &&
                totalPages > 0 &&
                selectedStatus != null &&
                selectedOwnership != null

    var scannerMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val scannerTextError =
        stringResource(R.string.scanner_text_error)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = handleBack
            ) {
                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription =
                        stringResource(R.string.go_back)
                )
            }

            Text(
                text = when (selectedMethod) {
                    AddBookMethod.SEARCH ->
                        stringResource(
                            R.string.search_for_book_title
                        )

                    AddBookMethod.SCAN ->
                        stringResource(
                            R.string.scan_book_title
                        )

                    AddBookMethod.MANUAL ->
                        stringResource(
                            R.string.add_manually_title
                        )

                    null ->
                        stringResource(
                            R.string.add_book_title
                        )
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (selectedMethod == null) {
            Text(
                text = stringResource(
                    R.string.choose_add_book_method
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            AddBookOptionCard(
                number = "01",
                title = stringResource(
                    R.string.search_online
                ),
                description = stringResource(
                    R.string.search_online_description
                ),
                onClick = {
                    selectedMethod =
                        AddBookMethod.SEARCH
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AddBookOptionCard(
                number = "02",
                title = stringResource(
                    R.string.scan_book
                ),
                description = stringResource(
                    R.string.scan_book_description
                ),
                onClick = {
                    selectedMethod =
                        AddBookMethod.SCAN
                }
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            AddBookOptionCard(
                number = "03",
                title = stringResource(
                    R.string.add_manually
                ),
                description = stringResource(
                    R.string.add_manually_description
                ),
                onClick = {
                    selectedMethod =
                        AddBookMethod.MANUAL
                }
            )
        } else {
            when (selectedMethod) {
                AddBookMethod.SEARCH -> {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { newValue ->
                                searchQuery = newValue
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            label = {
                                Text(
                                    stringResource(
                                        R.string.search_by_title_or_author
                                    )
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector =
                                        Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        bookSearchViewModel
                                            .searchBooks(
                                                searchQuery
                                            )
                                    },
                                    enabled =
                                        searchQuery.isNotBlank() &&
                                                !searchUiState.isLoading
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored
                                                .Filled
                                                .ArrowForward,
                                        contentDescription =
                                            stringResource(
                                                R.string.search_action
                                            )
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions =
                                KeyboardOptions(
                                    imeAction =
                                        ImeAction.Search
                                ),
                            keyboardActions =
                                KeyboardActions(
                                    onSearch = {
                                        bookSearchViewModel
                                            .searchBooks(
                                                searchQuery
                                            )
                                    }
                                )
                        )

                        if (searchUiState.isLoading) {
                            Column(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )

                                Text(
                                    text =
                                        stringResource(
                                            R.string.searching_for_books
                                        ),
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }

                        searchUiState.errorMessage?.let {
                            Text(
                                text = stringResource(
                                    R.string.book_search_error
                                ),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .error
                            )
                        }

                        if (
                            !searchUiState.isLoading &&
                            searchUiState.errorMessage == null &&
                            searchUiState.results.isNotEmpty()
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.search_results_count,
                                    searchUiState.results.size
                                ),
                                fontWeight =
                                    FontWeight.Bold,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onBackground
                            )

                            LazyColumn(
                                modifier =
                                    Modifier.weight(1f),
                                verticalArrangement =
                                    Arrangement.spacedBy(
                                        12.dp
                                    )
                            ) {
                                items(
                                    items =
                                        searchUiState.results,
                                    key = { book ->
                                        book.externalId
                                    }
                                ) { book ->
                                    BookSearchResultCard(
                                        book = book,
                                        onClick = {
                                            bookTitle =
                                                book.title

                                            bookAuthor =
                                                book.author

                                            bookTotalPages =
                                                book.totalPages
                                                    ?.toString()
                                                    .orEmpty()

                                            selectedIsbn =
                                                book.isbn

                                            selectedCoverUrl =
                                                book.coverUrl

                                            selectedLargeCoverUrl =
                                                book.largeCoverUrl

                                            selectedDescription =
                                                book.description

                                            selectedMethod =
                                                AddBookMethod.MANUAL
                                        }
                                    )
                                }
                            }
                        }

                        if (
                            searchUiState.hasSearched &&
                            !searchUiState.isLoading &&
                            searchUiState.errorMessage == null &&
                            searchUiState.results.isEmpty()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text =
                                        stringResource(
                                            R.string.book_not_identified
                                        ),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onBackground
                                )

                                Text(
                                    text =
                                        stringResource(
                                            R.string.book_not_identified_hint
                                        ),
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )

                                Button(
                                    onClick = {
                                        selectedMethod =
                                            AddBookMethod.SCAN
                                    }
                                ) {
                                    Text(
                                        stringResource(
                                            R.string.scan_again
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        selectedMethod =
                                            AddBookMethod.MANUAL
                                    }
                                ) {
                                    Text(
                                        stringResource(
                                            R.string.add_manually
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                AddBookMethod.SCAN -> {
                    BookScannerScreen(
                        mode = scannerMode,
                        onModeChange = { newMode ->
                            scannerMode = newMode
                        },
                        onSpineTextRecognized = { text ->
                            val searchText =
                                text
                                    .lines()
                                    .map { line ->
                                        line.trim()
                                    }
                                    .filter { line ->
                                        line.length >= 2
                                    }
                                    .joinToString(" ")

                            val recognizedWords =
                                searchText
                                    .split(" ")
                                    .filter { word ->
                                        word.length >= 2
                                    }

                            if (
                                recognizedWords.size < 2
                            ) {
                                scannerMessage =
                                    scannerTextError
                            } else {
                                scannerMessage = null
                                searchQuery = searchText

                                bookSearchViewModel
                                    .searchBooksWithFallback(
                                        searchText
                                    )

                                selectedMethod =
                                    AddBookMethod.SEARCH
                            }
                        },
                        onBarcodeDetected = { barcode ->
                            searchQuery = barcode

                            bookSearchViewModel
                                .searchBookByIsbn(
                                    barcode
                                )

                            selectedMethod =
                                AddBookMethod.SEARCH
                        },
                        modifier = Modifier.weight(1f)
                    )

                    scannerMessage?.let { message ->
                        Text(
                            text = message,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primary,
                            modifier =
                                Modifier.padding(top = 8.dp)
                        )
                    }
                }

                AddBookMethod.MANUAL -> {
                    Column {
                        OutlinedTextField(
                            value = bookTitle,
                            onValueChange = {
                                bookTitle = it
                            },
                            label = {
                                Text(
                                    stringResource(
                                        R.string.book_title
                                    )
                                )
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value = bookAuthor,
                            onValueChange = {
                                bookAuthor = it
                            },
                            label = {
                                Text(
                                    stringResource(
                                        R.string.author_label
                                    )
                                )
                            },
                            modifier =
                                Modifier.fillMaxWidth()
                        )

                        Spacer(
                            modifier =
                                Modifier.height(12.dp)
                        )

                        OutlinedTextField(
                            value = bookTotalPages,
                            onValueChange = { newValue ->
                                if (
                                    newValue.all { character ->
                                        character.isDigit()
                                    }
                                ) {
                                    bookTotalPages =
                                        newValue
                                }
                            },
                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Number
                                ),
                            label = {
                                Text(
                                    stringResource(
                                        R.string.total_pages
                                    )
                                )
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = hasPagesError
                        )

                        if (hasPagesError) {
                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    stringResource(
                                        R.string.invalid_page_number
                                    ),
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .error
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded =
                                isStatusMenuExpanded,
                            onExpandedChange = {
                                isStatusMenuExpanded =
                                    !isStatusMenuExpanded
                            }
                        ) {
                            OutlinedTextField(
                                value =
                                    selectedStatus
                                        ?.displayName()
                                        ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(
                                            R.string.reading_status
                                        )
                                    )
                                },
                                placeholder = {
                                    Text(
                                        stringResource(
                                            R.string.choose_status
                                        )
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults
                                        .TrailingIcon(
                                            expanded =
                                                isStatusMenuExpanded
                                        )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(
                                        "reading_status_dropdown"
                                    )
                                    .menuAnchor(
                                        type =
                                            ExposedDropdownMenuAnchorType
                                                .PrimaryNotEditable,
                                        enabled = true
                                    )
                            )

                            ExposedDropdownMenu(
                                expanded =
                                    isStatusMenuExpanded,
                                onDismissRequest = {
                                    isStatusMenuExpanded =
                                        false
                                }
                            ) {
                                ReadingStatus.entries
                                    .forEach { status ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    status
                                                        .displayName()
                                                )
                                            },
                                            onClick = {
                                                selectedStatus =
                                                    status

                                                isStatusMenuExpanded =
                                                    false
                                            }
                                        )
                                    }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded =
                                isOwnershipMenuExpanded,
                            onExpandedChange = {
                                isOwnershipMenuExpanded =
                                    !isOwnershipMenuExpanded
                            }
                        ) {
                            OutlinedTextField(
                                value =
                                    selectedOwnership
                                        ?.displayName()
                                        ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(
                                            R.string.book_ownership
                                        )
                                    )
                                },
                                placeholder = {
                                    Text(
                                        stringResource(
                                            R.string.choose_ownership
                                        )
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults
                                        .TrailingIcon(
                                            expanded =
                                                isOwnershipMenuExpanded
                                        )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(
                                        "book_ownership_dropdown"
                                    )
                                    .menuAnchor(
                                        type =
                                            ExposedDropdownMenuAnchorType
                                                .PrimaryNotEditable,
                                        enabled = true
                                    )
                            )

                            ExposedDropdownMenu(
                                expanded =
                                    isOwnershipMenuExpanded,
                                onDismissRequest = {
                                    isOwnershipMenuExpanded =
                                        false
                                }
                            ) {
                                BookOwnership.entries
                                    .forEach { ownership ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    ownership
                                                        .displayName()
                                                )
                                            },
                                            onClick = {
                                                selectedOwnership =
                                                    ownership

                                                isOwnershipMenuExpanded =
                                                    false
                                            }
                                        )
                                    }
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.height(24.dp)
                        )

                        Button(
                            onClick = {
                                val pages =
                                    totalPages
                                        ?: return@Button

                                val status =
                                    selectedStatus
                                        ?: return@Button

                                val ownership =
                                    selectedOwnership
                                        ?: return@Button

                                val newBook =
                                    Book(
                                        title =
                                            bookTitle.trim(),
                                        author =
                                            bookAuthor.trim(),
                                        status = status,
                                        currentPage = 0,
                                        totalPages = pages,
                                        ownership =
                                            ownership,
                                        isbn =
                                            selectedIsbn,
                                        coverUrl =
                                            selectedCoverUrl,
                                        largeCoverUrl =
                                            selectedLargeCoverUrl,
                                        description =
                                            selectedDescription
                                    )

                                onSaveBook(newBook)
                            },
                            modifier =
                                Modifier.fillMaxWidth(),
                            enabled = isFormValid
                        ) {
                            Text(
                                stringResource(
                                    R.string.save_book
                                )
                            )
                        }
                    }
                }

                null -> Unit
            }
        }
    }
}

@Composable
private fun AddBookOptionCard(
    number: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        shape =
                            RoundedCornerShape(16.dp)
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                Text(
                    text = number,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onPrimary
                )
            }

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                )

                Spacer(
                    modifier =
                        Modifier.height(4.dp)
                )

                Text(
                    text = description,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReadingStatus.displayName(): String {
    return when (this) {
        ReadingStatus.READING ->
            stringResource(
                R.string.status_currently_reading
            )

        ReadingStatus.WANT_TO_READ ->
            stringResource(
                R.string.status_want_to_read
            )

        ReadingStatus.FINISHED ->
            stringResource(
                R.string.status_finished
            )

        ReadingStatus.DNF ->
            stringResource(
                R.string.status_did_not_finish
            )

        ReadingStatus.UNREAD ->
            stringResource(
                R.string.status_unread
            )
    }
}

@Composable
private fun BookOwnership.displayName(): String {
    return when (this) {
        BookOwnership.OWNED ->
            stringResource(
                R.string.ownership_owned
            )

        BookOwnership.BORROWED ->
            stringResource(
                R.string.ownership_borrowed
            )

        BookOwnership.WISHLIST ->
            stringResource(
                R.string.ownership_wishlist
            )
    }
}

@Composable
private fun BookSearchResultCard(
    book: BookSearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement =
                Arrangement.spacedBy(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(104.dp)
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant
                    ),
                contentAlignment =
                    Alignment.Center
            ) {
                if (!book.coverUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = book.coverUrl,
                        contentDescription =
                            stringResource(
                                R.string.book_cover_content_description,
                                book.title
                            ),
                        modifier =
                            Modifier.fillMaxSize(),
                        contentScale =
                            ContentScale.Crop
                    )
                } else {
                    Text(
                        text = "📚",
                        fontSize = 28.sp
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = book.title,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurface
                )

                Text(
                    text = book.author,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )

                book.totalPages?.let { pageCount ->
                    Text(
                        text = stringResource(
                            R.string.pages_count,
                            pageCount
                        ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }

                book.isbn?.let { isbn ->
                    Text(
                        text = stringResource(
                            R.string.isbn_label,
                            isbn
                        ),
                        fontSize = 12.sp,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }
    }
}