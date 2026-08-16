package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readingbunny.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {

    val dailyGoalMinutes: StateFlow<Int> =
        repository.dailyGoalMinutes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 30
        )

    fun setDailyGoalMinutes(minutes: Int) {

        viewModelScope.launch {
            repository.setDailyGoalMinutes(minutes)
        }
    }
}