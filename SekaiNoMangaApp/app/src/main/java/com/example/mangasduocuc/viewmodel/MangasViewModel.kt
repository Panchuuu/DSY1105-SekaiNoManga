package com.example.mangasduocuc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mangasduocuc.di.RepositoryProvider
import com.example.mangasduocuc.model.Manga
import com.example.mangasduocuc.repository.MangasRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MangasViewModel(
    private val repo: MangasRepository
) : ViewModel() {

    val mangas: StateFlow<List<Manga>> =
        repo.mangas.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun addManga(manga: Manga, onInserted: (String) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.insert(manga)
            onInserted(id)
        }
    }

    suspend fun getMangaById(id: String): Manga? = repo.getById(id)

    fun updateManga(manga: Manga, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onDone(repo.update(manga))
        }
    }

    fun deleteManga(id: String, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onDone(repo.delete(id))
        }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = RepositoryProvider.mangasRepository
                return MangasViewModel(repo) as T
            }
        }
    }
}