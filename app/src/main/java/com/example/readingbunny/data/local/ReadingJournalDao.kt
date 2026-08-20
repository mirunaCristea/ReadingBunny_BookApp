package com.example.readingbunny.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.readingbunny.model.ReadingJournalEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingJournalDao {

    @Insert
    suspend fun insertEntry(
        entry: ReadingJournalEntry
    )

    @Update
    suspend fun updateEntry(
        entry: ReadingJournalEntry
    )

    @Delete
    suspend fun deleteEntry(
        entry: ReadingJournalEntry
    )

    @Query(
        """
        SELECT * FROM reading_journal_entries
        WHERE bookId = :bookId
        ORDER BY createdAt DESC
        """
    )
    fun getEntriesForBook(
        bookId: Int
    ): Flow<List<ReadingJournalEntry>>

    @Query(
        "SELECT * FROM reading_journal_entries ORDER BY createdAt DESC"
    )
    fun getAllEntries():
            Flow<List<ReadingJournalEntry>>

    @Query(
        "DELETE FROM reading_journal_entries"
    )
    suspend fun deleteAllEntries()

    @Query(
        "DELETE FROM reading_journal_entries WHERE bookId = :bookId"
    )
    suspend fun deleteEntriesForBook(
        bookId: Int
    )
}