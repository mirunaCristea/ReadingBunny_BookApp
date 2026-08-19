package com.example.readingbunny.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenLibraryApiClient {

    private const val BASE_URL =
        "https://openlibrary.org/"

    val api: OpenLibraryApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(OpenLibraryApi::class.java)
    }
}