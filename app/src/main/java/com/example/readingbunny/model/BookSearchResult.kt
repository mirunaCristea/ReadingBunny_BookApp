package com.example.readingbunny.model

data class BookSearchResult(
    val externalId: String,
    val title: String,
    val author: String,
    val totalPages: Int?,
    val isbn: String?,
    val coverUrl: String?,
    val largeCoverUrl: String?,
    val description: String?
)