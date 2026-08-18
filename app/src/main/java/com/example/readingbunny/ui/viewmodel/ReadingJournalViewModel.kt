package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readingbunny.data.repository.ReadingJournalRepository
import com.example.readingbunny.model.JournalEntryType
import com.example.readingbunny.model.ReadingJournalEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ReadingJournalViewModel(
    private val repository: ReadingJournalRepository
) : ViewModel() {

    val allEntries =
        repository.allEntries

    fun entriesForBook(
        bookId: Int
    ): Flow<List<ReadingJournalEntry>> {

        return repository.getEntriesForBook(
            bookId
        )
    }

    fun addEntry(
        bookId: Int,
        type: JournalEntryType,
        content: String,
        page: Int?
    ) {

        val cleanContent = content.trim()

        if (cleanContent.isBlank()) {
            return
        }

        viewModelScope.launch {

            repository.addEntry(
                ReadingJournalEntry(
                    bookId = bookId,
                    type = type,
                    content = cleanContent,
                    page = page
                )
            )
        }
    }

    fun deleteEntry(
        entry: ReadingJournalEntry
    ) {

        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}