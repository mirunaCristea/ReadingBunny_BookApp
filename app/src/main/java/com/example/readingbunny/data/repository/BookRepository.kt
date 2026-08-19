
package com.example.readingbunny.data.repository
import androidx.room3.withWriteTransaction
import kotlinx.coroutines.flow.Flow
import com.example.readingbunny.data.local.BookDao
import com.example.readingbunny.data.local.ReadingBunnyDatabase
import com.example.readingbunny.model.Book

class BookRepository(
    private val database: ReadingBunnyDatabase
)
{
    private val bookDao =  database.bookDao()
    suspend fun addBook(book: Book) = bookDao.insertBook(book)


    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

    suspend fun updateBook(book: Book) = bookDao.updateBook(book)

    suspend fun deleteBook(
        book: Book
    ){
        database.withWriteTransaction {
            database
                .readingJournalDao()
                .deleteEntriesForBook(
                    book.id
                )
            database
                .readingSessionDao()
                .deleteSessionsForBook(
                    book.id
                )
            database
                .shelfBookPositionDao()
                .deletePosition(
                    book.id
                )
            bookDao.deleteBook(
                book
            )
        }
    }

}

