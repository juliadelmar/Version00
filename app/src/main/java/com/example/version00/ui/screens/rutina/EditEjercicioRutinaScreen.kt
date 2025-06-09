@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(35)
@Composable
fun EditEjercicioRutinaScreen(
    rutinaId: Int,
    ejercicioId: Int,
    navController: NavHostController,
    viewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var ejercicioGuardadoOriginal by remember { mutableStateOf<EjercicioGuardado?>(null) }
    var isLoadingEjercicio by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    var series by remember { mutableStateOf(0) }
    var descanso by remember { mutableStateOf(0) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    var notas by remember { mutableStateOf("") }

    LaunchedEffect(rutinaId, ejercicioId) {
        isLoadingEjercicio = true
        viewModel.obtenerEjercicioGuardadoDeRutina(rutinaId, ejercicioId) { ejercicio ->
            if (ejercicio != null) {
                ejercicioGuardadoOriginal = ejercicio
                series = ejercicio.series
                descanso = ejercicio.descanso
                notas = ejercicio.notas

                reps.clear()
                pesos.clear()

                reps.addAll(ejercicio.reps.take(series))
                pesos.addAll(ejercicio.pesos.take(series))

                while (reps.size < series) reps.add("")
                while (pesos.size < series) pesos.add("")
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Error: No se pudo cargar el ejercicio.")
                    delay(1000)
                    navController.popBackStack()
                }
            }
            isLoadingEjercicio = false
        }
    }

    LaunchedEffect(series) {
        if (ejercicioGuardadoOriginal != null) {
            while (reps.size < series) reps.add("")
            while (reps.size > series && reps.isNotEmpty()) reps.removeAt(reps.lastIndex)
            while (pesos.size < series) pesos.add("")
            while (pesos.size > series && pesos.isNotEmpty()) pesos.removeAt(pesos.lastIndex)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Actualizar ejercicio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (isLoadingEjercicio) {
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        ejercicioGuardadoOriginal?.let { currentEjercicio ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = currentEjercicio.urlGif,
                        contentDescription = currentEjercicio.nombre,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(currentEjercicio.nombre, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleLarge)
                }

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Número de series", color = MaterialTheme.colorScheme.onBackground)
                    Row {
                        IconButton(onClick = { if (series > 1) series-- }) {
                            Text("-", color = MaterialTheme.colorScheme.onBackground)
                        }
                        Text(series.toString(), color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { if (series < 10) series++ }) {
                            Text("+", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Descanso (segundos)", color = MaterialTheme.colorScheme.onBackground)
                    Row {
                        IconButton(onClick = { if (descanso >= 10) descanso -= 10 }) {
                            Text("-", color = MaterialTheme.colorScheme.onBackground)
                        }
                        Text("${descanso}s", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { descanso += 10 }) {
                            Text("+", color = MaterialTheme.colorScheme.onBackground)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Detalles de Series", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)

                repeat(series) { index ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}º", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.width(30.dp))

                        OutlinedTextField(
                            value = reps.getOrElse(index) { "" },
                            onValueChange = { reps[index] = it.filter { it.isDigit() } },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Text("x", color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(horizontal = 8.dp))

                        OutlinedTextField(
                            value = pesos.getOrElse(index) { "" },
                            onValueChange = { pesos[index] = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Kg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isUpdating) return@Button
                        isUpdating = true

                        val ejercicioActualizado = currentEjercicio.copy(
                            series = series,
                            descanso = descanso,
                            reps = reps.take(series).toMutableList(),
                            pesos = pesos.take(series).toMutableList(),
                            notas = notas
                        )

                        viewModel.actualizarEjercicioDetalladoEnRutina(
                            rutinaId = rutinaId,
                            ejercicioActualizado = ejercicioActualizado
                        ) { success, mensaje ->
                            scope.launch {
                                snackbarHostState.showSnackbar(mensaje)
                                if (success) {
                                    navController.previousBackStackEntry?.savedStateHandle?.set("ejercicio_actualizado_key", true)
                                    navController.popBackStack()
                                }
                                isUpdating = false
                            }
                        }
                    },
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Actualizar ejercicio")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
