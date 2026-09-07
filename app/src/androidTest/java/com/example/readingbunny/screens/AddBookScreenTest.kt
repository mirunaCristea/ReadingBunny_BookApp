package com.example.readingbunny.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.remember
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.readingbunny.data.remote.GoogleBooksApi
import com.example.readingbunny.data.remote.OpenLibraryApi
import com.example.readingbunny.data.remote.dto.GoogleBooksResponse
import com.example.readingbunny.data.remote.dto.OpenLibrarySearchResponse
import com.example.readingbunny.data.repository.BookSearchRepository
import com.example.readingbunny.model.Book
import com.example.readingbunny.model.BookOwnership
import com.example.readingbunny.model.ReadingStatus
import com.example.readingbunny.ui.screens.AddBookScreen
import com.example.readingbunny.ui.theme.ReadingBunnyTheme
import com.example.readingbunny.ui.viewmodel.BookSearchViewModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import androidx.compose.ui.test.onNodeWithTag

class AddBookScreenTest {

    @get:Rule
    val composeRule =
        createAndroidComposeRule<ComponentActivity>()

    @Test
    fun manualAdd_savesValidBook() {

        var savedBook: Book? = null

        composeRule.setContent {

            val searchViewModel =
                remember {
                    createSearchViewModel()
                }

            ReadingBunnyTheme {

                AddBookScreen(
                    onBackClick = {},
                    onSaveBook = {
                        savedBook = it
                    },
                    bookSearchViewModel =
                        searchViewModel
                )
            }
        }

        composeRule
            .onNodeWithText(
                "Add manually"
            )
            .performClick()

        val textFields =
            composeRule.onAllNodes(
                hasSetTextAction()
            )

        textFields[0]
            .performTextInput(
                "The Hobbit"
            )

        textFields[1]
            .performTextInput(
                "J.R.R. Tolkien"
            )

        textFields[2]
            .performTextInput(
                "310"
            )

        composeRule
            .onNodeWithTag(
                "reading_status_dropdown"
            )
            .performClick()

        composeRule
            .onNodeWithText(
                "Currently Reading"
            )
            .performClick()

        composeRule
            .onNodeWithTag(
                "book_ownership_dropdown"
            )
            .performClick()

        composeRule
            .onNodeWithText(
                "Owned"
            )
            .performClick()

        composeRule
            .onNodeWithText(
                "Save book"
            )
            .performClick()

        composeRule.runOnIdle {

            Assert.assertNotNull(savedBook)

            Assert.assertEquals(
                "The Hobbit",
                savedBook?.title
            )

            Assert.assertEquals(
                "J.R.R. Tolkien",
                savedBook?.author
            )

            Assert.assertEquals(
                310,
                savedBook?.totalPages
            )

            Assert.assertEquals(
                ReadingStatus.READING,
                savedBook?.status
            )

            Assert.assertEquals(
                BookOwnership.OWNED,
                savedBook?.ownership
            )
        }
    }

    private fun createSearchViewModel():
            BookSearchViewModel {

        val googleApi =
            object : GoogleBooksApi {

                override suspend fun searchBooks(
                    query: String,
                    maxResults: Int,
                    apiKey: String
                ): GoogleBooksResponse {

                    return GoogleBooksResponse()
                }
            }

        val openLibraryApi =
            object : OpenLibraryApi {

                override suspend fun searchBooks(
                    query: String,
                    fields: String,
                    limit: Int
                ): OpenLibrarySearchResponse {

                    return OpenLibrarySearchResponse()
                }
            }

        val repository =
            BookSearchRepository(
                api = googleApi,
                apiKey = "fake",
                openLibraryApi =
                    openLibraryApi
            )

        return BookSearchViewModel(
            repository
        )
    }
}