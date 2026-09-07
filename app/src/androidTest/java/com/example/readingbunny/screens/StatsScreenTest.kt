package com.example.readingbunny.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.readingbunny.model.ReadingSession
import com.example.readingbunny.ui.screens.StatsScreen
import com.example.readingbunny.ui.theme.ReadingBunnyTheme
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class StatsScreenTest {

    @get:Rule
    val composeRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun statsScreen_calculatesSessionStatistics() {

        val today =
            LocalDate.now()

        val firstSession =
            ReadingSession(
                id = 1,
                bookId = 1,
                startedAt =
                    timestamp(
                        today,
                        10
                    ),
                endedAt =
                    timestamp(
                        today,
                        10
                    ) + 1_800_000,
                startPage = 10,
                endPage = 30,
                durationSeconds = 1800
            )

        val secondSession =
            ReadingSession(
                id = 2,
                bookId = 1,
                startedAt =
                    timestamp(
                        today,
                        14
                    ),
                endedAt =
                    timestamp(
                        today,
                        14
                    ) + 900_000,
                startPage = 30,
                endPage = 40,
                durationSeconds = 900
            )

        composeRule.setContent {

            ReadingBunnyTheme {

                StatsScreen(
                    sessions =
                        listOf(
                            firstSession,
                            secondSession
                        ),
                    books = emptyList()
                )
            }
        }

        composeRule
            .onNodeWithText("45m")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("30")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("22m")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Reading time")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Pages")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Sessions")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Reading days")
            .assertIsDisplayed()
    }

    private fun timestamp(
        date: LocalDate,
        hour: Int
    ): Long {

        return date
            .atTime(hour, 0)
            .atZone(
                ZoneId.systemDefault()
            )
            .toInstant()
            .toEpochMilli()
    }
}