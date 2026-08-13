package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readingbunny.data.repository.ReadingSessionRepository

class ReadingSessionViewModelFactory(
    private val repository: ReadingSessionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                ReadingSessionViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return ReadingSessionViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}