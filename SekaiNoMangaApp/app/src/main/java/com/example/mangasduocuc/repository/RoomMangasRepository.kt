package com.example.mangasduocuc.repository

import com.example.mangasduocuc.data.local.AppDatabase
import com.example.mangasduocuc.data.local.toEntity
import com.example.mangasduocuc.data.local.toModel
import com.example.mangasduocuc.model.Manga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomMangasRepository(
    db: AppDatabase
) : MangasRepository {

    private val dao = db.mangaDao()

    override val mangas: Flow<List<Manga>> =
        dao.observeAll().map { list -> list.map { it.toModel() } }

    override suspend fun getById(id: String): Manga? {
        val longId = id.toLongOrNull() ?: return null
        return dao.getById(longId)?.toModel()
    }

    override suspend fun insert(book: Manga): String {
        val entity = book.toEntity()
        val generatedId = dao.insert(entity)
        return generatedId.toString()
    }

    override suspend fun update(book: Manga): Boolean {
        val rows = dao.update(book.toEntity())
        return rows > 0
    }

    override suspend fun delete(id: String): Boolean {
        val longId = id.toLongOrNull() ?: return false
        val rows = dao.delete(longId)
        return rows > 0
    }
}