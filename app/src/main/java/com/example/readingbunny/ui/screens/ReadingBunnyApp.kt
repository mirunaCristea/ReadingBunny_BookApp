package com.example.readingbunny.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.readingbunny.ReadingBunnyApplication
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.ui.viewmodel.BookSearchViewModel
import com.example.readingbunny.ui.viewmodel.BookSearchViewModelFactory
import com.example.readingbunny.ui.viewmodel.BookViewModel
import com.example.readingbunny.ui.viewmodel.BookViewModelFactory
import com.example.readingbunny.ui.viewmodel.BookshelfViewModel
import com.example.readingbunny.ui.viewmodel.BookshelfViewModelFactory
import com.example.readingbunny.ui.viewmodel.ProfileViewModel
import com.example.readingbunny.ui.viewmodel.ProfileViewModelFactory
import com.example.readingbunny.ui.viewmodel.ReadingJournalViewModel
import com.example.readingbunny.ui.viewmodel.ReadingJournalViewModelFactory
import com.example.readingbunny.ui.viewmodel.ReadingSessionViewModel
import com.example.readingbunny.ui.viewmodel.ReadingSessionViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun ReadingBunnyApp() {
    var selectedItem by rememberSaveable {
        mutableIntStateOf(0)
    }

    var isAddingBook by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val application =
        context.applicationContext as ReadingBunnyApplication

    val bookViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(application.repository)
    )

    val bookshelfViewModel: BookshelfViewModel = viewModel(
        factory = BookshelfViewModelFactory(
            application.decorationRepository,
            application.bookPositionRepository
        )
    )

    val decorations by bookshelfViewModel.decorations.collectAsState(
        initial = emptyList()
    )

    val books by bookViewModel.books.collectAsState(
        initial = emptyList()
    )

    val bookPositions by bookshelfViewModel.bookPositions.collectAsState(
        initial = emptyList()
    )

    val readingJournalViewModel: ReadingJournalViewModel = viewModel(
        factory =
            ReadingJournalViewModelFactory(
                application.readingJournalRepository
            )
    )

    var selectedBookId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val selectedBook = books.firstOrNull { book ->
        book.id == selectedBookId
    }

    val journalEntries by
    readingJournalViewModel
        .entriesForBook(
            selectedBookId ?: -1
        )
        .collectAsState(
            initial = emptyList()
        )

    val allJournalEntries by
    readingJournalViewModel
        .allEntries
        .collectAsState(
            initial = emptyList()
        )

    val currentBook = books.firstOrNull { book ->
        book.status == ReadingStatus.READING
    }

    val readingSessionViewModel: ReadingSessionViewModel = viewModel(
        factory = ReadingSessionViewModelFactory(
            application.readingSessionRepository
        )
    )

    val elapsedSeconds by
    readingSessionViewModel.elapsedSeconds.collectAsState()

    val isSessionRunning by
    readingSessionViewModel.isRunning.collectAsState()

    val readingSessions by
    readingSessionViewModel.sessions.collectAsState(
        initial = emptyList()
    )

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(
            application.userPreferencesRepository
        )
    )

    val dailyGoalMinutes by
    profileViewModel.dailyGoalMinutes.collectAsState()

    val coroutineScope =
        rememberCoroutineScope()

    var backupMessage by remember {
        mutableStateOf<String?>(null)
    }

    val today = java.time.LocalDate.now()

    val todayReadingSeconds =
        readingSessions
            .filter { session ->
                val sessionDate =
                    java.time.Instant
                        .ofEpochMilli(session.startedAt)
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()

                sessionDate == today
            }
            .sumOf { session ->
                session.durationSeconds
            }

    var activeSessionBookId by rememberSaveable {
        mutableStateOf<Int?>(null)
    }

    val activeSessionBook =
        books.firstOrNull { book ->
            book.id == activeSessionBookId
        }

    val bookSearchViewModel: BookSearchViewModel = viewModel(
        factory = BookSearchViewModelFactory(
            application.bookSearchRepository
        )
    )

    if (activeSessionBook != null) {
        val sessionBook = activeSessionBook

        ReadingSessionScreen(
            book = sessionBook,
            elapsedSeconds = elapsedSeconds,
            isRunning = isSessionRunning,

            onPause = {
                readingSessionViewModel.pauseSession()
            },

            onResume = {
                readingSessionViewModel.resumeSession()
            },

            onFinish = { endPage ->
                readingSessionViewModel.finishSession(
                    endPage = endPage
                ) {
                    val newStatus =
                        if (endPage >= sessionBook.totalPages) {
                            ReadingStatus.FINISHED
                        } else {
                            ReadingStatus.READING
                        }

                    bookViewModel.updateBook(
                        sessionBook.copy(
                            currentPage = endPage,
                            status = newStatus
                        )
                    )

                    activeSessionBookId = null
                }
            },

            onCancel = {
                readingSessionViewModel.cancelSession()
                activeSessionBookId = null
            }
        )
    } else {
        val currentlyReadingBooks =
            books.count { book ->
                book.status == ReadingStatus.READING
            }

        val finishedBooks =
            books.count { book ->
                book.status == ReadingStatus.FINISHED
            }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,

            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedItem == 0,
                        onClick = {
                            selectedItem = 0
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = {
                            Text("Home")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedItem == 1,
                        onClick = {
                            selectedItem = 1
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = "Books"
                            )
                        },
                        label = {
                            Text("Books")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedItem == 2,
                        onClick = {
                            selectedItem = 2
                        },
                        icon = {
                            Icon(
                                imageVector =
                                    Icons.Filled.CollectionsBookmark,
                                contentDescription = "Bookshelf"
                            )
                        },
                        label = {
                            Text("Bookshelf")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedItem == 3,
                        onClick = {
                            selectedItem = 3
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.BarChart,
                                contentDescription = "Stats"
                            )
                        },
                        label = {
                            Text("Stats")
                        }
                    )

                    NavigationBarItem(
                        selected = selectedItem == 4,
                        onClick = {
                            selectedItem = 4
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = {
                            Text("Profile")
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.padding(innerPadding)
            ) {
                when (selectedItem) {
                    0 -> HomeScreen(
                        book = currentBook,
                        todayReadingSeconds = todayReadingSeconds,
                        dailyGoalMinutes = dailyGoalMinutes,
                        onStartReading = { book ->
                            activeSessionBookId = book.id

                            readingSessionViewModel.startSession(
                                book
                            )
                        }
                    )

                    1 -> {
                        if (selectedBook != null) {
                            BookDetailsScreen(
                                book = selectedBook,
                                journalEntries = journalEntries,
                                onBackClick = {
                                    selectedBookId = null
                                },
                                onUpdateBook = { updatedBook ->
                                    bookViewModel.updateBook(updatedBook)
                                },
                                onDeleteBook = { book ->
                                    bookViewModel.deleteBook(book)
                                    selectedBookId = null
                                },
                                onAddJournalEntry = {
                                        type,
                                        content,
                                        page ->

                                    readingJournalViewModel.addEntry(
                                        bookId = selectedBook.id,
                                        type = type,
                                        content = content,
                                        page = page
                                    )
                                },
                                onDeleteJournalEntry = { entry ->
                                    readingJournalViewModel.deleteEntry(
                                        entry
                                    )
                                }
                            )
                        } else if (isAddingBook) {
                            AddBookScreen(
                                onBackClick = {
                                    isAddingBook = false
                                },
                                onSaveBook = { newBook ->
                                    bookViewModel.addBook(newBook)
                                    isAddingBook = false
                                },
                                bookSearchViewModel = bookSearchViewModel
                            )
                        } else {
                            BookScreen(
                                books = books,
                                onAddBookClick = {
                                    isAddingBook = true
                                },
                                onBookClick = { book ->
                                    selectedBookId = book.id
                                }
                            )
                        }
                    }

                    2 -> BookshelfScreen(
                        books = books,
                        decorations = decorations,
                        bookPositions = bookPositions,

                        onBookClick = { book ->
                            selectedBookId = book.id
                            selectedItem = 1
                        },

                        onAddDecoration = { type, shelfIndex, slotIndex ->
                            bookshelfViewModel.addDecoration(
                                type,
                                shelfIndex,
                                slotIndex
                            )
                        },

                        onDeleteDecoration = { decoration ->
                            bookshelfViewModel.deleteDecoration(decoration)
                        },

                        onMoveBook = { bookId, shelfIndex, slotIndex ->
                            bookshelfViewModel.moveBook(
                                bookId,
                                shelfIndex,
                                slotIndex
                            )
                        },

                        onMoveDecoration = { decoration, shelfIndex, slotIndex ->
                            bookshelfViewModel.moveDecoration(
                                decoration,
                                shelfIndex,
                                slotIndex
                            )
                        }
                    )

                    3 -> StatsScreen(
                        sessions = readingSessions,
                        books = books
                    )

                    4 -> ProfileScreen(
                        dailyGoalMinutes = dailyGoalMinutes,
                        totalBooks = books.size,
                        currentlyReadingBooks = currentlyReadingBooks,
                        finishedBooks = finishedBooks,

                        onDailyGoalChange = { minutes ->
                            profileViewModel.setDailyGoalMinutes(
                                minutes
                            )
                        },

                        onExportBackup = { uri ->
                            backupMessage = null

                            coroutineScope.launch {
                                try {
                                    application.backupRepository
                                        .exportBackup(
                                            uri = uri,
                                            books = books,
                                            readingSessions =
                                                readingSessions,
                                            shelfDecorations =
                                                decorations,
                                            shelfBookPositions =
                                                bookPositions,
                                            readingJournalEntries =
                                                allJournalEntries,
                                            dailyGoalMinutes =
                                                dailyGoalMinutes
                                        )

                                    backupMessage =
                                        "Backup exported successfully."
                                } catch (exception: Exception) {
                                    Log.e(
                                        "Backup",
                                        "Backup export failed",
                                        exception
                                    )

                                    backupMessage =
                                        "Could not export backup. Please try again."
                                }
                            }
                        },

                        onRestoreBackup = { uri ->
                            backupMessage = null

                            coroutineScope.launch {
                                try {
                                    application.backupRepository
                                        .restoreBackup(uri)

                                    backupMessage =
                                        "Backup restored successfully."
                                } catch (exception: Exception) {
                                    Log.e(
                                        "Backup",
                                        "Backup restore failed",
                                        exception
                                    )

                                    backupMessage =
                                        "Could not restore backup. Please check the backup file and try again."
                                }
                            }
                        },

                        backupMessage = backupMessage
                    )
                }
            }
        }
    }
}