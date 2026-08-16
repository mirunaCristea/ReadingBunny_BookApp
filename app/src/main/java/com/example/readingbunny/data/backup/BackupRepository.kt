package com.example.readingbunny.data.backup

import android.content.Context
import android.net.Uri
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
    private val context: Context
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
}