package com.example.readingbunny.backup

import android.content.Context
import android.net.Uri
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.readingbunny.data.backup.BackupRepository
import com.example.readingbunny.data.local.ReadingBunnyDatabase
import com.example.readingbunny.data.preferences.UserPreferencesRepository
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookOwnership
import com.example.readingbunny.model.ReadingBunnyBackup
import com.example.readingbunny.model.ReadingSession
import com.example.readingbunny.model.ReadingStatus
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BackupRepositoryInstrumentedTest {

    private lateinit var context:
            Context

    private lateinit var database:
            ReadingBunnyDatabase

    private lateinit var preferences:
            UserPreferencesRepository

    private lateinit var repository:
            BackupRepository

    @Before
    fun setup() {

        context =
            ApplicationProvider
                .getApplicationContext()

        database =
            Room
                .inMemoryDatabaseBuilder<
                        ReadingBunnyDatabase
                        >(context)
                .build()

        preferences =
            UserPreferencesRepository(
                context
            )

        repository =
            BackupRepository(
                context = context,
                database = database,
                userPreferencesRepository =
                    preferences
            )
    }

    @After
    fun tearDown() {

        database.close()
    }

    @Test
    fun restoreBackup_rejectsSessionWithMissingBook() =
        runBlocking {

            val backup =
                ReadingBunnyBackup(
                    exportedAt = 1L,
                    books =
                        listOf(
                            testBook(
                                id = 1,
                                title =
                                    "Existing Book"
                            )
                        ),
                    readingSessions =
                        listOf(
                            ReadingSession(
                                id = 1,
                                bookId = 999,
                                startedAt = 1,
                                endedAt = 2,
                                startPage = 0,
                                endPage = 10,
                                durationSeconds =
                                    100
                            )
                        ),
                    shelfDecorations =
                        emptyList(),
                    shelfBookPositions =
                        emptyList(),
                    readingJournalEntries =
                        emptyList(),
                    dailyGoalMinutes = 30
                )

            val uri =
                writeBackup(backup)

            try {

                repository
                    .restoreBackup(uri)

                fail(
                    "Expected invalid backup"
                )

            } catch (
                exception:
                IllegalArgumentException
            ) {

                assertEquals(
                    "Reading session refers to a missing book",
                    exception.message
                )
            }
        }

    @Test
    fun restoreBackup_replacesExistingData() =
        runBlocking {

            database
                .bookDao()
                .insertBook(
                    testBook(
                        id = 1,
                        title = "Old Book"
                    )
                )

            val newBook =
                testBook(
                    id = 2,
                    title = "Restored Book"
                )

            val session =
                ReadingSession(
                    id = 1,
                    bookId = 2,
                    startedAt = 100,
                    endedAt = 200,
                    startPage = 10,
                    endPage = 20,
                    durationSeconds = 100
                )

            val backup =
                ReadingBunnyBackup(
                    exportedAt = 1000,
                    books =
                        listOf(newBook),
                    readingSessions =
                        listOf(session),
                    shelfDecorations =
                        emptyList(),
                    shelfBookPositions =
                        emptyList(),
                    readingJournalEntries =
                        emptyList(),
                    dailyGoalMinutes = 45
                )

            repository.restoreBackup(
                writeBackup(backup)
            )

            assertNull(
                database
                    .bookDao()
                    .getBookById(1)
            )

            val restoredBook =
                database
                    .bookDao()
                    .getBookById(2)

            assertEquals(
                "Restored Book",
                restoredBook?.title
            )

            val restoredSessions =
                database
                    .readingSessionDao()
                    .getAllSessions()
                    .first()

            assertEquals(
                1,
                restoredSessions.size
            )

            assertEquals(
                2,
                restoredSessions
                    .first()
                    .bookId
            )

            assertEquals(
                45,
                preferences
                    .dailyGoalMinutes
                    .first()
            )
        }

    private fun writeBackup(
        backup: ReadingBunnyBackup
    ): Uri {

        val file =
            File(
                context.cacheDir,
                "test-backup-" +
                        System.nanoTime() +
                        ".json"
            )

        file.writeText(
            Gson().toJson(backup)
        )

        return Uri.fromFile(file)
    }

    private fun testBook(
        id: Int,
        title: String
    ) =
        Book(
            id = id,
            title = title,
            author = "Test Author",
            status =
                ReadingStatus.READING,
            ownership =
                BookOwnership.OWNED,
            currentPage = 0,
            totalPages = 200
        )
}