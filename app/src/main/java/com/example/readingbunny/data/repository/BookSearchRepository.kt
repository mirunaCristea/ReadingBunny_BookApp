package com.example.readingbunny.data.repository

import com.example.readingbunny.data.remote.GoogleBooksApi
import com.example.readingbunny.data.remote.OpenLibraryApi
import com.example.readingbunny.model.BookSearchResult
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

class BookSearchRepository(
    private val api: GoogleBooksApi,
    private val apiKey: String,
    private val openLibraryApi: OpenLibraryApi,
) {

    suspend fun searchBooks(
        query: String
    ): List<BookSearchResult> {

        if (query.isBlank()) {
            return emptyList()
        }

        val response = retryTransientRequest {
            api.searchBooks(
                query = query.trim(),
                apiKey = apiKey
            )
        }

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

    suspend fun searchBookByIsbn(
        isbn: String
    ): List<BookSearchResult> {

        val cleanIsbn = isbn.trim()

        if (cleanIsbn.isBlank()) {
            return emptyList()
        }

        val googleResults =
            searchBooks("isbn:$cleanIsbn")

        if (googleResults.isNotEmpty()) {
            return googleResults
        }

        return searchOpenLibrary(
            query = "isbn:$cleanIsbn",
            fallbackIsbn = cleanIsbn
        )
    }

    private suspend fun searchOpenLibrary(
        query: String,
        fallbackIsbn: String? = null
    ): List<BookSearchResult> {

        val response = retryTransientRequest {
            openLibraryApi.searchBooks(
                query = query
            )
        }

        return response.docs.mapNotNull { book ->

            val title = book.title
                ?.trim()
                .orEmpty()

            if (title.isBlank()) {
                return@mapNotNull null
            }

            val author =
                book.authors
                    ?.joinToString(", ")
                    ?: "Unknown author"

            val isbn =
                fallbackIsbn
                    ?: book.isbn
                        ?.firstOrNull { value ->
                            value.length == 13
                        }
                    ?: book.isbn
                        ?.firstOrNull()

            val coverUrl =
                book.coverId?.let { coverId ->
                    "https://covers.openlibrary.org/b/id/$coverId-M.jpg"
                }

            BookSearchResult(
                externalId = "openlibrary:${book.key}",
                title = title,
                author = author,
                totalPages = book.numberOfPages,
                isbn = isbn,
                coverUrl = coverUrl,
                description = null
            )
        }
    }

    suspend fun searchBooksWithFallback(
        query: String
    ): List<BookSearchResult> {

        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            return emptyList()
        }

        val googleResults = searchBooks(cleanQuery)

        if (googleResults.isNotEmpty()) {
            return googleResults
        }

        return searchOpenLibrary(cleanQuery)
    }


    private suspend fun <T> retryTransientRequest(
        maxRetries: Int = 2,
        initialDelayMillis: Long = 500L,
        request: suspend () -> T
    ): T {
        var retryCount = 0
        var retryDelay = initialDelayMillis

        while (true) {
            try {
                return request()
            } catch (exception: HttpException) {
                val isTemporaryError =
                    exception.code() == 502 ||
                            exception.code() == 503 ||
                            exception.code() == 504

                if (!isTemporaryError || retryCount >= maxRetries) {
                    throw exception
                }
            } catch (exception: IOException) {
                if (retryCount >= maxRetries) {
                    throw exception
                }
            }

            delay(retryDelay)

            retryCount++
            retryDelay *= 2
        }
    }
}