package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readingbunny.data.repository.ShelfBookPositionRepository
import com.example.readingbunny.data.repository.ShelfDecorationRepository
import com.example.readingbunny.data.preferences.UserPreferencesRepository

class BookshelfViewModelFactory(
    private val decorationRepository: ShelfDecorationRepository,
    private val positionRepository: ShelfBookPositionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BookshelfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BookshelfViewModel(decorationRepository, positionRepository, userPreferencesRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}