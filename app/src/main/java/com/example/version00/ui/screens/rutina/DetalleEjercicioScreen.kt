@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

// IMPORTA TU SVG HIGHLIGHTER (asegúrate que la ruta del paquete es correcta)
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.data.SvgMuscleHighlighter
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.launch

@RequiresApi(35) // Considera si esta API level es realmente necesaria aquí
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
    var isSaving by remember { mutableStateOf(false) } // Renombrado para claridad
    var isLoadingEjercicio by remember { mutableStateOf(true) }


    LaunchedEffect(ejercicioId) {
        isLoadingEjercicio = true
        viewModel.obtenerEjercicioPorId(ejercicioId) { loadedEjercicio ->
            ejercicio = loadedEjercicio
            isLoadingEjercicio = false
        }
    }

    // Estado para los músculos a resaltar
    // Aquí asumimos que quieres resaltar los pectorales para CUALQUIER ejercicio en esta pantalla.
    // En una implementación real, esto dependería del 'ejercicio' cargado.
    // Asegúrate que los IDs "pectoral_derecho" y "pectoral_izquierdo" coincidan
    // con los android:name en tu archivo body_muscles.xml
//    val musculosAResaltar = remember(ejercicio) { // Se recalcula si 'ejercicio' cambia
//        // Lógica para determinar qué músculos resaltar basado en 'ejercicio'
//        // Por ahora, hardcodeamos los pectorales
//        if (ejercicio?.nombre?.contains("Pecho", ignoreCase = true) == true ||
//            ejercicio?.nombre?.contains("Pectoral", ignoreCase = true) == true) {
//            setOf("pectoral_derecho", "pectoral_izquierdo") // Asume estos IDs en tu SVG
//        } else {
//            emptySet() // No resaltar nada si no es de pecho
//        }
//        // Ejemplo más simple para siempre resaltar pectorales en esta pantalla:
//        // setOf("pectoral_derecho", "pectoral_izquierdo")
//    }
    val musculosAResaltarParaPrueba = remember {
        setOf("cabeza", "esternocleidomastoideo_derecho") // EJEMPLO, USA TUS IDs REALES
    }

    if (isLoadingEjercicio || ejercicio == null) { // Si está cargando O el ejercicio es null
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    // Ya tenemos 'ejercicio' cargado y no es null aquí
    val currentEjercicio = ejercicio!! // Podemos usar !! porque hemos retornado si es null

    var series by remember { mutableStateOf(3) }
    var descanso by remember { mutableStateOf(60) }
    val reps = remember { mutableStateListOf<String>() }
    val pesos = remember { mutableStateListOf<String>() }
    var notas by remember { mutableStateOf("") }

    // Inicializar reps y pesos con el tamaño correcto una vez
    LaunchedEffect(Unit) { // Se ejecuta solo una vez al inicio
        repeat(series) {
            reps.add("")
            pesos.add("")
        }
    }

    // Sincronizar el tamaño de reps y pesos con 'series'
    LaunchedEffect(series) {
        while (reps.size < series) reps.add("")
        while (reps.size > series) reps.removeLast()
        while (pesos.size < series) pesos.add("")
        while (pesos.size > series) pesos.removeLast()
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues) // Aplicar padding del Scaffold
                .padding(horizontal = 16.dp) // Padding horizontal general
                .verticalScroll(rememberScrollState()) // Hacer la columna scrollable
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver), tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                // Usar nombre del ejercicio si está disponible o un texto genérico
                Text(currentEjercicio.nombre ?: stringResource(R.string.seleccionar_ejercicio_titulo), color = Color.White, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(16.dp))

            // ===== INTEGRACIÓN DEL SVG MUSCLE HIGHLIGHTER =====
            // Asegúrate de tener R.drawable.body_outline y R.drawable.body_muscles (o R.raw.body_muscles)
            // Y que los svgViewBoxWidth/Height sean los de TU archivo SVG
            SvgMuscleHighlighter(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp) // Ajusta la altura como necesites
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.DarkGray.copy(alpha = 0.3f)), // Un fondo sutil
                backgroundImageRes = R.drawable.cuerpo_frente, // Reemplaza con tu imagen de fondo
                svgImageRes = R.drawable._cuerpo_delantero,    // Reemplaza con tu Vector Drawable
                highlightedMuscleIds = musculosAResaltarParaPrueba,
                svgViewBoxWidth = 300f, // ANCHO del viewBox de tu body_muscles.xml
                svgViewBoxHeight = 600f // ALTO del viewBox de tu body_muscles.xml
            )
            // ====================================================

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
                    // Podrías añadir más info del ejercicio aquí si la tienes
                }
            }

            Spacer(Modifier.height(24.dp))

            // Selectores de Series y Descanso (sin cambios)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.numero_de_series), color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (series > 1) series-- }) { Text("-", color = Color.White) }
                        Text(series.toString(), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { if (series < 10) series++ }) { Text("+", color = Color.White) } // Límite 10 series
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.descanso_entre_series), color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (descanso >= 10) descanso -= 10 else descanso = 0 }) { Text("-", color = Color.White) } // Mínimo 0
                        Text("${descanso}s", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { if (descanso <= 290) descanso += 10 }) { Text("+", color = Color.White) } // Máximo 300s (5min)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.detalles_de_series), color = Color.White)

            // Inputs de Reps y Pesos (sin cambios)
            if (series > 0) {
                repeat(series) { index ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "${index + 1}º", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically).padding(end=8.dp))
                        OutlinedTextField(
                            value = reps.getOrElse(index) { "" },
                            onValueChange = { newValue ->
                                // Asegurar que el índice es válido antes de escribir
                                if (index < reps.size) reps[index] = newValue.filter { it.isDigit() }.take(3)
                                else if (index == reps.size) reps.add(newValue.filter { it.isDigit() }.take(3))
                            },
                            label = { Text(stringResource(R.string.reps_label)) },
                            modifier = Modifier.weight(1f)
                            // ... (añadir colors y keyboardOptions como en EditEjercicioRutinaScreen si quieres)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("x", color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = pesos.getOrElse(index) { "" },
                            onValueChange = { newValue ->
                                if (index < pesos.size) pesos[index] = newValue.filter { it.isDigit() || it == '.' }.take(5)
                                else if (index == pesos.size) pesos.add(newValue.filter { it.isDigit() || it == '.' }.take(5))
                            },
                            label = { Text(stringResource(R.string.kg_label)) },
                            modifier = Modifier.weight(1f)
                            // ... (añadir colors y keyboardOptions)
                        )
                    }
                }
            }


            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text(stringResource(R.string.notas_opcional_label)) },
                modifier = Modifier.fillMaxWidth()
                // ... (añadir colors y heightIn como en EditEjercicioRutinaScreen)
            )

            Spacer(Modifier.height(20.dp)) // Solo un Spacer aquí
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
                        reps = reps.take(series).toMutableList(),
                        pesos = pesos.take(series).toMutableList(),
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
            Spacer(Modifier.height(16.dp)) // Espacio al final para scroll
        }
    }
}

