package com.example.mangasduocuc.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangasduocuc.viewmodel.MangasViewModel
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.ui.draw.clip

@Composable
fun MangaDetailsScreen(
    nav: NavController,
    id: String,
    snackbarHostState: SnackbarHostState,
    vm: MangasViewModel = viewModel(factory = MangasViewModel.factory())
) {
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf<String?>(null) }
    var author by remember { mutableStateOf<String?>(null) }
    var year by remember { mutableStateOf<Int?>(null) }
    var coverUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        val b = vm.getMangaById(id)
        if (b == null) {
            scope.launch { snackbarHostState.showSnackbar("Manga no encontrado") }
            nav.popBackStack()
        } else {
            title = b.title
            author = b.author
            year = b.year
            year = b.year
            coverUri = b.coverUri
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (title == null && author == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                if (!coverUri.isNullOrBlank()) {
                    AsyncImage(
                        model = coverUri,
                        contentDescription = "Portada de ${title ?: ""}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Sin portada",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(text = title ?: "-", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(6.dp))
                Text(text = "Autor: ${author ?: "-"}", style = MaterialTheme.typography.bodyLarge)
                year?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(text = "Año: $it", style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { nav.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}