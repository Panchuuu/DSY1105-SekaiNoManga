package com.example.mangasduocuc.model

data class Manga(
    val id: String,
    val title: String,
    val author: String,
    val year: Int? = null,
    val coverUri: String? = null
)