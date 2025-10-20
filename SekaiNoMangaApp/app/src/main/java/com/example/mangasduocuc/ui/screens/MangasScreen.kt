package com.example.mangasduocuc.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mangasduocuc.model.Manga
import com.example.mangasduocuc.navigation.Route
import com.example.mangasduocuc.viewmodel.MangasViewModel

@Composable
fun MangasScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    windowSizeClass: WindowSizeClass,
    vm: MangasViewModel = viewModel(factory = MangasViewModel.factory())
) {
    val mangas by vm.mangas.collectAsStateWithLifecycle(initialValue = emptyList())
    val isLoading by vm.isLoading.collectAsStateWithLifecycle(initialValue = false)

    val uiState = when {
        isLoading -> "loading"
        mangas.isEmpty() -> "empty"
        else -> "content"
    }

    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Crossfade(targetState = uiState, label = "mangas-crossfade") { state ->
            when (state) {
                "loading" -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                "empty" -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Aún no hay mangas", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Crea tu primer manga para comenzar.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { nav.navigate(Route.Form.path) }) {
                            Text("Crear primer manga")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState
                    ) {
                        items(
                            items = mangas,
                            key = { it.id }
                        ) { manga ->
                            var visible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) { visible = true }

                            AnimatedVisibility(
                                visible = visible,
                                enter = fadeIn(animationSpec = tween(durationMillis = 500))
                            ) {
                                MangaListItem(manga = manga) {
                                    nav.navigate(Route.Details.of(manga.id))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MangaListItem(manga: Manga, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Portada del Manga
            if (!manga.coverUri.isNullOrBlank()) {
                AsyncImage(
                    model = manga.coverUri,
                    contentDescription = "Portada de ${manga.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = "Sin portada",
                    modifier = Modifier.size(80.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            // Información del Manga
            Column {
                Text(manga.title, style = MaterialTheme.typography.titleLarge)
                Text(manga.author, style = MaterialTheme.typography.bodyMedium)
                manga.year?.let {
                    Text("Año: $it", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}