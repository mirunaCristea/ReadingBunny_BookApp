package com.example.readingbunny


import android.app.Application
import com.example.readingbunny.data.local.ReadingBunnyDatabase
import com.example.readingbunny.data.repository.BookRepository
import com.example.readingbunny.data.repository.ReadingSessionRepository
import com.example.readingbunny.data.repository.ShelfBookPositionRepository
import com.example.readingbunny.data.repository.ShelfDecorationRepository
import com.example.readingbunny.data.remote.GoogleBooksApiClient
import com.example.readingbunny.data.repository.BookSearchRepository
import com.example.readingbunny.BuildConfig
import com.example.readingbunny.data.remote.OpenLibraryApiClient
import com.example.readingbunny.data.preferences.UserPreferencesRepository
import com.example.readingbunny.data.backup.BackupRepository

class ReadingBunnyApplication : Application() {

    val database: ReadingBunnyDatabase by lazy {
        ReadingBunnyDatabase.getDatabase(this)
    }

    val repository: BookRepository by lazy {
        BookRepository(database.bookDao())
    }

    val decorationRepository: ShelfDecorationRepository by lazy {
        ShelfDecorationRepository(
            database.shelfDecorationDao()
        )
    }

    val bookPositionRepository: ShelfBookPositionRepository by lazy {
        ShelfBookPositionRepository(
            database.shelfBookPositionDao()
        )
    }

    val readingSessionRepository: ReadingSessionRepository by lazy {
        ReadingSessionRepository(
            database.readingSessionDao()
        )
    }

    val bookSearchRepository by lazy {
        BookSearchRepository(
            api = GoogleBooksApiClient.api,
            openLibraryApi = OpenLibraryApiClient.api,
            apiKey = BuildConfig.GOOGLE_BOOKS_API_KEY
        )
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(this)
    }

    val backupRepository: BackupRepository by lazy {
        BackupRepository(this)
    }

}