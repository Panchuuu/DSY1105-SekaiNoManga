package com.example.mangasduocuc.navigation

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Mangas : Routes("mangas")
    data object MangaForm : Routes("manga_form")
    data object MangaDetails : Routes("manga_details/{id}") {
        fun create(id: Long) = "manga_details/$id"
    }
}