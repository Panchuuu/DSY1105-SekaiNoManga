package com.example.mangasduocuc.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mangasduocuc.navigation.Route
import com.example.mangasduocuc.ui.theme.LightGray
import com.example.mangasduocuc.ui.utils.ImageUriUtils
import com.example.mangasduocuc.viewmodel.MangaFormViewModel
import kotlinx.coroutines.launch

@Composable
fun MangaFormScreen(
    nav: NavController,
    snackbarHostState: SnackbarHostState,
    mangaId: String? = null
) {
    val vm: MangaFormViewModel = viewModel(factory = MangaFormViewModel.factory(mangaId))
    val ui by vm.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }

    // --- LÓGICA PARA RECIBIR LA IMAGEN SELECCIONADA DE LA GALERÍA LOCAL ---
    val savedStateHandle: SavedStateHandle? = nav.currentBackStackEntry?.savedStateHandle
    val selectedImageResId = savedStateHandle?.getLiveData<Int>("selected_image_uri")

    LaunchedEffect(selectedImageResId) {
        selectedImageResId?.observeForever { resId ->
            if (resId != null) {
                // Convertimos el ID del recurso a una URI que Coil puede leer
                val uriString = "android.resource://${context.packageName}/$resId"
                vm.setCoverUri(uriString)
                // Limpiamos el valor para que no se vuelva a activar al volver a la pantalla
                savedStateHandle.remove<Int>("selected_image_uri")
            }
        }
    }

    // --- LAUNCHERS ---
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            scope.launch { snackbarHostState.showSnackbar("Portada guardada") }
        } else {
            vm.clearCoverUri()
            scope.launch { snackbarHostState.showSnackbar("Captura cancelada") }
        }
    }

    val requestCameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = ImageUriUtils.createImageUri(context)
            vm.setCoverUri(uri.toString())
            takePictureLauncher.launch(uri)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let { text ->
                vm.onDescriptionChange(text)
                scope.launch { snackbarHostState.showSnackbar("Dictado agregado") }
            }
        }
    }
    val requestAudioPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }
            speechLauncher.launch(intent)
        } else {
            scope.launch { snackbarHostState.showSnackbar("Permiso de micrófono denegado") }
        }
    }

    // --- DIÁLOGO DE ELECCIÓN DE IMAGEN ---
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar Portada") },
            text = { Text("¿Desde dónde quieres obtener la imagen?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    requestCameraPermission.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Cámara")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    nav.navigate(Route.LocalImagePicker.path)
                }) {
                    Text("Galería Local")
                }
            }
        )
    }

    // --- INTERFAZ DE USUARIO ---
    Scaffold(
        bottomBar = {
            BottomAppBar(containerColor = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = { nav.popBackStack() }) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            vm.submit(
                                onSuccess = { id ->
                                    val message = if (mangaId == null) "Manga creado" else "Manga actualizado"
                                    scope.launch { snackbarHostState.showSnackbar(message) }
                                    nav.navigate(Route.Details.of(id)) {
                                        popUpTo(Route.Mangas.path) { inclusive = false }
                                    }
                                },
                                onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                            )
                        },
                        enabled = ui.isValid && !ui.isSaving
                    ) {
                        if (ui.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoverImagePlaceholder(
                coverUri = ui.coverUri,
                onClick = { showImageSourceDialog = true }
            )
            Spacer(Modifier.height(24.dp))
            FormTextInput(
                value = ui.title,
                onValueChange = vm::onTitleChange,
                label = "Título",
                isError = ui.errorByField["title"] != null,
                supportingText = ui.errorByField["title"]
            )
            Spacer(Modifier.height(16.dp))
            FormTextInput(
                value = ui.author,
                onValueChange = vm::onAuthorChange,
                label = "Autor",
                isError = ui.errorByField["author"] != null,
                supportingText = ui.errorByField["author"]
            )
            Spacer(Modifier.height(16.dp))
            FormTextInput(
                value = ui.year,
                onValueChange = vm::onYearChange,
                label = "Año de publicación",
                isError = ui.errorByField["year"] != null,
                supportingText = ui.errorByField["year"],
                keyboardType = KeyboardType.Number
            )
            Spacer(Modifier.height(24.dp))
            FormTextInput(
                value = ui.description,
                onValueChange = vm::onDescriptionChange,
                label = "Descripción (Opcional)",
                isError = ui.errorByField["description"] != null,
                supportingText = ui.errorByField["description"],
                singleLine = false,
                trailingIcon = {
                    IconButton(onClick = { requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO) }) {
                        Icon(Icons.Default.Mic, contentDescription = "Dictar descripción")
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CoverImagePlaceholder(coverUri: String?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LightGray),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (coverUri.isNullOrBlank()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Añadir portada",
                        modifier = Modifier.size(48.dp),
                        tint = LightGray
                    )
                    Text("Añadir Portada", color = LightGray)
                }
            } else {
                AsyncImage(
                    model = coverUri,
                    contentDescription = "Portada del manga",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun FormTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    supportingText: String?,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        supportingText = { if (isError) Text(supportingText ?: "") },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = LightGray,
        )
    )
}