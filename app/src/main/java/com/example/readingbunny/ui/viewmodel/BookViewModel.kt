package com.example.readingbunny.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.readingbunny.data.repository.BookRepository
import com.example.readingbunny.model.Book
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BookViewModel(
    private val repository: BookRepository
): ViewModel() {

    val books: Flow<List<Book>> = repository.getAllBooks()

    fun addBook(book: Book) {
        viewModelScope.launch {
            repository.addBook(book)
        }
    }

    fun updateBook(book: Book) {
        viewModelScope.launch {
            repository.updateBook(book)
        }
    }

    fun deleteBook(book: Book) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }


}