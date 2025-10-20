package com.example.mangasduocuc.di

import android.content.Context
import com.example.mangasduocuc.data.local.AppDatabase
import com.example.mangasduocuc.repository.MangasRepository
import com.example.mangasduocuc.repository.RoomMangasRepository

object RepositoryProvider {

    @Volatile
    private var _mangasRepository: MangasRepository? = null

    fun init(context: Context) {
        if (_mangasRepository == null) {
            synchronized(this) {
                if (_mangasRepository == null) {
                    val db = AppDatabase.build(context)
                    _mangasRepository = RoomMangasRepository(db)
                }
            }
        }
    }

    val mangasRepository: MangasRepository
        get() = _mangasRepository
            ?: error("RepositoryProvider no inicializado. Llama a init(context) primero.")
}