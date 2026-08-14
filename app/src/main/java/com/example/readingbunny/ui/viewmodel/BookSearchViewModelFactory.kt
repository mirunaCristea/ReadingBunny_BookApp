package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readingbunny.data.repository.BookSearchRepository

class BookSearchViewModelFactory(
    private val repository: BookSearchRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(BookSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookSearchViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}