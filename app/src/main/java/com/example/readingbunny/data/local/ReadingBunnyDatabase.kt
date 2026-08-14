package com.example.readingbunny.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
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
    version = 6,
    exportSchema = false
)
abstract class ReadingBunnyDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun shelfDecorationDao(): ShelfDecorationDao

    abstract fun shelfBookPositionDao(): ShelfBookPositionDao

    abstract fun readingSessionDao(): ReadingSessionDao
    companion object {

        private val MIGRATION_5_6 = object : Migration(5,6) {
            override suspend fun migrate(
                connection: SQLiteConnection
            ) {
                connection.execSQL(
                    "ALTER TABLE `books` ADD COLUMN `isbn` TEXT"
                )

                connection.execSQL(
                    "ALTER TABLE `books` ADD COLUMN `coverUrl` TEXT"
                )

                connection.execSQL(
                    "ALTER TABLE `books` ADD COLUMN `description` TEXT"
                )
            }
        }
        @Volatile
        private var INSTANCE: ReadingBunnyDatabase? = null

        fun getDatabase(context: Context): ReadingBunnyDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder<ReadingBunnyDatabase>(
                    context = context.applicationContext,
                    name = "reading_bunny_database"
                )
                    .addMigrations(MIGRATION_5_6)
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}