package com.example.mangasduocuc.ui.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mangasduocuc.navigation.Route
import com.example.mangasduocuc.viewmodel.MangaFormViewModel
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.imePadding
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.mangasduocuc.ui.utils.ImageUriUtils

@Composable
fun MangaFormScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    vm: MangaFormViewModel = viewModel(factory = MangaFormViewModel.factory())
) {
    val ui by vm.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()
    val context = LocalContext.current

    val speechLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val text = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
            if (!text.isNullOrBlank()) {
                vm.onDescriptionChange(text)
                scope.launch { snackbarHostState.showSnackbar("Dictado agregado a Descripción") }
            }
        }
    }

    val requestAudioPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }
            speechLauncher.launch(intent)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de micrófono denegado") }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) {
            vm.clearCoverUri()
            scope.launch { snackbarHostState.showSnackbar("Foto cancelada") }
        } else {
            scope.launch { snackbarHostState.showSnackbar("Portada guardada") }
        }
    }

    val requestCameraPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = ImageUriUtils.createImageUri(context)
            vm.setCoverUri(uri.toString())
            takePictureLauncher.launch(uri)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        val hasAnyError = ui.errorByField.values.any { it != null }
        AnimatedVisibility(
            visible = hasAnyError,
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                tonalElevation = 2.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    "Revisa los campos marcados en rojo",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        OutlinedTextField(
            value = ui.title,
            onValueChange = vm::onTitleChange,
            label = { Text("Título") },
            isError = ui.errorByField["title"] != null,
            supportingText = {
                AnimatedVisibility(
                    visible = ui.errorByField["title"] != null,
                    enter = fadeIn() + expandVertically(),
                    exit  = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        ui.errorByField["title"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ui.author,
            onValueChange = vm::onAuthorChange,
            label = { Text("Autor") },
            isError = ui.errorByField["author"] != null,
            supportingText = {
                AnimatedVisibility(
                    visible = ui.errorByField["author"] != null,
                    enter = fadeIn() + expandVertically(),
                    exit  = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        ui.errorByField["author"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = ui.year,
            onValueChange = vm::onYearChange,
            label = { Text("Año") },
            isError = ui.errorByField["year"] != null,
            supportingText = {
                AnimatedVisibility(
                    visible = ui.errorByField["year"] != null,
                    enter = fadeIn() + expandVertically(),
                    exit  = fadeOut() + shrinkVertically()
                ) {
                    Text(
                        ui.errorByField["year"] ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO) },
                enabled = !ui.isSaving
            ) { Text("Dictar descripción 🎤") }

            OutlinedButton(
                onClick = {
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                },
                enabled = !ui.isSaving
            ) { Text("Tomar portada 📷") }
        }

        if (ui.coverUri != null) {
            Spacer(Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = ui.coverUri,
                    contentDescription = "Portada",
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { nav.popBackStack() },
                enabled = !ui.isSaving
            ) { Text("Cancelar") }

            Button(
                onClick = {
                    vm.submit(
                        onInserted = { id ->
                            scope.launch { snackbarHostState.showSnackbar("Manga creado") }
                            nav.navigate(Route.Details.of(id)) {
                                popUpTo(Route.Mangas.path) { inclusive = false }
                            }
                        },
                        onError = { msg ->
                            scope.launch { snackbarHostState.showSnackbar(msg) }
                        }
                    )
                },
                enabled = ui.isValid && !ui.isSaving,
                modifier = Modifier.animateContentSize()
            ) {
                if (ui.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}