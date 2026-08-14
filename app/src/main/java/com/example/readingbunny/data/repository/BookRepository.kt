
package com.example.readingbunny.data.repository
import kotlinx.coroutines.flow.Flow
import com.example.readingbunny.data.local.BookDao
import com.example.readingbunny.model.Book

class BookRepository(
    private val bookDao: BookDao
)
{
    suspend fun addBook(book: Book) = bookDao.insertBook(book)


    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(book: Book) = bookDao.deleteBook(book)

}

