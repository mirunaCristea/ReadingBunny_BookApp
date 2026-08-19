package com.example.readingbunny.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(
    private val context: Context
) {

    private val dailyGoalKey =
        intPreferencesKey("daily_goal_minutes")

    val dailyGoalMinutes: Flow<Int> =
        context.dataStore.data.map { preferences ->

            preferences[dailyGoalKey] ?: 30
        }

    suspend fun setDailyGoalMinutes(
        minutes: Int
    ) {

        context.dataStore.edit { preferences ->

            preferences[dailyGoalKey] = minutes
        }
    }
}