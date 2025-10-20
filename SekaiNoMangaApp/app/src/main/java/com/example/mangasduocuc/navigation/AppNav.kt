package com.example.mangasduocuc.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.example.mangasduocuc.ui.screens.MangaDetailsScreen
import com.example.mangasduocuc.ui.screens.MangaFormScreen
import com.example.mangasduocuc.ui.screens.MangaSummaryScreen
import com.example.mangasduocuc.ui.screens.MangasScreen
import com.example.mangasduocuc.ui.screens.HomeScreen

sealed class Route(val path: String) {
    data object Home    : Route("home")
    data object Mangas   : Route("mangas")
    data object Details : Route("details/{id}") {
        fun of(id: String) = "details/$id"
    }
    data object Form    : Route("mangaForm")
    data object Summary : Route("mangaSummary")
}

private fun titleFor(route: String?): String = when {
    route == null -> "Mangas"
    route.startsWith(Route.Home.path) -> "Inicio"
    route.startsWith(Route.Mangas.path) -> "Mangas"
    route.startsWith("details/") -> "Detalle"
    route.startsWith(Route.Form.path) -> "Nuevo manga"
    route.startsWith(Route.Summary.path) -> "Resumen"
    else -> "Mangas"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNav(windowSizeClass: WindowSizeClass) {
    val nav = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val canNavigateBack = nav.previousBackStackEntry != null

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(titleFor(currentRoute)) },
                navigationIcon = {
                    if (canNavigateBack && currentRoute != Route.Home.path) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = nav,
            startDestination = Route.Home.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.path) {
                HomeScreen(nav = nav, snackbarHostState = snackbarHostState)
            }
            composable(Route.Mangas.path) {
                MangasScreen(
                    nav = nav,
                    snackbarHostState = snackbarHostState,
                    windowSizeClass = windowSizeClass
                )
            }
            composable(
                route = Route.Details.path,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                MangaDetailsScreen(nav = nav, id = id, snackbarHostState = snackbarHostState)
            }
            composable(Route.Form.path) {
                MangaFormScreen(nav = nav, snackbarHostState = snackbarHostState)
            }
            composable(Route.Summary.path) {
                MangaSummaryScreen(nav = nav, snackbarHostState = snackbarHostState)
            }
        }
    }
}