package com.example.readingbunny.data.remote.dto

data class GoogleBooksResponse(
    val items: List<GoogleBookItemDto>? = null
)

data class GoogleBookItemDto(
    val id: String,
    val volumeInfo: GoogleVolumeInfoDto
)

data class GoogleVolumeInfoDto(
    val title: String? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val pageCount: Int? = null,
    val industryIdentifiers: List<IndustryIdentifierDto>? = null,
    val imageLinks: ImageLinksDto? = null
)

data class IndustryIdentifierDto(
    val type: String,
    val identifier: String
)

data class ImageLinksDto(
    val thumbnail: String? = null
)