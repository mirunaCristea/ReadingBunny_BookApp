package com.example.readingbunny.data.remote.dto

data class OpenLibrarySearchResponse(
    val docs: List<OpenLibraryBookDto> = emptyList()
)