package com.example.readingbunny.data.remote

import com.example.readingbunny.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleBooksApiClient {

    private const val BASE_URL =
        "https://www.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader(
                    "X-Android-Package",
                    BuildConfig.APPLICATION_ID
                )
                .addHeader(
                    "X-Android-Cert",
                    "0F83DCFC5080058AB2C9B64D951E18D6D6921D5B"
                )
                .build()

            chain.proceed(request)
        }
        .build()

    val api: GoogleBooksApi by lazy {

        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(GoogleBooksApi::class.java)

    }
}