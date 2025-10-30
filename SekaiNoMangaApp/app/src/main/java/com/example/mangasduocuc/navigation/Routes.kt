package com.example.mangasduocuc.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Mangas : Routes("mangas")

    // Hacemos que el ID sea opcional para el formulario
    data object MangaForm : Routes("manga_form?id={id}") {
        fun create() = "manga_form" // Para un manga nuevo
        fun createWithId(id: String) = "manga_form?id=$id" // Para editar
    }

    data object MangaDetails : Routes("manga_details/{id}") {
        fun create(id: String) = "manga_details/$id"
    }
}