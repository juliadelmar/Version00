package com.example.version00.ui.screens.rutina

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.data.SugeridorDeCarga
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.CalendarioMenstrualViewModel
import com.example.version00.ui.viewmodel.FaseCiclo
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaScreen(
    rutinaId: Int,
    ejercicios: List<EjercicioGuardado>,
    navController: NavHostController,
    rutinaNombre: String,
    cicloViewModel: CalendarioMenstrualViewModel,
    rutinaViewModel: RutinaFirebaseViewModel
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    val pesosAnteriores = remember { mutableStateMapOf<String, List<String>>() }
    val pesosEjecutados = remember { mutableStateMapOf<String, Float>() }
    val pesosUsadosPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    val repsHechasPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    val repsRecamaraPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    var mostrarResumen by remember { mutableStateOf(false) }

    LaunchedEffect(rutinaId) {
        rutinaViewModel.obtenerEjerciciosDeRutina(rutinaId) { historial ->
            for (ejercicio in historial) {
                val id = ejercicio.id.toString()
                val pesos = ejercicio.pesos ?: emptyList()
                pesosAnteriores[id] = pesos
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = rutinaNombre) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(ejercicios) { ejercicio ->
                    val series = ejercicio.series
                    val faseActual = cicloViewModel.faseActualDelCiclo.value ?: FaseCiclo.NINGUNA
                    val pesosPrevios = pesosAnteriores[ejercicio.id.toString()] ?: emptyList()
                    val pesoPromedio = pesosPrevios
                        .mapNotNull { it.replace(",", ".").toFloatOrNull() }
                        .average()
                        .toFloat()

                    val repsAnterior = ejercicio.reps.firstOrNull()?.toIntOrNull() ?: 10

                    val sugerencia = SugeridorDeCarga.generarSugerencia(
                        fase = faseActual,
                        pesoAnterior = pesoPromedio,
                        repsAnterior = repsAnterior
                    )

                    val pesosUsados = remember { List(series) { mutableStateOf(sugerencia.pesoSugerido.toString()) } }
                    val repsHechas = remember { List(series) { mutableStateOf(sugerencia.repsSugeridas.toString()) } }
                    val repsRecamara = remember { List(series) { mutableStateOf(sugerencia.rirSugerido.toString()) } }

                    pesosUsadosPorEjercicio[ejercicio.id.toString()] = pesosUsados
                    repsHechasPorEjercicio[ejercicio.id.toString()] = repsHechas
                    repsRecamaraPorEjercicio[ejercicio.id.toString()] = repsRecamara

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = ejercicio.urlGif,
                                    contentDescription = ejercicio.nombre,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "Plan: ${ejercicio.series} series de ${ejercicio.reps.joinToString()} reps",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            repeat(series) { index ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("S${index + 1}", style = MaterialTheme.typography.bodySmall)

                                    TextField(
                                        value = pesosUsados[index].value,
                                        onValueChange = {
                                            pesosUsados[index].value = it
                                            pesosEjecutados[ejercicio.id.toString()] =
                                                pesosUsados.mapNotNull { it.value.toFloatOrNull() }.average().toFloat()
                                        },
                                        label = { Text("Kg") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )

                                    TextField(
                                        value = repsHechas[index].value,
                                        onValueChange = { repsHechas[index].value = it },
                                        label = { Text("Reps") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )

                                    TextField(
                                        value = repsRecamara[index].value,
                                        onValueChange = { repsRecamara[index].value = it },
                                        label = { Text("RIR") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { mostrarResumen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Terminar rutina")
            }

            if (mostrarResumen) {
                val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val totalSeries = ejercicios.sumOf { it.series }
                val totalReps = ejercicios.sumOf { it.reps.sumOf { rep -> rep.toIntOrNull() ?: 0 } }
                val totalCarga = pesosEjecutados.values.sum()
                val fatigaPorMusculo = mutableMapOf<String, Double>()

                ejercicios.forEach { ejercicio ->
                    val activaciones = ejercicio.porcentajeDeActivacion
                    val rirs = repsRecamaraPorEjercicio[ejercicio.id.toString()]?.mapNotNull { it.value.toDoubleOrNull() } ?: return@forEach

                    for ((_, musculos) in activaciones) {
                        for ((musculo, porcentaje) in musculos) {
                            val fatigaTotal = rirs.sumOf { rir ->
                                val esfuerzoRelativo = 1.0 - (rir / 5.0).coerceIn(0.0, 1.0)
                                esfuerzoRelativo * (porcentaje / 100.0)
                            }
                            fatigaPorMusculo[musculo] = fatigaPorMusculo.getOrDefault(musculo, 0.0) + fatigaTotal
                        }
                    }
                }

                ModalBottomSheet(
                    onDismissRequest = { mostrarResumen = false },
                    sheetState = rememberModalBottomSheetState()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Informe de la Rutina", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Fecha: $fecha")
                                Text("Ejercicios: ${ejercicios.size}")
                                Text("Series totales: $totalSeries")
                                Text("Reps totales: $totalReps")
                                Text("Carga total: ${"%.1f".format(totalCarga)} kg")
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Fatiga muscular", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        fatigaPorMusculo.forEach { (musculo, fatiga) ->
                            Text("$musculo: ${"%.1f".format(fatiga)} puntos")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                val ejerciciosActualizados = ejercicios.map { ejercicio ->
                                    val pesosFinales = pesosUsadosPorEjercicio[ejercicio.id.toString()]?.map { it.value }?.toMutableList() ?: mutableListOf()
                                    val repsFinales = repsHechasPorEjercicio[ejercicio.id.toString()]?.map { it.value }?.toMutableList() ?: mutableListOf()
                                    val rirFinales = repsRecamaraPorEjercicio[ejercicio.id.toString()]?.map { it.value }?.toMutableList() ?: mutableListOf()

                                    val actualizado = ejercicio.copy(
                                        pesos = pesosFinales,
                                        reps = repsFinales,
                                        repsRecamara = rirFinales
                                    )

                                    rutinaViewModel.actualizarEjercicioDetalladoEnRutina(
                                        rutinaId = rutinaId,
                                        ejercicioActualizado = actualizado,
                                        onResult = { _, _ -> }
                                    )

                                    actualizado
                                }



                                rutinaViewModel.guardarHistorialFatiga(rutinaId, rutinaNombre, fatigaPorMusculo) { success, mensaje ->
                                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                                    if (success) mostrarResumen = false
                                }

                                rutinaViewModel.actualizarFatigaAcumulada(fatigaPorMusculo) { success, mensaje ->
                                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                                    if (success) mostrarResumen = false
                                }

                                rutinaViewModel.guardarRutinaEnHistorial(rutinaId, rutinaNombre, ejerciciosActualizados) { success, mensaje ->
                                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                                    if (success) mostrarResumen = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Guardar informe")
                        }
                    }
                }
            }
        }
    }
}
