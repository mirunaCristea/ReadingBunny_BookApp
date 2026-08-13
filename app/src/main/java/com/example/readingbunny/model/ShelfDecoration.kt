package com.example.readingbunny.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

enum class DecorationType(
    val displayName: String,
    val emoji: String
) {
    PLANT("Plant", "🌱"),
    CANDLE("Candle", "🕯️"),
    LAMP("Lamp", "💡"),
    FRAME("Frame", "🖼️")
}

@Entity(tableName = "shelf_decorations")
data class ShelfDecoration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val type: DecorationType,
    val slotIndex: Int,
    val shelfIndex: Int
)

