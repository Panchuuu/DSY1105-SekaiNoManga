package com.example.mangasduocuc.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {

    @Query("SELECT * FROM mangas ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<MangaEntity>>

    @Query("SELECT * FROM mangas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): MangaEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: MangaEntity): Long

    @Update
    suspend fun update(entity: MangaEntity): Int

    @Query("DELETE FROM mangas WHERE id = :id")
    suspend fun delete(id: Long): Int
}