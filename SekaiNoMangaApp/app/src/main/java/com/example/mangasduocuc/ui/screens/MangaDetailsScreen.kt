package com.example.mangasduocuc.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mangasduocuc.navigation.Route
import com.example.mangasduocuc.ui.theme.LightGray
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (title == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Portada del manga
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .aspectRatio(3f / 4f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = RoundedCornerShape(8.dp)
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
                                .background(LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Image, contentDescription = "Sin portada", modifier = Modifier.size(50.dp))
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Título y autor
                Text(
                    text = title ?: "-",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "por ${author ?: "-"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                year?.let {
                    Text(
                        text = "Publicado en $it",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    thickness = 1.dp,
                    color = LightGray
                )

                // Botones
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { nav.navigate(Route.Form.edit(id)) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar")
                    }
                    OutlinedButton(
                        onClick = { nav.popBackStack() },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, LightGray)
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}