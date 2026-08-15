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

data class BookSearchUiState(
    val results: List<BookSearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
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
                    errorMessage = null
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
                        errorMessage = exception.message
                            ?: exception::class.simpleName
                            ?: "Unknown error"
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
                    errorMessage = null
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
                            exception.message ?: "Book lookup failed"
                    )
                }
            }
        }
    }
}
