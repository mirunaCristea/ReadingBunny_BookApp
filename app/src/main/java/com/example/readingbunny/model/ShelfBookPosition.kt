package com.example.readingbunny.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "shelf_book_positions")
data class ShelfBookPosition(
    @PrimaryKey
    val bookId: Int,
    val shelfIndex: Int,
    val slotIndex: Int
)