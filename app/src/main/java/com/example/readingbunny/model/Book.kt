package com.example.readingbunny.model
import androidx.room3.Entity
import androidx.room3.PrimaryKey
enum class ReadingStatus{
    UNREAD,
    READING,
    WANT_TO_READ,
    FINISHED,
    DNF

}

enum class BookOwnership {
    OWNED,
    BORROWED,
    WISHLIST
}

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val author: String,
    val status: ReadingStatus,
    val ownership: BookOwnership,
    val currentPage: Int,
    val totalPages: Int,
    val isbn: String? = null,
    val coverUrl: String? = null,
    val largeCoverUrl: String? = null,
    val description: String? = null
){
    private fun ReadingStatus.displayName(): String {
        return when (this) {
            ReadingStatus.READING -> "Currently Reading"
            ReadingStatus.WANT_TO_READ -> "Want to Read"
            ReadingStatus.FINISHED -> "Finished"
            ReadingStatus.DNF -> "Did Not Finish"
            ReadingStatus.UNREAD -> "Unread"
        }
    }

    private fun BookOwnership.displayName(): String {
        return when (this) {
            BookOwnership.WISHLIST -> "Wishlist"
            BookOwnership.BORROWED -> "Borrowed"
            BookOwnership.OWNED -> "Owned"


        }
    }

}
