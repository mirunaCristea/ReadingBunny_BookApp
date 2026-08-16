package com.example.readingbunny.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

enum class JournalEntryType {
    NOTE,
    QUOTE
}

@Entity(tableName = "reading_journal_entries")
data class ReadingJournalEntry(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val bookId: Int,

    val type: JournalEntryType,

    val content: String,

    val page: Int? = null,

    val createdAt: Long =
        System.currentTimeMillis()
)