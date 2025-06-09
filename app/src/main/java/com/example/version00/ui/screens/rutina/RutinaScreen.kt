// Archivo: RutinaScreen.kt
package com.example.version00.ui.screens.rutina

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.data.SugeridorDeCarga
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.navigation.AppDestinations
import com.example.version00.ui.viewmodel.CalendarioMenstrualViewModel
import com.example.version00.ui.viewmodel.FaseCiclo
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


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
    val pesosUsadosPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    val repsHechasPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    val repsRecamaraPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    val seriesCheckeadasPorEjercicio = remember { mutableStateMapOf<String, List<MutableState<Boolean>>>() }

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

    ejercicios.forEach { ejercicio ->
        val series = ejercicio.series
        val id = ejercicio.id.toString()
        if (pesosUsadosPorEjercicio[id].isNullOrEmpty()) {
            val faseActual = cicloViewModel.faseActualDelCiclo.value ?: FaseCiclo.NINGUNA
            val pesosPrevios = pesosAnteriores[id] ?: emptyList()
            val pesoPromedio = pesosPrevios.mapNotNull { it.replace(",", ".").toFloatOrNull() }.average().toFloat()
            val repsAnterior = ejercicio.reps.firstOrNull()?.toIntOrNull() ?: 10

            val sugerencia = SugeridorDeCarga.generarSugerencia(
                fase = faseActual,
                pesoAnterior = pesoPromedio,
                repsAnterior = repsAnterior
            )

            pesosUsadosPorEjercicio[id] = List(series) { mutableStateOf(formatearPesoSeguro(sugerencia.pesoSugerido)) }
            repsHechasPorEjercicio[id] = List(series) { mutableStateOf(sugerencia.repsSugeridas.toString()) }
            repsRecamaraPorEjercicio[id] = List(series) { mutableStateOf(sugerencia.rirSugerido.toString()) }
            seriesCheckeadasPorEjercicio[id] = List(series) { mutableStateOf(false) }
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
                    val id = ejercicio.id.toString()
                    val series = ejercicio.series
                    val pesos = pesosUsadosPorEjercicio[id] ?: return@items
                    val reps = repsHechasPorEjercicio[id] ?: return@items
                    val rirs = repsRecamaraPorEjercicio[id] ?: return@items
                    val checks = seriesCheckeadasPorEjercicio[id] ?: return@items

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = ejercicio.urlGif,
                                contentDescription = ejercicio.nombre,
                                modifier = Modifier
                                    .size(64.dp)
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

                        // 🏷️ Etiquetas de columna
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Spacer(modifier = Modifier.width(40.dp)) // espacio para checkbox + S1
                            Text("Kg", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                            Text("Reps", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                            Text("RIR", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                        }

                        repeat(series) { index ->
                            val isChecked = checks[index].value
                            val textDecoration = if (isChecked) TextDecoration.LineThrough else null
                            val disabled = isChecked

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checks[index].value = it }
                                )

                                Text("S${index + 1}", modifier = Modifier.width(32.dp))

                                EditableField(
                                    value = pesos[index].value,
                                    onChange = { pesos[index].value = it },
                                    label = "Kg",
                                    enabled = !disabled,
                                    decoration = textDecoration,
                                    modifier = Modifier.weight(1f)
                                )

                                EditableField(
                                    value = reps[index].value,
                                    onChange = { reps[index].value = it },
                                    label = "Reps",
                                    enabled = !disabled,
                                    decoration = textDecoration,
                                    modifier = Modifier.weight(1f)
                                )

                                EditableField(
                                    value = rirs[index].value,
                                    onChange = { rirs[index].value = it },
                                    label = "RIR",
                                    enabled = !disabled,
                                    decoration = textDecoration,
                                    modifier = Modifier.weight(1f)
                                )
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
                RutinaResumenModal(
                    rutinaId = rutinaId,
                    rutinaNombre = rutinaNombre,
                    ejercicios = ejercicios,
                    pesosUsadosPorEjercicio = pesosUsadosPorEjercicio,
                    repsHechasPorEjercicio = repsHechasPorEjercicio,
                    repsRecamaraPorEjercicio = repsRecamaraPorEjercicio,
                    seriesCheckeadasPorEjercicio = seriesCheckeadasPorEjercicio,
                    rutinaViewModel = rutinaViewModel,
                    navController = navController,
                    onDismiss = { mostrarResumen = false }
                )
            }
        }
    }
}

@Composable
fun EditableField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    decoration: TextDecoration?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .background(
                if (enabled) colors.surfaceVariant else colors.surface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                val clean = it.filter { c -> c.isDigit() || c == '.' || c == ',' }
                onChange(clean.replace(",", "."))
            },
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(
                color = colors.onSurface,
                textDecoration = decoration
            ),
            cursorBrush = SolidColor(colors.primary)
        )

        if (value.isEmpty()) {
            Text(
                text = label,
                style = LocalTextStyle.current.copy(
                    color = colors.onSurface.copy(alpha = 0.4f),
                    textDecoration = decoration
                )
            )
        }
    }
}

fun formatearPesoSeguro(valor: Float): String {
    return if (valor.isNaN() || valor.isInfinite()) "0" else "%.1f".format(valor)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaResumenModal(
    rutinaId: Int,
    rutinaNombre: String,
    ejercicios: List<EjercicioGuardado>,
    pesosUsadosPorEjercicio: Map<String, List<MutableState<String>>>,
    repsHechasPorEjercicio: Map<String, List<MutableState<String>>>,
    repsRecamaraPorEjercicio: Map<String, List<MutableState<String>>>,
    seriesCheckeadasPorEjercicio: Map<String, List<MutableState<Boolean>>>,
    rutinaViewModel: RutinaFirebaseViewModel,
    navController: NavHostController,
    onDismiss: () -> Unit
) {
    val contexto = LocalContext.current
    val fecha = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()) }

    // Valores calculados
    var totalCarga by remember { mutableStateOf(0.0) }
    var caloriasEstimadas by remember { mutableStateOf(0.0) }
    val fatigaPorMusculo = remember { mutableStateMapOf<String, Double>() }

    // Cálculo
    LaunchedEffect(true) {
        var carga = 0.0
        var calorias = 0.0
        val fatigaTemp = mutableMapOf<String, Double>()

        ejercicios.forEach { ejercicio ->
            val id = ejercicio.id.toString()
            val activaciones = ejercicio.porcentajeDeActivacion
            val pesos = pesosUsadosPorEjercicio[id]?.map { it.value.replace(",", ".").toFloatOrNull() ?: 0f } ?: return@forEach
            val reps = repsHechasPorEjercicio[id]?.map { it.value.toIntOrNull() ?: 0 } ?: return@forEach
            val rirs = repsRecamaraPorEjercicio[id]?.map { it.value.toDoubleOrNull() ?: 0.0 } ?: return@forEach
            val checks = seriesCheckeadasPorEjercicio[id] ?: return@forEach

            for (i in 0 until ejercicio.series) {
                if (checks.getOrNull(i)?.value != true) continue

                val peso = pesos.getOrNull(i) ?: 0f
                val rep = reps.getOrNull(i) ?: 0
                val rir = rirs.getOrNull(i) ?: 0.0
                if (rep <= 0) continue

                val esfuerzo = 1.0 - (rir / 5.0).coerceIn(0.0, 1.0)
                val trabajo = if (peso > 0f) peso * rep else rep * esfuerzo.toFloat() * 2f

                if (peso > 0f) carga += peso * rep
                calorias += trabajo * esfuerzo * 0.01

                for ((_, musculos) in activaciones) {
                    for ((musculo, porcentaje) in musculos) {
                        val fatiga = esfuerzo * (porcentaje / 100.0)
                        fatigaTemp[musculo] = fatigaTemp.getOrDefault(musculo, 0.0) + fatiga
                    }
                }
            }
        }

        totalCarga = carga
        caloriasEstimadas = calorias
        fatigaPorMusculo.clear()
        fatigaPorMusculo.putAll(fatigaTemp)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Informe de la Rutina", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📅 Fecha: $fecha")
                    Text("🏋️ Ejercicios: ${ejercicios.size}")
                    Text("⚖️ Carga total: ${"%.1f".format(totalCarga)} kg")
                    Text("🔥 Calorías estimadas: ${"%.1f".format(caloriasEstimadas)} kcal")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Fatiga muscular", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (fatigaPorMusculo.isEmpty()) {
                Text("❗ No se completó ninguna serie.")
            } else {
                fatigaPorMusculo.forEach { (musculo, fatiga) ->
                    Text("• $musculo: ${"%.1f".format(fatiga)} puntos")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val ejerciciosActualizados = ejercicios.map { ejercicio ->
                        val id = ejercicio.id.toString()
                        val checks = seriesCheckeadasPorEjercicio[id] ?: listOf()

                        val pesosFinales = pesosUsadosPorEjercicio[id]
                            ?.mapIndexedNotNull { i, v -> if (checks.getOrNull(i)?.value == true) v.value else null }
                            ?.toMutableList() ?: mutableListOf()

                        val repsFinales = repsHechasPorEjercicio[id]
                            ?.mapIndexedNotNull { i, v -> if (checks.getOrNull(i)?.value == true) v.value else null }
                            ?.toMutableList() ?: mutableListOf()

                        val rirsFinales = repsRecamaraPorEjercicio[id]
                            ?.mapIndexedNotNull { i, v -> if (checks.getOrNull(i)?.value == true) v.value else null }
                            ?.toMutableList() ?: mutableListOf()

                        val actualizado = ejercicio.copy(
                            pesos = pesosFinales,
                            reps = repsFinales,
                            repsRecamara = rirsFinales
                        )

                        rutinaViewModel.actualizarEjercicioDetalladoEnRutina(
                            rutinaId,
                            ejercicioActualizado = actualizado,
                            onResult = { _, _ -> }
                        )

                        actualizado
                    }

                    rutinaViewModel.guardarHistorialFatiga(
                        rutinaId = rutinaId,
                        rutinaNombre = rutinaNombre,
                        fatigaPorMusculo = fatigaPorMusculo.toMap(), // ✅ Este es el nombre correcto
                        onResult = { success, mensaje ->
                            Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    )

                    rutinaViewModel.actualizarFatigaAcumulada(
                        fatigaNueva = fatigaPorMusculo.toMap(),
                        onComplete = { success, mensaje ->
                            Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    )


                    rutinaViewModel.guardarRutinaEnHistorial(
                        rutinaId,
                        rutinaNombre,
                        ejerciciosActualizados,
                        caloriasEstimadas,
                        totalCarga
                    ) { success, mensaje ->
                        Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                        if (success) {
                            onDismiss()
                            navController.navigate(AppDestinations.HOME_ROUTE) {
                                popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar informe")
            }
        }
    }
}
