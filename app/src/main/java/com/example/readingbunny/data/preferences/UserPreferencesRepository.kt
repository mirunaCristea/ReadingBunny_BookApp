package com.example.readingbunny.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.readingbunny.model.BookshelfStyle


private val Context.dataStore by preferencesDataStore(
    name = "user_preferences"
)

class UserPreferencesRepository(
    private val context: Context
) {

    private val dailyGoalKey =
        intPreferencesKey("daily_goal_minutes")

    private val bookshelfStyleKey =
        stringPreferencesKey("bookshelf_style")

    val dailyGoalMinutes: Flow<Int> =
        context.dataStore.data.map { preferences ->

            preferences[dailyGoalKey] ?: 30
        }

    val bookshelfStyle: Flow<BookshelfStyle> =
        context.dataStore.data.map { preferences ->

            val savedStyle = preferences[bookshelfStyleKey]

            BookshelfStyle.entries.firstOrNull {
                it.name == savedStyle
            } ?: BookshelfStyle.COZY
        }

    suspend fun setDailyGoalMinutes(
        minutes: Int
    ) {

        context.dataStore.edit { preferences ->

            preferences[dailyGoalKey] = minutes
        }
    }


    suspend fun setBookshelfStyle(
        style: BookshelfStyle
    ) {
        context.dataStore.edit { preferences ->

            preferences[bookshelfStyleKey] = style.name
        }
    }
}