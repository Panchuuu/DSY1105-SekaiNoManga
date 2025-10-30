package com.example.mangasduocuc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.ucb.mangas.R

@Composable
fun LocalImagePickerScreen(nav: NavController) {
    val localImages = listOf(
        R.drawable.logo_tienda_sekai,
        R.drawable.dan_da_dan_01
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(localImages) { imageResId ->
            Card(
                modifier = Modifier
                    .aspectRatio(3f / 4f)
                    .clickable {
                        // Devolvemos el ID del recurso a la pantalla anterior (el formulario)
                        nav.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_image_uri", imageResId)
                        nav.popBackStack()
                    },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Portada local",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}