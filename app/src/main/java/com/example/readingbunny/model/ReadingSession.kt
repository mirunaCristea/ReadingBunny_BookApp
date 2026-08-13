package com.example.readingbunny.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "reading_sessions")
data class ReadingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val bookId: Int,

    val startedAt: Long,
    val endedAt: Long,

    val startPage: Int,
    val endPage: Int,

    val durationSeconds: Long
)