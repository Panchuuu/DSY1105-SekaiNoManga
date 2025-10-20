package com.example.mangasduocuc.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mangas")
data class MangaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val author: String,
    val year: Int? = null,
    val description: String = "",
    val coverUri: String? = null
)