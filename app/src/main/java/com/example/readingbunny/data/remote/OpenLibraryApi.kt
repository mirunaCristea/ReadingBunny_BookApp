package com.example.readingbunny.data.remote

import com.example.readingbunny.data.remote.dto.OpenLibrarySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String =
            "key,title,author_name,isbn,cover_i,number_of_pages_median",
        @Query("limit") limit: Int = 5
    ): OpenLibrarySearchResponse
}