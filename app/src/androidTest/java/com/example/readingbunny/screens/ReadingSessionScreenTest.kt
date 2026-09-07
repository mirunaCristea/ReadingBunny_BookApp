package com.example.readingbunny.screens

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookOwnership
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.ui.screens.ReadingSessionScreen
import com.example.readingbunny.ui.theme.ReadingBunnyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ReadingSessionScreenTest {

    @get:Rule
    val composeRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun closeSession_requiresDiscardConfirmation() {

        var cancelled = false

        composeRule.setContent {

            ReadingBunnyTheme {

                ReadingSessionScreen(
                    book = testBook(),
                    elapsedSeconds = 10,
                    isRunning = true,
                    onPause = {},
                    onResume = {},
                    onFinish = {},
                    onCancel = {
                        cancelled = true
                    },
                    onAddJournalEntry =
                        { _, _, _ -> }
                )
            }
        }

        composeRule
            .onNodeWithText("Close")
            .performClick()

        composeRule
            .onNodeWithText(
                "Discard session?"
            )
            .assertExists()

        composeRule
            .onNodeWithText(
                "Discard session"
            )
            .performClick()

        composeRule.runOnIdle {

            assertTrue(cancelled)
        }
    }

    @Test
    fun saveSession_returnsEndPage() {

        var savedPage: Int? = null

        composeRule.setContent {

            ReadingBunnyTheme {

                ReadingSessionScreen(
                    book = testBook(),
                    elapsedSeconds = 120,
                    isRunning = true,
                    onPause = {},
                    onResume = {},
                    onFinish = {
                        savedPage = it
                    },
                    onCancel = {},
                    onAddJournalEntry =
                        { _, _, _ -> }
                )
            }
        }

        composeRule
            .onNodeWithText(
                "Stop session"
            )
            .performClick()

        composeRule
            .onNode(
                hasSetTextAction()
            )
            .performTextReplacement(
                "120"
            )

        composeRule
            .onNodeWithText(
                "Save session"
            )
            .assertIsEnabled()
            .performClick()

        composeRule.runOnIdle {

            assertEquals(
                120,
                savedPage
            )
        }
    }

    @Test
    fun saveSession_isDisabled_whenPageIsTooLarge() {

        composeRule.setContent {

            ReadingBunnyTheme {

                ReadingSessionScreen(
                    book = testBook(),
                    elapsedSeconds = 120,
                    isRunning = true,
                    onPause = {},
                    onResume = {},
                    onFinish = {},
                    onCancel = {},
                    onAddJournalEntry =
                        { _, _, _ -> }
                )
            }
        }

        composeRule
            .onNodeWithText(
                "Stop session"
            )
            .performClick()

        composeRule
            .onNode(
                hasSetTextAction()
            )
            .performTextReplacement(
                "301"
            )

        composeRule
            .onNodeWithText(
                "Save session"
            )
            .assertIsNotEnabled()

        composeRule
            .onNodeWithText(
                "Page must be between 50 and 300"
            )
            .assertExists()
    }

    private fun testBook() =
        Book(
            id = 1,
            title = "Dune",
            author = "Frank Herbert",
            status = ReadingStatus.READING,
            ownership =
                BookOwnership.OWNED,
            currentPage = 50,
            totalPages = 300
        )
}