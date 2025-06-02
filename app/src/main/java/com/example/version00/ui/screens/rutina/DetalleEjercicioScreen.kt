@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

import android.os.Build
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

/**
 * Pantalla de detalle de un ejercicio donde el usuario puede configurar:
 * - series, repeticiones, pesos, RIR
 * - descanso entre series
 * - notas
 * También se muestra una visualización SVG de músculos activados.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE) // API 35
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

    // Función de normalización para mapear nombres de músculos a IDs del SVG
    fun String.normalizado(): String {
        return lowercase()
            .replace(Regex("[áàäâ]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöô]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace("ñ", "n")
    }

    // Cargar datos del ejercicio al iniciar la pantalla
    LaunchedEffect(ejercicioId) {
        isLoadingEjercicio = true
        viewModel.obtenerEjercicioPorId(ejercicioId) {
            ejercicio = it
            isLoadingEjercicio = false
        }
    }

    // Mapeo de nombres de músculos a IDs del SVG para visualización
    val musculosActivacionVisiblesFrontal = mapOf(
        "deltoides".normalizado() to setOf("hombro_derecho", "hombro_izquierdo"),
        "biceps cabeza larga".normalizado() to setOf("biceps_derecho", "biceps_izquierdo"),
        "recto abdominal".normalizado() to setOf("abdominal_1", "abdominal_2", "abdominal_3", "abdominal_4")
        // Añade los demás según sea necesario
    )

    // Generar mapa final de activación por ID
    val activacionPorId = remember(ejercicio) {
        ejercicio?.porcentajeDeActivacion?.flatMap { (_, subgrupo) ->
            subgrupo.flatMap { (musculo, porcentaje) ->
                val ids = musculosActivacionVisiblesFrontal[musculo.normalizado()]
                ids?.map { id -> id to porcentaje } ?: emptyList()
            }
        }?.toMap() ?: emptyMap()
    }

    // Mostrar loading si aún no se ha cargado el ejercicio
    if (isLoadingEjercicio || ejercicio == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val currentEjercicio = ejercicio!!

    // Estado del formulario
    var series by remember { mutableStateOf(4) }
    var descanso by remember { mutableStateOf(60) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    val repsRecamara = remember { mutableStateListOf<String>() }
    var notas by remember { mutableStateOf("") }

    // Inicializar listas al cargar
    LaunchedEffect(Unit) {
        repeat(series) {
            reps.add("")
            pesos.add("")
            repsRecamara.add("")
        }
    }

    // Ajustar listas si cambia el número de series
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
        cursorColor = Color(0xFFFF9800)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Barra superior
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text(currentEjercicio.nombre ?: "Ejercicio", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(16.dp))

            // Imagen del ejercicio
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
                Text(currentEjercicio.nombre, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            // Controles de series y descanso
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                AjusteValor("Series", series, { if (series > 1) series-- }, { if (series < 30) series++ })
                AjusteValor("Descanso", descanso, { if (descanso >= 10) descanso -= 10 }, { if (descanso <= 290) descanso += 10 }, unidad = "s")
            }

            Spacer(Modifier.height(16.dp))
            Text("Detalles de cada serie", color = Color.White)

            // Campos de repeticiones, peso y RIR por serie
            repeat(series) { index ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${index + 1}", color = Color.White, modifier = Modifier.width(30.dp))
                    OutlinedTextField(reps[index], { reps[index] = it.filter { c -> c.isDigit() } }, modifier = Modifier.weight(1f), colors = textFieldColors)
                    Text("x", color = Color.White)
                    OutlinedTextField(pesos[index], { pesos[index] = it.filter { c -> c.isDigit() || c == '.' }.replace(",", ".") }, modifier = Modifier.weight(1f), colors = textFieldColors)
                    OutlinedTextField(repsRecamara[index], { repsRecamara[index] = it.filter { c -> c.isDigit() } }, modifier = Modifier.weight(1f), colors = textFieldColors)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Campo de notas
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Botón de guardar ejercicio
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

                    viewModel.guardarEjercicioDetalladoEnRutina(rutinaId, ejercicioGuardado) { ok, msg ->
                        scope.launch {
                            snackbarHostState.showSnackbar(msg)
                            if (ok) navController.popBackStack()
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                else Text("Guardar Ejercicio", color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            // Instrucciones y errores comunes
            Text("Instrucciones", color = Color.White)
            currentEjercicio.instrucciones.forEachIndexed { i, s -> Text("${i + 1}. $s", color = Color.LightGray) }

            Spacer(Modifier.height(12.dp))
            Text("Errores comunes", color = Color.White)
            currentEjercicio.erroresComunes.forEach { e -> Text("• $e", color = Color(0xFFCF6679)) }

            Spacer(Modifier.height(16.dp))

            // Visualización SVG de músculos activos
            if (activacionPorId.isNotEmpty()) {
                SvgMuscleHighlighter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray.copy(alpha = 0.3f)),
                    backgroundImageRes = R.drawable.cuerpo_frente,
                    svgImageRes = R.drawable._cuerpo_delantero,
                    activacionPorId = activacionPorId,
                    svgViewBoxWidth = 300f,
                    svgViewBoxHeight = 600f
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun AjusteValor(label: String, valor: Int, onRestar: () -> Unit, onSumar: () -> Unit, unidad: String = "") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.White)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRestar) { Text("-", color = Color.White) }
            Text("$valor$unidad", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
            IconButton(onClick = onSumar) { Text("+", color = Color.White) }
        }
    }
}
