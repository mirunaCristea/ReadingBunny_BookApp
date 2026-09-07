package com.example.readingbunny.data.repository

import com.example.readingbunny.data.remote.GoogleBooksApi
import com.example.readingbunny.data.remote.OpenLibraryApi
import com.example.readingbunny.data.remote.dto.GoogleBookItemDto
import com.example.readingbunny.data.remote.dto.GoogleBooksResponse
import com.example.readingbunny.data.remote.dto.GoogleVolumeInfoDto
import com.example.readingbunny.data.remote.dto.ImageLinksDto
import com.example.readingbunny.data.remote.dto.IndustryIdentifierDto
import com.example.readingbunny.data.remote.dto.OpenLibraryBookDto
import com.example.readingbunny.data.remote.dto.OpenLibrarySearchResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class BookSearchRepositoryTest {

    @Test
    fun searchBooks_returnsEmptyList_whenQueryIsBlank() = runBlocking {
        val repository = createRepository()

        val result = repository.searchBooks("   ")

        assertTrue(result.isEmpty())
    }

    @Test
    fun searchBooks_mapsGoogleBookCorrectly() = runBlocking {
        val repository = createRepository(
            googleBlock = {
                GoogleBooksResponse(
                    items = listOf(
                        GoogleBookItemDto(
                            id = "book123",
                            volumeInfo = GoogleVolumeInfoDto(
                                title = "The Hobbit",
                                authors = listOf("J.R.R. Tolkien"),
                                description = "A fantasy novel",
                                pageCount = 310,
                                industryIdentifiers = listOf(
                                    IndustryIdentifierDto(
                                        type = "ISBN_13",
                                        identifier = "9780000000001"
                                    )
                                )
                            )
                        )
                    )
                )
            }
        )

        val result = repository.searchBooks("hobbit")

        assertEquals(1, result.size)

        val book = result.first()

        assertEquals("book123", book.externalId)
        assertEquals("The Hobbit", book.title)
        assertEquals("J.R.R. Tolkien", book.author)
        assertEquals(310, book.totalPages)
        assertEquals("9780000000001", book.isbn)
        assertEquals("A fantasy novel", book.description)
    }

    @Test
    fun searchBooks_usesUnknownAuthor_whenAuthorIsMissing() = runBlocking {
        val repository = createRepository(
            googleBlock = {
                GoogleBooksResponse(
                    items = listOf(
                        GoogleBookItemDto(
                            id = "book1",
                            volumeInfo = GoogleVolumeInfoDto(
                                title = "Unknown Book",
                                authors = null
                            )
                        )
                    )
                )
            }
        )

        val result = repository.searchBooks("unknown")

        assertEquals("Unknown author", result.first().author)
    }

    @Test
    fun searchBooks_ignoresBook_whenTitleIsBlank() = runBlocking {
        val repository = createRepository(
            googleBlock = {
                GoogleBooksResponse(
                    items = listOf(
                        GoogleBookItemDto(
                            id = "book1",
                            volumeInfo = GoogleVolumeInfoDto(
                                title = "   "
                            )
                        )
                    )
                )
            }
        )

        val result = repository.searchBooks("test")

        assertTrue(result.isEmpty())
    }

    @Test
    fun searchBooks_prefersIsbn13_overIsbn10() = runBlocking {
        val repository = createRepository(
            googleBlock = {
                GoogleBooksResponse(
                    items = listOf(
                        GoogleBookItemDto(
                            id = "book1",
                            volumeInfo = GoogleVolumeInfoDto(
                                title = "Test Book",
                                industryIdentifiers = listOf(
                                    IndustryIdentifierDto(
                                        type = "ISBN_10",
                                        identifier = "1234567890"
                                    ),
                                    IndustryIdentifierDto(
                                        type = "ISBN_13",
                                        identifier = "9781234567890"
                                    )
                                )
                            )
                        )
                    )
                )
            }
        )

        val result = repository.searchBooks("test")

        assertEquals(
            "9781234567890",
            result.first().isbn
        )
    }

    @Test
    fun searchBooks_convertsCoverUrlToHttps() = runBlocking {
        val repository = createRepository(
            googleBlock = {
                GoogleBooksResponse(
                    items = listOf(
                        GoogleBookItemDto(
                            id = "book1",
                            volumeInfo = GoogleVolumeInfoDto(
                                title = "Test Book",
                                imageLinks = ImageLinksDto(
                                    thumbnail =
                                        "http://example.com/cover.jpg"
                                )
                            )
                        )
                    )
                )
            }
        )

        val result = repository.searchBooks("test")

        assertEquals(
            "https://example.com/cover.jpg",
            result.first().coverUrl
        )
    }

    @Test
    fun searchBooksWithFallback_usesOpenLibrary_whenGoogleReturnsNothing() =
        runBlocking {

            val repository = createRepository(
                googleBlock = {
                    GoogleBooksResponse(
                        items = emptyList()
                    )
                },
                openLibraryBlock = {
                    OpenLibrarySearchResponse(
                        docs = listOf(
                            OpenLibraryBookDto(
                                key = "/works/OL123W",
                                title = "Dune",
                                authors = listOf(
                                    "Frank Herbert"
                                ),
                                isbn = listOf(
                                    "9780441172719"
                                ),
                                coverId = 12345L,
                                numberOfPages = 412
                            )
                        )
                    )
                }
            )

            val result =
                repository.searchBooksWithFallback(
                    "Dune"
                )

            assertEquals(1, result.size)

            val book = result.first()

            assertEquals(
                "openlibrary:/works/OL123W",
                book.externalId
            )

            assertEquals("Dune", book.title)
            assertEquals(
                "Frank Herbert",
                book.author
            )

            assertEquals(
                "9780441172719",
                book.isbn
            )

            assertEquals(
                412,
                book.totalPages
            )
        }

    @Test
    fun searchBookByIsbn_usesOpenLibrary_whenGoogleReturnsNothing() =
        runBlocking {

            val repository = createRepository(
                googleBlock = {
                    GoogleBooksResponse(
                        items = emptyList()
                    )
                },
                openLibraryBlock = {
                    OpenLibrarySearchResponse(
                        docs = listOf(
                            OpenLibraryBookDto(
                                key = "/works/OL456W",
                                title = "Example",
                                authors = listOf(
                                    "Example Author"
                                ),
                                isbn = null,
                                coverId = null,
                                numberOfPages = 200
                            )
                        )
                    )
                }
            )

            val result =
                repository.searchBookByIsbn(
                    "9781111111111"
                )

            assertEquals(1, result.size)

            assertEquals(
                "9781111111111",
                result.first().isbn
            )
        }

    @Test
    fun searchBooks_retriesAfterIOException() =
        runBlocking {

            var attempts = 0

            val repository = createRepository(
                googleBlock = {

                    attempts++

                    if (attempts < 3) {
                        throw IOException(
                            "Temporary network problem"
                        )
                    }

                    GoogleBooksResponse(
                        items = listOf(
                            GoogleBookItemDto(
                                id = "book1",
                                volumeInfo =
                                    GoogleVolumeInfoDto(
                                        title =
                                            "Retry Success"
                                    )
                            )
                        )
                    )
                }
            )

            val result =
                repository.searchBooks("retry")

            assertEquals(3, attempts)

            assertEquals(
                "Retry Success",
                result.first().title
            )
        }

    private fun createRepository(
        googleBlock:
        suspend (String) ->
        GoogleBooksResponse = {
            GoogleBooksResponse()
        },

        openLibraryBlock:
        suspend (String) ->
        OpenLibrarySearchResponse = {
            OpenLibrarySearchResponse()
        }
    ): BookSearchRepository {

        val googleApi =
            object : GoogleBooksApi {

                override suspend fun searchBooks(
                    query: String,
                    maxResults: Int,
                    apiKey: String
                ): GoogleBooksResponse {

                    return googleBlock(query)
                }
            }

        val openLibraryApi =
            object : OpenLibraryApi {

                override suspend fun searchBooks(
                    query: String,
                    fields: String,
                    limit: Int
                ): OpenLibrarySearchResponse {

                    return openLibraryBlock(query)
                }
            }

        return BookSearchRepository(
            api = googleApi,
            apiKey = "fake-key",
            openLibraryApi = openLibraryApi
        )
    }
}