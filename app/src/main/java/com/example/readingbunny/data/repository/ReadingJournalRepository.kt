package com.example.readingbunny.data.repository

import com.example.readingbunny.data.local.ReadingJournalDao
import com.example.readingbunny.model.ReadingJournalEntry

class ReadingJournalRepository(
    private val dao: ReadingJournalDao
) {

    fun getEntriesForBook(
        bookId: Int
    ) =
        dao.getEntriesForBook(bookId)

    val allEntries =
        dao.getAllEntries()

    suspend fun addEntry(
        entry: ReadingJournalEntry
    ) {
        dao.insertEntry(entry)
    }

    suspend fun updateEntry(
        entry: ReadingJournalEntry
    ) {
        dao.updateEntry(entry)
    }

    suspend fun deleteEntry(
        entry: ReadingJournalEntry
    ) {
        dao.deleteEntry(entry)
    }
}