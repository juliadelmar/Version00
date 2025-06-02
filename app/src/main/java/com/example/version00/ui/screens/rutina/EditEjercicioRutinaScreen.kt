@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla para editar un ejercicio ya guardado dentro de una rutina.
 * Permite modificar el número de series, repeticiones, pesos, descanso y notas.
 */
@RequiresApi(35)
@Composable
fun EditEjercicioRutinaScreen(
    rutinaId: Int,
    ejercicioId: Int, // ID del ejercicio guardado dentro de la rutina
    navController: NavHostController,
    viewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado original del ejercicio a editar
    var ejercicioGuardadoOriginal by remember { mutableStateOf<EjercicioGuardado?>(null) }

    // Estados para controlar carga y actualización
    var isLoadingEjercicio by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    // Campos editables
    var series by remember { mutableStateOf(0) }
    var descanso by remember { mutableStateOf(0) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    var notas by remember { mutableStateOf("") }

    // 🔄 Cargar los datos del ejercicio desde Firebase al entrar
    LaunchedEffect(rutinaId, ejercicioId) {
        isLoadingEjercicio = true
        viewModel.obtenerEjercicioGuardadoDeRutina(rutinaId, ejercicioId) { ejercicio ->
            if (ejercicio != null) {
                ejercicioGuardadoOriginal = ejercicio
                series = ejercicio.series
                descanso = ejercicio.descanso
                notas = ejercicio.notas
                reps.clear(); pesos.clear()
                reps.addAll(ejercicio.reps)
                pesos.addAll(ejercicio.pesos)
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

    // 🔁 Ajustar tamaño de las listas de reps/pesos si cambia el número de series
    LaunchedEffect(series) {
        if (ejercicioGuardadoOriginal != null) {
            while (reps.size < series) reps.add("")
            while (reps.size > series) reps.removeLast()
            while (pesos.size < series) pesos.add("")
            while (pesos.size > series) pesos.removeLast()
        }
    }

    // 🧱 Estructura principal de la pantalla
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.actualizar_ejercicio)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray.copy(alpha = 0.3f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoadingEjercicio) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
            return@Scaffold
        }

        ejercicioGuardadoOriginal?.let { currentEjercicio ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 🖼 Imagen + Nombre
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = currentEjercicio.urlGif,
                        contentDescription = currentEjercicio.nombre,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.DarkGray),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(currentEjercicio.nombre, color = Color.White, style = MaterialTheme.typography.titleLarge)
                }

                Spacer(Modifier.height(24.dp))

                // 🔢 Número de series
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Número de series", color = Color.White)
                    Row {
                        IconButton(onClick = { if (series > 1) series-- }) {
                            Text("-", color = Color.White)
                        }
                        Text(series.toString(), color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { if (series < 10) series++ }) {
                            Text("+", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ⏱ Tiempo de descanso
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Descanso (segundos)", color = Color.White)
                    Row {
                        IconButton(onClick = { if (descanso >= 10) descanso -= 10 }) {
                            Text("-", color = Color.White)
                        }
                        Text("${descanso}s", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { descanso += 10 }) {
                            Text("+", color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Detalles de Series", color = Color.White, style = MaterialTheme.typography.titleMedium)

                // 🔄 Lista de sets: reps + kg
                repeat(series) { index ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${index + 1}º", color = Color.White, modifier = Modifier.width(30.dp))

                        OutlinedTextField(
                            value = reps.getOrElse(index) { "" },
                            onValueChange = { reps[index] = it.filter { it.isDigit() } },
                            label = { Text("Reps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White
                            )
                        )

                        Text("x", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))

                        OutlinedTextField(
                            value = pesos.getOrElse(index) { "" },
                            onValueChange = { pesos[index] = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Kg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White
                            )
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 📝 Campo para notas
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )

                Spacer(Modifier.height(24.dp))

                // 💾 Botón para guardar cambios
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Actualizar ejercicio", color = Color.White)
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
