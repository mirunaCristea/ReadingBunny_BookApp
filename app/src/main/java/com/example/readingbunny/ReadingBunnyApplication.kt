package com.example.readingbunny


import android.app.Application
import com.example.readingbunny.data.local.ReadingBunnyDatabase
import com.example.readingbunny.data.repository.BookRepository
import com.example.readingbunny.data.repository.ReadingSessionRepository
import com.example.readingbunny.data.repository.ShelfBookPositionRepository
import com.example.readingbunny.data.repository.ShelfDecorationRepository

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
}