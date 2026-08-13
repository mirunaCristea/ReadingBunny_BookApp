package com.example.readingbunny.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.ReadingSession
import com.example.readingbunny.model.ShelfBookPosition
import com.example.readingbunny.model.ShelfDecoration

@Database(
    entities = [
        Book::class,
        ShelfDecoration::class,
        ShelfBookPosition::class,
        ReadingSession::class
    ],
    version = 5,
    exportSchema = false
)
abstract class ReadingBunnyDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun shelfDecorationDao(): ShelfDecorationDao

    abstract fun shelfBookPositionDao(): ShelfBookPositionDao

    abstract fun readingSessionDao(): ReadingSessionDao
    companion object {

        @Volatile
        private var INSTANCE: ReadingBunnyDatabase? = null

        fun getDatabase(context: Context): ReadingBunnyDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder<ReadingBunnyDatabase>(
                    context = context.applicationContext,
                    name = "reading_bunny_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}