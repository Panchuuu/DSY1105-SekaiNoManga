package com.example.mangasduocuc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mangasduocuc.viewmodel.MangasViewModel
import kotlinx.coroutines.launch

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
            coverUri = b.coverUri
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (title == null && author == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Imagen de portada como cabecera
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (!coverUri.isNullOrBlank()) {
                        AsyncImage(
                            model = coverUri,
                            contentDescription = "Portada de ${title ?: ""}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Sin portada",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    // Gradiente para que el texto sea legible
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = 300f
                                )
                            )
                    )
                    // Título sobre la imagen
                    Text(
                        text = title ?: "-",
                        style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }

                // Resto de la información
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Autor: ${author ?: "-"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    year?.let {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Año: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Botones
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { /* Lógica para editar (puedes implementarla más adelante) */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Editar")
                        }
                        OutlinedButton(onClick = { nav.popBackStack() }) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }
}