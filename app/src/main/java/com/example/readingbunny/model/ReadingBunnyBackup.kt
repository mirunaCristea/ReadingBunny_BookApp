package com.example.readingbunny.model

data class ReadingBunnyBackup(
    val backupVersion: Int = 1,
    val exportedAt: Long,
    val books: List<Book>,
    val readingSessions: List<ReadingSession>,
    val shelfDecorations: List<ShelfDecoration>,
    val shelfBookPositions: List<ShelfBookPosition>,
    val dailyGoalMinutes: Int
)