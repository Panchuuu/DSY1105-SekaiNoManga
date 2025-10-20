package com.example.mangasduocuc.repository

import com.example.mangasduocuc.model.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InMemoryMangasRepository : MangasRepository {
    private val _mangas = MutableStateFlow<List<Manga>>(emptyList())
    override val mangas = _mangas.asStateFlow()

    override suspend fun getById(id: String): Manga? =
        _mangas.value.firstOrNull { it.id == id }

    override suspend fun insert(book: Manga): String {
        val id = if (book.id.isBlank()) UUID.randomUUID().toString() else book.id
        val toInsert = book.copy(id = id)
        _mangas.update { current -> current + toInsert }
        return id
    }

    override suspend fun update(book: Manga): Boolean {
        var updated = false
        _mangas.update { current ->
            val idx = current.indexOfFirst { it.id == book.id }
            if (idx >= 0) {
                updated = true
                current.toMutableList().apply { set(idx, book) }
            } else current
        }
        return updated
    }

    override suspend fun delete(id: String): Boolean {
        var removed = false
        _mangas.update { current ->
            val before = current.size
            val after = current.filterNot { it.id == id }
            removed = after.size != before
            after
        }
        return removed
    }
}