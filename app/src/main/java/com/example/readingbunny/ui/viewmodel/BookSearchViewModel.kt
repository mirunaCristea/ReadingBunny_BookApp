package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readingbunny.data.repository.BookSearchRepository
import com.example.readingbunny.model.BookSearchResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.util.Log
import retrofit2.HttpException
import java.io.IOException

data class BookSearchUiState(
    val results: List<BookSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasSearched: Boolean = false
)

class BookSearchViewModel(
    private val repository: BookSearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookSearchUiState())
    val uiState: StateFlow<BookSearchUiState> = _uiState.asStateFlow()

    fun searchBooks(query: String) {
        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            _uiState.value = BookSearchUiState()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    hasSearched = true
                )
            }

            try {
                val results = repository.searchBooks(cleanQuery)

                _uiState.update {
                    it.copy(
                        results = results,
                        isLoading = false
                    )
                }
            } catch (exception: Exception) {
                Log.e(
                    "BookSearch",
                    "Search request failed",
                    exception
                )


                _uiState.update {
                    it.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage = exception.toFriendlySearchMessage()
                    )
                }
            }
        }
    }


    fun searchBookByIsbn(isbn: String) {

        val cleanIsbn = isbn.trim()

        if (cleanIsbn.isBlank()) {
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    hasSearched = true
                )
            }

            try {

                val results =
                    repository.searchBookByIsbn(cleanIsbn)

                _uiState.update {
                    it.copy(
                        results = results,
                        isLoading = false
                    )
                }

            } catch (exception: Exception) {

                Log.e(
                    "BookSearch",
                    "ISBN search failed",
                    exception
                )

                _uiState.update {
                    it.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage =
                            exception.toFriendlySearchMessage()
                    )
                }
            }
        }
    }


    fun searchBooksWithFallback(query: String) {

        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            _uiState.value = BookSearchUiState()
            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    hasSearched = true
                )
            }

            try {

                val results =
                    repository.searchBooksWithFallback(
                        cleanQuery
                    )

                _uiState.update {
                    it.copy(
                        results = results,
                        isLoading = false
                    )
                }

            } catch (exception: Exception) {

                Log.e(
                    "BookSearch",
                    "Fallback search failed",
                    exception
                )

                _uiState.update {
                    it.copy(
                        results = emptyList(),
                        isLoading = false,
                        errorMessage =
                            exception.toFriendlySearchMessage()
                    )
                }
            }
        }
    }
}


private fun Exception.toFriendlySearchMessage(): String {
    return when (this) {
        is IOException ->
            "Could not connect. Please check your internet connection and try again."

        is HttpException -> {
            when (code()) {
                502, 503, 504 ->
                    "The book search service is temporarily unavailable. Please try again in a moment."

                429 ->
                    "Too many search requests. Please wait a moment and try again."

                else ->
                    "Could not search for books. Please try again."
            }
        }

        else ->
            "Could not search for books. Please try again."
    }
}