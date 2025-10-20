package com.example.mangasduocuc.data.local

import com.example.mangasduocuc.model.Manga


fun MangaEntity.toModel() = Manga(
    id = id.toString(),
    title = title,
    author = author,
    year = year,
    coverUri = coverUri
)

fun Manga.toEntity() = MangaEntity(
    id = id.toLongOrNull() ?: 0L,
    title = title,
    author = author,
    year = year,
    description = "",
    coverUri = coverUri
)