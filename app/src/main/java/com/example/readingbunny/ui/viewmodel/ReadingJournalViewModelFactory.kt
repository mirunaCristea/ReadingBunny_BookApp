package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readingbunny.data.repository.ReadingJournalRepository

class ReadingJournalViewModelFactory(
    private val repository: ReadingJournalRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                ReadingJournalViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return ReadingJournalViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}