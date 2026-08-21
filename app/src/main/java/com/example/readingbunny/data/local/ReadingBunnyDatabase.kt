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
import com.example.readingbunny.model.ReadingJournalEntry

@Database(
    entities = [
        Book::class,
        ShelfDecoration::class,
        ShelfBookPosition::class,
        ReadingSession::class,
        ReadingJournalEntry::class
    ],
    version = 8,
    exportSchema = false
)
abstract class ReadingBunnyDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun shelfDecorationDao(): ShelfDecorationDao

    abstract fun shelfBookPositionDao(): ShelfBookPositionDao

    abstract fun readingSessionDao(): ReadingSessionDao

    abstract fun readingJournalDao(): ReadingJournalDao
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

        private val MIGRATION_6_7 =
            object : Migration(6, 7) {

                override suspend fun migrate(
                    connection: SQLiteConnection
                ) {

                    connection.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS `reading_journal_entries` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `bookId` INTEGER NOT NULL,
                    `type` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `page` INTEGER,
                    `createdAt` INTEGER NOT NULL
                )
                """.trimIndent()
                    )
                }
            }
            
        private val MIGRATION_7_8 =
            object : Migration(7, 8) {

            override suspend fun migrate(
                connection: SQLiteConnection
            ) {
                connection.execSQL(
                    "ALTER TABLE `books` ADD COLUMN `largeCoverUrl` TEXT"
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
                    .addMigrations(
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                    )
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}