package com.example.mangasduocuc.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mangasduocuc.di.RepositoryProvider
import com.example.mangasduocuc.model.Manga
import com.example.mangasduocuc.repository.MangasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Year

data class MangaFormUiState(
    val id: String? = null, // Guardamos el ID para saber si es edición
    val title: String = "",
    val author: String = "",
    val year: String = "",
    val description: String = "",
    val coverUri: String? = null,
    val errorByField: Map<String, String?> = emptyMap(),
    val isValid: Boolean = false,
    val isSaving: Boolean = false,
    val isDirty: Boolean = false
)

class MangaFormViewModel(
    private val repo: MangasRepository,
    private val mangaId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(MangaFormUiState())
    val uiState: StateFlow<MangaFormUiState> = _uiState.asStateFlow()

    init {
        if (mangaId != null) {
            loadManga(mangaId)
        }
    }

    private fun loadManga(id: String) {
        viewModelScope.launch {
            val manga = repo.getById(id)
            if (manga != null) {
                _uiState.update {
                    it.copy(
                        id = manga.id,
                        title = manga.title,
                        author = manga.author,
                        year = manga.year?.toString() ?: "",
                        coverUri = manga.coverUri
                    )
                }
                validate()
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, isDirty = true) }
        validate()
    }

    fun onAuthorChange(value: String) {
        _uiState.update { it.copy(author = value, isDirty = true) }
        validate()
    }

    fun onYearChange(value: String) {
        _uiState.update { it.copy(year = value, isDirty = true) }
        validate()
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value, isDirty = true) }
        validate()
    }

    fun setCoverUri(uri: String?) {
        _uiState.update { it.copy(coverUri = uri, isDirty = true) }
        validate()
    }

    fun clearCoverUri() {
        _uiState.update { it.copy(coverUri = null, isDirty = true) }
        validate()
    }

    private fun validate() {
        val s = _uiState.value
        val errs = mutableMapOf<String, String?>()

        if (s.title.isBlank()) errs["title"] = "El título es obligatorio"
        else if (s.title.length < 2) errs["title"] = "Mínimo 2 caracteres"

        if (s.author.isBlank()) errs["author"] = "El autor es obligatorio"
        else if (s.author.length < 2) errs["author"] = "Mínimo 2 caracteres"

        val yearErr = validateYear(s.year)
        if (yearErr != null) errs["year"] = yearErr

        if (s.description.isNotBlank() && s.description.length < 5) {
            errs["description"] = "Si agregas descripción, usa al menos 5 caracteres"
        }

        val valid = errs.values.all { it == null } &&
                s.title.isNotBlank() &&
                s.author.isNotBlank() &&
                s.year.isNotBlank()

        _uiState.update { it.copy(errorByField = errs, isValid = valid) }
    }

    private fun validateYear(value: String): String? {
        if (value.isBlank()) return "El año es obligatorio"
        val yr = value.toIntOrNull() ?: return "Debe ser numérico"
        val current = Year.now().value
        val min = 1400
        val max = current + 1
        return if (yr in min..max) null else "Debe estar entre $min y $max"
    }

    fun submit(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val s = _uiState.value
        if (!s.isValid || s.isSaving) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val yearInt = s.year.toIntOrNull()
                val manga = Manga(
                    id = s.id ?: "",
                    title = s.title.trim(),
                    author = s.author.trim(),
                    year = yearInt,
                    coverUri = s.coverUri
                )

                if (s.id == null) {
                    // Crear nuevo manga
                    val newId = repo.insert(manga)
                    onSuccess(newId)
                } else {
                    repo.update(manga)
                    onSuccess(s.id)
                }

                _uiState.update { it.copy(isSaving = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
                onError("No se pudo guardar: ${e.message ?: "Error desconocido"}")
            }
        }
    }

    companion object {
        fun factory(mangaId: String?): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = RepositoryProvider.mangasRepository
                return MangaFormViewModel(repo, mangaId) as T
            }
        }
    }
}