package com.example.mangasduocuc.repository

import kotlinx.coroutines.flow.Flow
import com.example.mangasduocuc.model.Manga

interface MangasRepository {
    val mangas: Flow<List<Manga>>

    suspend fun getById(id: String): Manga?

    suspend fun insert(book: Manga): String

    suspend fun update(book: Manga): Boolean

    suspend fun delete(id: String): Boolean
}