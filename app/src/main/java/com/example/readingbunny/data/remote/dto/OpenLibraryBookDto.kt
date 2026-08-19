package com.example.readingbunny.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenLibraryBookDto(
    val key: String,
    val title: String?,

    @SerializedName("author_name")
    val authors: List<String>?,

    val isbn: List<String>?,

    @SerializedName("cover_i")
    val coverId: Long?,

    @SerializedName("number_of_pages_median")
    val numberOfPages: Int?
)