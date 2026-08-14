package com.example.readingbunny.data.repository

import com.example.readingbunny.data.remote.GoogleBooksApi
import com.example.readingbunny.model.BookSearchResult

class BookSearchRepository(
    private val api: GoogleBooksApi,
    private val apiKey: String
) {

    suspend fun searchBooks(
        query: String
    ): List<BookSearchResult> {

        if (query.isBlank()) {
            return emptyList()
        }

        val response = api.searchBooks(
            query = query.trim(),
            apiKey = apiKey
        )

        return response.items
            .orEmpty()
            .mapNotNull { item ->

                val volumeInfo = item.volumeInfo

                val title =
                    volumeInfo.title
                        ?.trim()
                        .orEmpty()

                if (title.isBlank()) {
                    return@mapNotNull null
                }

                val author =
                    volumeInfo.authors
                        ?.joinToString(", ")
                        ?: "Unknown author"

                val isbn =
                    volumeInfo.industryIdentifiers
                        ?.firstOrNull {
                            it.type == "ISBN_13"
                        }
                        ?.identifier
                        ?: volumeInfo.industryIdentifiers
                            ?.firstOrNull {
                                it.type == "ISBN_10"
                            }
                            ?.identifier

                val coverUrl =
                    volumeInfo.imageLinks
                        ?.thumbnail
                        ?.replaceFirst(
                            "http://",
                            "https://"
                        )

                BookSearchResult(
                    externalId = item.id,
                    title = title,
                    author = author,
                    totalPages = volumeInfo.pageCount,
                    isbn = isbn,
                    coverUrl = coverUrl,
                    description = volumeInfo.description
                )
            }
    }
}