package com.example.mangasduocuc.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mangasduocuc.model.Manga
import com.example.mangasduocuc.navigation.Route
import com.example.mangasduocuc.viewmodel.MangasViewModel
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

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
                    EmptyState(
                        onCreate = { nav.navigate(Route.Form.path) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(vertical = 4.dp)
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
                                MangaListItem(
                                    manga = manga,
                                    onClick = { nav.navigate(Route.Details.of(manga.id)) },
                                    onDelete = { idToDelete ->
                                        vm.deleteManga(idToDelete) { ok: Boolean ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    if (ok) "Manga eliminado"
                                                    else "No se pudo eliminar"
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    onCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Image,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(8.dp))
        Text("Aún no hay mangas", style = MaterialTheme.typography.titleMedium)
        Text(
            "Crea tu primer manga para comenzar.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onCreate) { Text("Crear primer manga") }
    }
}

@Composable
private fun MangaListItem(
    manga: Manga,
    onClick: () -> Unit,
    onDelete: (String) -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!manga.coverUri.isNullOrBlank()) {
                AsyncImage(
                    model = manga.coverUri,
                    contentDescription = "Portada de ${manga.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = "Sin portada",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    manga.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )


                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    manga.author?.takeIf { it.isNotBlank() }?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text(it) }
                        )
                    }
                    manga.year?.let {
                        AssistChip(
                            onClick = {},
                            label = { Text("Año $it") }
                        )
                    }
                }
            }


            IconButton(onClick = { showConfirm = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Eliminar manga") },
            text = { Text("¿Seguro que deseas eliminar \"${manga.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirm = false
                        onDelete(manga.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancelar") }
            }
        )
    }
}
