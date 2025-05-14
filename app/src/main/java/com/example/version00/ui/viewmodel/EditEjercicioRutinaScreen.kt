package com.example.version00.ui.screens.rutina

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

@RequiresApi(35)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEjercicioRutinaScreen(
    rutinaId: Int,
    ejercicioId: Int, // ID del EjercicioGuardado (que es el _id del Ejercicio original)
    navController: NavHostController,
    viewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var ejercicioGuardadoOriginal by remember { mutableStateOf<EjercicioGuardado?>(null) }
    var isLoadingEjercicio by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    // Estados para los campos editables
    var series by remember { mutableStateOf(0) } // Inicializar a 0 o un valor por defecto adecuado
    var descanso by remember { mutableStateOf(0) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    var notas by remember { mutableStateOf("") }

    LaunchedEffect(rutinaId, ejercicioId) {
        isLoadingEjercicio = true
        Log.d("EditScreen", "Lanzado LaunchedEffect para cargar ejercicio: rutinaId=$rutinaId, ejercicioId=$ejercicioId")
        viewModel.obtenerEjercicioGuardadoDeRutina(rutinaId, ejercicioId) { ejercicio ->
            Log.d("EditScreen", "Ejercicio obtenido de ViewModel: $ejercicio")
            if (ejercicio != null) {
                ejercicioGuardadoOriginal = ejercicio
                series = ejercicio.series
                descanso = ejercicio.descanso
                notas = ejercicio.notas

                reps.clear()
                reps.addAll(ejercicio.reps)
                while (reps.size < ejercicio.series) reps.add("") // Asegurar tamaño mínimo

                pesos.clear()
                pesos.addAll(ejercicio.pesos)
                while (pesos.size < ejercicio.series) pesos.add("") // Asegurar tamaño mínimo

                Log.d("EditScreen", "Datos cargados: series=$series, reps=$reps")
            } else {
                scope.launch {
                    snackbarHostState.showSnackbar("Error: No se pudo cargar el ejercicio para editar.")
                    delay(1000) // Pequeña pausa para que el usuario vea el snackbar
                    navController.popBackStack()
                }
            }
            isLoadingEjercicio = false
        }
    }

    // Sincronizar el tamaño de reps y pesos con el estado 'series'
    LaunchedEffect(series) {
        // Solo ajustar si el ejercicio ya ha sido cargado para evitar reseteos iniciales
        if (ejercicioGuardadoOriginal != null) {
            Log.d("EditScreen", "Series cambió a $series. Ajustando reps/pesos. Actual reps: ${reps.toList()}, Actual pesos: ${pesos.toList()}")
            // Ajustar reps
            val currentRepsSize = reps.size
            if (currentRepsSize < series) {
                repeat(series - currentRepsSize) { reps.add("") }
            } else if (currentRepsSize > series) {
                repeat(currentRepsSize - series) { reps.removeLast() }
            }

            // Ajustar pesos
            val currentPesosSize = pesos.size
            if (currentPesosSize < series) {
                repeat(series - currentPesosSize) { pesos.add("") }
            } else if (currentPesosSize > series) {
                repeat(currentPesosSize - series) { pesos.removeLast() }
            }
            Log.d("EditScreen", "Después de ajustar: reps=${reps.toList()}, pesos=${pesos.toList()}")
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.actualizar_ejercicio)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver), tint = Color.White)
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

                // Selector de Series
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Número de series", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (series > 1) series-- else series = 1 }) { // Mínimo 1 serie
                            Text("-", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        }
                        Text(series.toString(), color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { if (series < 10) series++ }) {
                            Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Selector de Descanso
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Descanso (segundos)", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (descanso >= 10) descanso -= 10 else descanso = 0 }) { // Mínimo 0
                            Text("-", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        }
                        Text("${descanso}s", color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 12.dp))
                        IconButton(onClick = { if (descanso <= 290) descanso += 10 }) {
                            Text("+", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Detalles de Series", color = Color.White, style = MaterialTheme.typography.titleMedium)

                // Solo mostrar campos de reps/pesos si hay series
                if (series > 0) {
                    repeat(series) { index ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${index + 1}º", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp))
                            OutlinedTextField(
                                value = reps.getOrElse(index) { "" },
                                onValueChange = { reps[index] = it.filter { char -> char.isDigit() }.take(3) },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = Color.White, unfocusedLabelColor = Color.Gray,
                                    cursorColor = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("x", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                            Spacer(Modifier.width(8.dp))
                            OutlinedTextField(
                                value = pesos.getOrElse(index) { "" },
                                onValueChange = { pesos[index] = it.filter { char -> char.isDigit() || char == '.' }.take(5) },
                                label = { Text("Kg") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = if (index == series -1) ImeAction.Next else ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray,
                                    focusedLabelColor = Color.White, unfocusedLabelColor = Color.Gray,
                                    cursorColor = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                ),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }


                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White, unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.White, unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.White, focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    ),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    maxLines = 4
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
                        Log.d("EditScreen", "Actualizando ejercicio: $ejercicioActualizado")

                        viewModel.actualizarEjercicioDetalladoEnRutina(
                            rutinaId = rutinaId,
                            ejercicioActualizado = ejercicioActualizado
                        ) { actualizadoCorrecto, mensaje ->
                            scope.launch {
                                snackbarHostState.showSnackbar(mensaje)
                                if (actualizadoCorrecto) {
                                    Log.d("EditScreen", "Ejercicio actualizado con éxito, enviando señal de refresco.")
                                    navController.previousBackStackEntry?.savedStateHandle?.set("ejercicio_actualizado_key", true)
                                    navController.popBackStack()
                                } else {
                                    Log.e("EditScreen", "Error al actualizar ejercicio: $mensaje")
                                }
                                isUpdating = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.actualizar_ejercicio), color = Color.White) // Usar string resource
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        } ?: run {
            // Si ejercicioGuardadoOriginal es null después de isLoadingEjercicio=false
            // (Ya manejado por el popBackStack en el LaunchedEffect si falla la carga)
        }
    }
}