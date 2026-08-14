package com.example.readingbunny.data.remote

import com.example.readingbunny.data.remote.dto.GoogleBooksResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GoogleBooksApi {

    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 20,
        @Header("X-Goog-Api-Key") apiKey: String
    ): GoogleBooksResponse
}