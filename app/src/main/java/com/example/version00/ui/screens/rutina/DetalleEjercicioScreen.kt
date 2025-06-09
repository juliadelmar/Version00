@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.data.SvgMuscleHighlighter
import com.example.version00.ui.data.SvgMuscleHighlighter2
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

    // Función de normalización
    fun String.normalizado(): String {
        return lowercase()
            .replace(Regex("[áàäâ]"), "a")
            .replace(Regex("[éèëê]"), "e")
            .replace(Regex("[íìïî]"), "i")
            .replace(Regex("[óòöô]"), "o")
            .replace(Regex("[úùüû]"), "u")
            .replace("ñ", "n")
    }
    val musculosActivacionVisiblesFrontal = mapOf(
        "pectoral mayor porcion media" to setOf("pectoral_derecho", "pectoral_izquierdo"),
        "pectorales" to setOf("pectoral_derecho", "pectoral_izquierdo", "pectorales_derecho", "pectorales_izquierdo"),
        "deltoides porcion anterior" to setOf("hombro_derecho", "hombro_izquierdo"),
        "deltoides posterior" to setOf("Deltoides_Posterior_Izquierdo", "Deltoides_Posterior_Derecho"),
        "biceps cabeza larga" to setOf("biceps_derecho", "biceps_izquierdo"),
        "biceps cabeza corta" to setOf("biceps_derecho", "biceps_izquierdo"),
        "biceps" to setOf("biceps_derecho", "biceps_izquierdo"),
        "triceps braquial" to setOf("triceps_derecho", "triceps_izquierdo"),
        "triceps cabeza larga" to setOf("triceps_derecho", "triceps_izquierdo"),
        "serrato anterior" to setOf("serrato_izquierdo", "serrato_derecho"),
        "recto abdominal" to setOf("abdominal_1", "abdominal_2", "abdominal_3", "abdominal_4", "abdominal_5", "abdominal_6", "abdominal_7", "abdominal_8"),
        "oblicuo externo" to setOf("oblicuo_derecho", "oblicuo_izquierdo"),
        "oblicuo inferior" to setOf("oblicuo_inferior_derecho", "oblicuo_inferior_izquierdo"),
        "cuadriceps" to setOf("recto_femoral_derecho", "recto_femoral_izquierdo", "vasto_lateral_derecho", "vasto_lateral_izquierdo", "vasto_intermedio_derecho", "vasto_intermedio_izquierdo"),
        "sartorio" to setOf("sartorio_derecho", "sartorio_izquierdo"),
        "aductor largo" to setOf("aductor_largo_derecho", "aductor_largo_izquierdo"),
        "soleo" to setOf("soleo_derecho", "soleo_izquierdo"),
        "gastrocnemio" to setOf("gastrocnemio_medial_derecho", "gastrocnemio_medial_izquierdo", "gastrocnemio_lateral_derecho", "gastrocnemio_lateral_izquierdo", "Gastrocnemio_Izquierdo", "Gastrocnemio_Derecho"),
        "trapecio superior" to setOf("trapecio_superior", "trapecio_superior_izquierdo", "trapecio_superior_derecho", "Trapecio_Superior_Izquierdo", "Trapecio_Superior_Derecho"),
        "trapecio medio" to setOf("Trapecio_Medio_Izquierdo", "Trapecio_Medio_Derecho"),
        "trapecio inferior" to setOf("Trapecio_Inferior_Izquierdo", "Trapecio_Inferior_Derecho"),
        "esternocleidomastoideo" to setOf("esternocleidomastoideo_derecho", "esternocleidomastoideo_izquierdo"),
        "antebrazo superior" to setOf("antebrazo_superior_derecho", "antebrazo_superior_izquierdo"),
        "antebrazo inferior" to setOf("antebrazo_inferior_derecho", "antebrazo_inferior_izquierdo"),
        "infraespinoso" to setOf("Infraespinoso_Izquierdo", "Infraespinoso_Derecho"),
        "dorsal ancho" to setOf("Dorsal_Ancho_Izquierdo", "Dorsal_Ancho_Derecho"),
        "gluteo medio" to setOf("gluteo_medio_izquierdo", "gluteo_medio_derecho"),
        "gluteo mayor" to setOf("gluteo_mayor_izquierdo", "gluteo_mayor_derecho"),
        "isquiotibiales" to setOf("Isquiotibiales_Izquierdo", "Isquiotibiales_Derecho"),
        "erector espinal" to setOf("erector_espinal_izquierdo", "erector_espinal_derecho"),
        "romboides" to setOf("romboides_izquierdo", "romboides_derecho"),
        "braquiorradial" to setOf("braquiorradial_derecho", "braquiorradial_izquierdo"),
        "multifidos" to setOf("multifidos_derecho", "multifidos_izquierdo")
    )




    val seriesInicial = 4
    var series by remember { mutableStateOf(seriesInicial) }
    var descanso by remember { mutableStateOf(60) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    val repsRecamara = remember { mutableStateListOf<String>() }
    var notas by remember { mutableStateOf("") }

    // Cargar el ejercicio y preparar las listas
    LaunchedEffect(ejercicioId) {
        isLoadingEjercicio = true
        viewModel.obtenerEjercicioPorId(ejercicioId) {
            ejercicio = it
            isLoadingEjercicio = false

            reps.clear(); pesos.clear(); repsRecamara.clear()
            repeat(seriesInicial) {
                reps.add("")
                pesos.add("")
                repsRecamara.add("")
            }
        }
    }

    // Ajustar tamaño de las listas al cambiar el número de series
    LaunchedEffect(series) {
        if (reps.isNotEmpty()) {
            while (reps.size < series) reps.add("")
            while (reps.size > series) reps.removeLast()
            while (pesos.size < series) pesos.add("")
            while (pesos.size > series) pesos.removeLast()
            while (repsRecamara.size < series) repsRecamara.add("")
            while (repsRecamara.size > series) repsRecamara.removeLast()
        }
    }

    val activacionPorId = remember(ejercicio) {
        ejercicio?.porcentajeDeActivacion?.flatMap { (_, subgrupo) ->
            subgrupo.flatMap { (musculo, porcentaje) ->
                val ids = musculosActivacionVisiblesFrontal[musculo.normalizado()]
                ids?.map { id -> id to porcentaje } ?: emptyList()
            }
        }?.toMap() ?: emptyMap()
    }

    if (isLoadingEjercicio || ejercicio == null || reps.size < series) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    val currentEjercicio = ejercicio!!

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text(currentEjercicio.nombre ?: "Ejercicio", color = Color.White, style = MaterialTheme.typography.titleLarge)
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
                Text(currentEjercicio.nombre, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                AjusteValor("Series", series, onRestar = {
                    if (series > 1) series--
                }, onSumar = {
                    if (series < 30) series++
                })
                AjusteValor("Descanso", descanso, { if (descanso >= 10) descanso -= 10 }, { if (descanso <= 290) descanso += 10 }, unidad = "s")
            }

            Spacer(Modifier.height(16.dp))
            Text("Detalles de cada serie", color = Color.White)

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

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas") },
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
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
                        reps = reps.take(series).toMutableList(),
                        pesos = pesos.take(series).toMutableList(),
                        repsRecamara = repsRecamara.take(series).toMutableList(),
                        notas =  notas
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

            Text("Instrucciones", color = Color.White)
            currentEjercicio.instrucciones.forEachIndexed { i, s -> Text("${i + 1}. $s", color = Color.LightGray) }

            Spacer(Modifier.height(12.dp))
            Text("Errores comunes", color = Color.White)
            currentEjercicio.erroresComunes.forEach { e -> Text("• $e", color = Color(0xFFCF6679)) }

            Spacer(Modifier.height(16.dp))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (activacionPorId.isNotEmpty()) {
                    SvgMuscleHighlighter(
                        modifier = Modifier
                            .weight(1f)
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.DarkGray.copy(alpha = 0.3f)),
                        backgroundImageRes = R.drawable.cuerpo_frente,
                        svgImageRes = R.drawable._cuerpo_delantero,
                        activacionPorId = activacionPorId,
                        svgViewBoxWidth = 300f,
                        svgViewBoxHeight = 600f
                    )
                    SvgMuscleHighlighter2(
                        modifier = Modifier
                            .weight(1f)
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.DarkGray.copy(alpha = 0.3f)),
                        backgroundImageRes = R.drawable.cuerpo_espalda_,
                        svgImageRes = R.drawable.cuerpo_espalda,
                        activacionPorId = activacionPorId,
                        svgViewBoxWidth = 300f,
                        svgViewBoxHeight = 600f
                    )
                }
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
