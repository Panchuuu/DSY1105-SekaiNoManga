package com.example.mangasduocuc.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mangasduocuc.model.Manga
import com.example.mangasduocuc.navigation.Route
import com.example.mangasduocuc.ui.theme.InkBlack
import com.example.mangasduocuc.ui.theme.LightGray
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
                        Text("No hay mangas en tu colección", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Añade tu primer manga para empezar.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { nav.navigate(Route.Form.new()) }) {
                            Text("Añadir Manga")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = mangas,
                            key = { it.id }
                        ) { manga ->
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

@Composable
fun MangaListItem(manga: Manga, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp), // Bordes rectos
        border = BorderStroke(1.dp, LightGray)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Información del Manga
            Column(modifier = Modifier.weight(1f)) {
                Text(manga.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(manga.author, style = MaterialTheme.typography.titleMedium)
                manga.year?.let {
                    Text("$it", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Portada del Manga
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                if (!manga.coverUri.isNullOrBlank()) {
                    AsyncImage(
                        model = manga.coverUri,
                        contentDescription = "Portada de ${manga.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = "Sin portada",
                            modifier = Modifier.size(40.dp),
                            tint = LightGray
                        )
                    }
                }
            }
        }
    }
}