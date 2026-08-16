package com.example.readingbunny.data.backup

import android.content.Context
import android.net.Uri
import androidx.room3.withWriteTransaction
import com.example.readingbunny.data.local.ReadingBunnyDatabase
import com.example.readingbunny.data.preferences.UserPreferencesRepository
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.ReadingBunnyBackup
import com.example.readingbunny.model.ReadingSession
import com.example.readingbunny.model.ShelfBookPosition
import com.example.readingbunny.model.ShelfDecoration
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class BackupRepository(
    private val context: Context,
    private val database: ReadingBunnyDatabase,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    private val gson: Gson =
        GsonBuilder()
            .setPrettyPrinting()
            .create()

    suspend fun exportBackup(
        uri: Uri,
        books: List<Book>,
        readingSessions: List<ReadingSession>,
        shelfDecorations: List<ShelfDecoration>,
        shelfBookPositions: List<ShelfBookPosition>,
        dailyGoalMinutes: Int
    ) {

        val backup = ReadingBunnyBackup(
            exportedAt = System.currentTimeMillis(),
            books = books,
            readingSessions = readingSessions,
            shelfDecorations = shelfDecorations,
            shelfBookPositions = shelfBookPositions,
            dailyGoalMinutes = dailyGoalMinutes
        )

        val json = gson.toJson(backup)

        withContext(Dispatchers.IO) {

            val outputStream =
                context.contentResolver
                    .openOutputStream(uri)
                    ?: throw IOException(
                        "Could not open backup file"
                    )

            outputStream
                .bufferedWriter()
                .use { writer ->

                    writer.write(json)
                }
        }
    }

    suspend fun restoreBackup(
        uri: Uri
    ) {

        val backup = withContext(Dispatchers.IO) {

            val inputStream =
                context.contentResolver
                    .openInputStream(uri)
                    ?: throw IOException(
                        "Could not open backup file"
                    )

            val json =
                inputStream
                    .bufferedReader()
                    .use { reader ->
                        reader.readText()
                    }

            try {

                gson.fromJson(
                    json,
                    ReadingBunnyBackup::class.java
                ) ?: throw IllegalArgumentException(
                    "Backup file is empty"
                )

            } catch (exception: Exception) {

                throw IllegalArgumentException(
                    "Invalid backup file",
                    exception
                )
            }
        }

        try {
            validateBackup(backup)
        } catch (exception: IllegalArgumentException) {
            throw exception
        } catch (exception: Exception) {
            throw IllegalArgumentException(
                "Invalid backup structure",
                exception
            )
        }

        database.withWriteTransaction {

            val bookDao =
                database.bookDao()

            val sessionDao =
                database.readingSessionDao()

            val decorationDao =
                database.shelfDecorationDao()

            val positionDao =
                database.shelfBookPositionDao()

            // Delete child/related data first.
            sessionDao.deleteAllSessions()
            positionDao.deleteAllPositions()
            decorationDao.deleteAllDecorations()
            bookDao.deleteAllBooks()

            // Books first so bookId relationships remain valid.
            backup.books.forEach { book ->
                bookDao.insertBook(book)
            }

            backup.readingSessions.forEach { session ->
                sessionDao.insertSession(session)
            }

            backup.shelfDecorations.forEach { decoration ->
                decorationDao.insertDecoration(decoration)
            }

            backup.shelfBookPositions.forEach { position ->
                positionDao.savePosition(position)
            }
        }

        userPreferencesRepository.setDailyGoalMinutes(
            backup.dailyGoalMinutes
        )
    }

    private fun validateBackup(
        backup: ReadingBunnyBackup
    ) {

        if (backup.backupVersion != 1) {
            throw IllegalArgumentException(
                "Unsupported backup version"
            )
        }

        if (backup.dailyGoalMinutes <= 0) {
            throw IllegalArgumentException(
                "Invalid daily reading goal"
            )
        }

        if (
            backup.books.any { book ->
                book.id <= 0
            }
        ) {
            throw IllegalArgumentException(
                "Invalid book IDs"
            )
        }

        val bookIds =
            backup.books
                .map { book ->
                    book.id
                }
                .toSet()

        if (bookIds.size != backup.books.size) {
            throw IllegalArgumentException(
                "Duplicate book IDs"
            )
        }

        val hasInvalidSession =
            backup.readingSessions.any { session ->
                session.bookId !in bookIds
            }

        if (hasInvalidSession) {
            throw IllegalArgumentException(
                "Reading session refers to a missing book"
            )
        }

        val hasInvalidPosition =
            backup.shelfBookPositions.any { position ->
                position.bookId !in bookIds
            }

        if (hasInvalidPosition) {
            throw IllegalArgumentException(
                "Shelf position refers to a missing book"
            )
        }
    }
}