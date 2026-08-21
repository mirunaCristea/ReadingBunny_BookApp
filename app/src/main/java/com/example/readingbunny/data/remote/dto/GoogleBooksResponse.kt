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
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null,
)