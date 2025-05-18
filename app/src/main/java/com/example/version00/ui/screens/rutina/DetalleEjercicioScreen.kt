@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.data.SvgMuscleHighlighter
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.launch

@RequiresApi(35)
@Composable
fun DetalleEjercicioScreen(
    rutinaId: Int,
    ejercicioId: Int,
    navController: NavHostController,
    viewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var ejercicio by remember { mutableStateOf<Ejercicio?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoadingEjercicio by remember { mutableStateOf(true) }

    fun String.normalizado(): String {
        return this.lowercase()
            .replace(Regex("[áàäâ]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöô]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace("ñ", "n")
    }

    LaunchedEffect(ejercicioId) {
        isLoadingEjercicio = true
        viewModel.obtenerEjercicioPorId(ejercicioId) { loadedEjercicio ->
            ejercicio = loadedEjercicio
            isLoadingEjercicio = false
        }
    }

    val musculosActivacionVisiblesFrontal = mapOf(
        "esternocleidomastoideo".normalizado() to setOf("esternocleidomastoideo_derecho", "esternocleidomastoideo_izquierdo"),
        "trapecio superior".normalizado() to setOf("trapecio_superior", "trapecio_superior_izquierdo", "Trapecio_superior_derecho"),
        "deltoides".normalizado() to setOf("hombro_derecho", "hombro_izquierdo"),
        "pectoral mayor porcion superior".normalizado() to setOf("pectoral_derecho", "pectoral_izquierdo"),
        "pectoral mayor porcion media".normalizado() to setOf("pectoral_derecho", "pectoral_izquierdo"),
        "biceps cabeza larga".normalizado() to setOf("biceps_derecho", "biceps_izquierdo"),
        "biceps cabeza corta".normalizado() to setOf("biceps_derecho", "biceps_izquierdo"),
        "antebrazo anterior".normalizado() to setOf("antebrazo_inferior_derecho", "antebrazo_inferior_izquierdo"),
        "antebrazo superior".normalizado() to setOf("antebrazo_superior_derecho", "antebrazo_superior_izquierdo"),
        "oblicuo externo".normalizado() to setOf("oblicuo_derecho", "oblicuo_izquierdo", "oblicuo_1", "oblicuo_2"),
        "oblicuo interno".normalizado() to setOf("oblicuo_inferior_derecho", "oblicuo_inferior_izquierdo"),
        "recto abdominal".normalizado() to setOf("abdominal_1", "abdominal_2", "abdominal_3", "abdominal_4", "abdominal_5", "abdominal_6", "abdominal_7", "abdominal_8"),
        "sartorio".normalizado() to setOf("sartorio_derecho", "sartorio_izquierdo"),
        "recto femoral".normalizado() to setOf("recto_femoral_derecho", "recto_femoral_izquierdo"),
        "vasto lateral".normalizado() to setOf("vasto_lateral_derecho", "vasto_lateral_izquierdo"),
        "vasto intermedio".normalizado() to setOf("vasto_intermedio_derecho_borde", "vasto_intermedio_derech_borde", "vasto_intermedio_izquierdo"),
        "aductor largo".normalizado() to setOf("aductor_largo_derecho", "aductor_largo_izquierdo"),
        "gastrocnemio medial".normalizado() to setOf("gastrocnemio_medial_derecho", "gastrocnemio_medial_izquierdo"),
        "gastrocnemio lateral".normalizado() to setOf("gastrocnemio_lateral_derecho", "gastrocnemio_lateral_izquierdo"),
        "soleo".normalizado() to setOf("soleo_derecho", "soleo_izquierdo")
    )

    val activacionPorId = remember(ejercicio) {
        ejercicio?.porcentajeDeActivacion?.flatMap { (_, subgrupo) ->
            subgrupo.flatMap { (musculo, porcentaje) ->
                val ids = musculosActivacionVisiblesFrontal[musculo.normalizado()]
                ids?.map { id -> id to porcentaje } ?: emptyList()
            }
        }?.toMap() ?: emptyMap()
    }

    if (isLoadingEjercicio || ejercicio == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val currentEjercicio = ejercicio!!

    var series by remember { mutableStateOf(4) } // Default series
    var descanso by remember { mutableStateOf(60) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    val repsRecamara = remember { mutableStateListOf<String>() } // For RIR
    var notas by remember { mutableStateOf("") }

    // This effect ensures lists are populated initially based on 'series'
    LaunchedEffect(Unit) {
        // If loading an existing exercise, you might populate from its data here.
        // For a new entry, initialize with empty strings for the default number of series.
        repeat(series) {
            if (reps.size <= it) reps.add("")
            if (pesos.size <= it) pesos.add("")
            if (repsRecamara.size <= it) repsRecamara.add("")
        }
    }

    // This effect dynamically adjusts the lists when 'series' changes
    LaunchedEffect(series) {
        while (reps.size < series) reps.add("")
        while (reps.size > series) reps.removeLast()

        while (pesos.size < series) pesos.add("")
        while (pesos.size > series) pesos.removeLast()

        while (repsRecamara.size < series) repsRecamara.add("")
        while (repsRecamara.size > series) repsRecamara.removeLast()
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        disabledTextColor = Color.Gray,
        focusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
        unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
        disabledContainerColor = Color.DarkGray.copy(alpha = 0.1f),
        cursorColor = Color(0xFFFF9800),
        focusedIndicatorColor = Color(0xFFFF9800),
        unfocusedIndicatorColor = Color.Gray,
        focusedLabelColor = Color(0xFFFF9800),
        unfocusedLabelColor = Color.LightGray,
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver), tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text(currentEjercicio.nombre ?: stringResource(R.string.seleccionar_ejercicio_titulo), color = Color.White, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(16.dp))

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
                Column {
                    Text(currentEjercicio.nombre, color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.numero_de_series), color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (series > 1) series-- }) { Text("-", color = Color.White, style = MaterialTheme.typography.titleLarge) }
                        Text(series.toString(), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.titleMedium)
                        // Removed upper limit, or you can set a high one like 20-30
                        IconButton(onClick = { if (series < 30) series++ }) { Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge) }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.descanso_entre_series), color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (descanso >= 10) descanso -= 10 else descanso = 0 }) { Text("-", color = Color.White, style = MaterialTheme.typography.titleLarge) }
                        Text("${descanso}s", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = { if (descanso <= 290) descanso += 10 }) { Text("+", color = Color.White, style = MaterialTheme.typography.titleLarge) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.detalles_de_series), color = Color.White, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))


            if (series > 0) {
                Column {
                    // Header Row (Optional, but good for clarity)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Set", color = Color.Gray, modifier = Modifier.width(30.dp)) // For "1º, 2º, ..."
                        Text(stringResource(R.string.reps_label), color = Color.Gray, modifier = Modifier.weight(1f).padding(start = 4.dp))
                        Spacer(Modifier.width(4.dp)) // Spacer before "x"
                        Text(stringResource(R.string.kg_label), color = Color.Gray, modifier = Modifier.weight(1f).padding(start = 4.dp))
                        Spacer(Modifier.width(4.dp)) // Spacer before RIR
                        Text(stringResource(R.string.rir_label), color = Color.Gray, modifier = Modifier.weight(1f).padding(start = 4.dp)) // RIR Label
                    }

                    repeat(series) { index ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp), // Consistent spacing
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                modifier = Modifier.width(30.dp).align(Alignment.CenterVertically)
                            )
                            OutlinedTextField(
                                value = reps.getOrElse(index) { "" },
                                onValueChange = { newValue ->
                                    if (index < reps.size) reps[index] = newValue.filter { it.isDigit() }.take(3)
                                },
                                //label = { Text(stringResource(R.string.reps_label)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = textFieldColors
                            )

                            Text("x", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))

                            OutlinedTextField(
                                value = pesos.getOrElse(index) { "" },
                                onValueChange = { newValue ->
                                    if (index < pesos.size) pesos[index] = newValue.filter { it.isDigit() || it == '.' || it == ',' }.take(5)
                                        .replace(',', '.') // Allow comma, convert to dot
                                },
                                //label = { Text(stringResource(R.string.kg_label)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = textFieldColors
                            )

                            OutlinedTextField( // Reps en Recámara (RIR)
                                value = repsRecamara.getOrElse(index) { "" },
                                onValueChange = { newValue ->
                                    if (index < repsRecamara.size) repsRecamara[index] = newValue.filter { it.isDigit() }.take(2)
                                },
                                //label = { Text(stringResource(R.string.rir_label)) }, // Use R.string.rir_label
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = textFieldColors
                            )
                        }
                    }
                }
            }


            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text(stringResource(R.string.notas_label)) },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                minLines = 2
            )

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (isSaving) return@Button

                    isSaving = true
                    val ejercicioGuardado = EjercicioGuardado(
                        id = currentEjercicio._id,
                        nombre = currentEjercicio.nombre,
                        urlGif = currentEjercicio.urlGif,
                        series = series,
                        descanso = descanso,
                        porcentajeDeActivacion = currentEjercicio.porcentajeDeActivacion,
                        // Ensure we only take the current number of series elements
                        reps = reps.take(series).toMutableList(),
                        pesos = pesos.take(series).toMutableList(),
                        repsRecamara = repsRecamara.take(series).toMutableList(),
                        notas = notas
                    )

                    viewModel.guardarEjercicioDetalladoEnRutina(
                        rutinaId = rutinaId,
                        ejercicio = ejercicioGuardado
                    ) { guardadoCorrecto, mensaje ->
                        scope.launch {
                            snackbarHostState.showSnackbar(mensaje)
                            if (guardadoCorrecto) {
                                navController.popBackStack()
                            }
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.guardar_ejercicio_boton), color = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))

            Text(stringResource(R.string.instrucciones), color = Color.White, style = MaterialTheme.typography.titleMedium)
            currentEjercicio.instrucciones.forEachIndexed { index, instruccion ->
                Text("${index + 1}. $instruccion", color = Color.LightGray, modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp))
            }

            Spacer(Modifier.height(12.dp))

            Text(stringResource(R.string.errores_comunes), color = Color.White, style = MaterialTheme.typography.titleMedium)
            currentEjercicio.erroresComunes.forEachIndexed { index, error ->
                Text("• $error", color = Color(0xFFCF6679), modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)) // Error color
            }

            Spacer(Modifier.height(16.dp))

            if (activacionPorId.isNotEmpty()) { // Only show if there's activation data
                SvgMuscleHighlighter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray.copy(alpha = 0.3f)),
                    backgroundImageRes = R.drawable.cuerpo_frente,
                    svgImageRes = R.drawable._cuerpo_delantero, // Ensure this SVG path is correct
                    activacionPorId = activacionPorId,
                    svgViewBoxWidth = 300f,
                    svgViewBoxHeight = 600f
                )
            }
            Spacer(Modifier.height(16.dp)) // Padding at the bottom
        }
    }
}