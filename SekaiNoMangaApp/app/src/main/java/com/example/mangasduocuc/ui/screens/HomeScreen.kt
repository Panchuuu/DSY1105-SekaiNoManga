package com.example.mangasduocuc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.ucb.mangas.R //
import com.example.mangasduocuc.navigation.Route

@Composable
fun HomeScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de bienvenida
        Text(
            text = "Bienvenido a\nSekaiNoManga",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Logo de la tienda
        Image(
            painter = painterResource(id = R.drawable.logo_tienda_sekai),
            contentDescription = "Logo de la tienda",
            modifier = Modifier
                .height(150.dp)
                .fillMaxWidth(0.7f)
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = { nav.navigate(Route.Mangas.path) },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Ver mi colección")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = { nav.navigate(Route.Form.new()) },
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Añadir nuevo manga")
        }
    }
}