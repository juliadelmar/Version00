package com.example.version00.ui.screens.rutina

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Asegúrate de tener este import
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import com.example.version00.ui.navigation.AppDestinations
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaDetailScreen(
    rutinaId: Int,
    navController: NavHostController,
    firebaseViewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // ... (estados y LaunchedEffects sin cambios) ...
    val ejerciciosDeRutina = remember { mutableStateListOf<EjercicioGuardado>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val refreshTrigger = remember { mutableStateOf(false) }

    LaunchedEffect(rutinaId, refreshTrigger.value) {
        isLoading = true
        Log.d("RutinaDetailScreen", "Cargando ejercicios para rutinaId: $rutinaId (refresh: ${refreshTrigger.value})")
        firebaseViewModel.obtenerEjerciciosDeRutina(rutinaId) { listaEjercicios ->
            ejerciciosDeRutina.clear()
            ejerciciosDeRutina.addAll(listaEjercicios)
            isLoading = false
            initialLoadDone = true
            Log.d("RutinaDetailScreen", "Ejercicios cargados: ${listaEjercicios.size}")
        }
    }

    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry?.destination?.route == "${AppDestinations.RUTINA_DETAIL_ROUTE}/{rutinaId}") {
            val ejercicioActualizado = currentBackStackEntry
                ?.savedStateHandle
                ?.remove<Boolean>("ejercicio_actualizado_key")
            if (ejercicioActualizado == true) {
                Log.d("RutinaDetailScreen", "Recibida señal 'ejercicio_actualizado_key'. Refrescando lista...")
                refreshTrigger.value = !refreshTrigger.value
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalle_de_rutina_id, rutinaId)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver))
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
        Column( // COLUMNA PRINCIPAL DEL CONTENIDO DEL SCAFFOLD
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sección de la lista (ocupa el espacio disponible con weight)
            Box(modifier = Modifier.weight(1f)) { // Envolvemos la lógica de la lista en un Box con weight
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (ejerciciosDeRutina.isEmpty() && initialLoadDone) {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_ejercicios_en_rutina),
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        // Quitamos el weight(1f) de aquí porque el Box padre ya lo tiene
                        modifier = Modifier.fillMaxSize() // La LazyColumn llena el Box padre
                    ) {
                        items(ejerciciosDeRutina, key = { ejercicio -> "ejercicio_${ejercicio.id}" }) { ejercicio ->
                            var offsetX by remember { mutableStateOf(0f) }
                            var deleteTriggered by remember { mutableStateOf(false) }
                            val density = LocalDensity.current
                            val deleteThresholdPx = with(density) { (-200).dp.toPx() }
                            val iconVisibilityThresholdPx = with(density) { (-100).dp.toPx() }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .pointerInput(ejercicio.id) {
                                        detectHorizontalDragGestures(
                                            onDragStart = {
                                                deleteTriggered = false
                                            },
                                            onHorizontalDrag = { change, dragAmount ->
                                                offsetX = (offsetX + dragAmount).coerceIn(deleteThresholdPx * 1.2f, 0f)
                                                if (offsetX < deleteThresholdPx && !deleteTriggered) {
                                                    deleteTriggered = true
                                                    val ejercicioIdAEliminar = ejercicio.id
                                                    val index = ejerciciosDeRutina.indexOfFirst { it.id == ejercicioIdAEliminar }
                                                    if (index != -1) {
                                                        ejerciciosDeRutina.removeAt(index)
                                                    }
                                                    firebaseViewModel.eliminarEjercicioDeRutina(rutinaId, ejercicioIdAEliminar)
                                                }
                                            },
                                            onDragEnd = {
                                                offsetX = 0f
                                                deleteTriggered = false
                                            }
                                        )
                                    }
                            ) {
                                // Fondo que se revela
                                if (offsetX < 0) {
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .background(Color.Red.copy(alpha = ((-offsetX / -deleteThresholdPx) * 0.7f).coerceIn(0f, 0.7f)))
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        if (offsetX < iconVisibilityThresholdPx) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color.White.copy(alpha = ((-offsetX / -deleteThresholdPx) * 2f).coerceIn(0f, 1f)),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                                // El item de ejercicio que se mueve y es clickeable
                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                                        .clickable {
                                            if (offsetX == 0f ) { // Simplificado, considerar `!deleteTriggered` también si es necesario
                                                Log.d("RutinaDetailScreen", "Item clickeado: ${ejercicio.nombre}. Navegando a editar.")
                                                navController.navigate(
                                                    "${AppDestinations.EDIT_EJERCICIO_RUTINA_ROUTE}/$rutinaId/${ejercicio.id}"
                                                )
                                            } else {
                                                Log.d("RutinaDetailScreen", "Click ignorado por swipe (offsetX: $offsetX)")
                                            }
                                        }
                                ) {
                                    EjercicioRutinaItem(ejercicio = ejercicio)
                                }
                            }
                        }
                    }
                }
            }

            // Sección de botones (ocupa su propio espacio, NO tiene weight)
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(16.dp)) // Espacio entre lista y botones
                Button(
                    onClick = {
                        navController.navigate("${AppDestinations.LISTA_EJERCICIOS_ROUTE}/$rutinaId")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.anadir_ejercicio_desc))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.anadir_ejercicio), color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (ejerciciosDeRutina.isNotEmpty()) {
                            navController.navigate("${AppDestinations.TRAINING_ROUTE}/$rutinaId")
                        } else {
                            scope.launch {
                                // Añadir un mensaje al Snackbar aquí
                                snackbarHostState.showSnackbar(
                                    message = "Añada ejercicios antes de comenzar la rutina.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = ejerciciosDeRutina.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.DarkGray
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.comenzar_rutina_desc))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.comenzar_rutina), color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espacio al final de la pantalla
            }
        }



    }
}

@Composable
fun EjercicioRutinaItem(ejercicio: EjercicioGuardado) {
    Card(
        modifier = Modifier.fillMaxWidth(), // El Card ocupa todo el ancho del Box que lo envuelve
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = ejercicio.urlGif,
                contentDescription = ejercicio.nombre,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = painterResource(id = R.drawable.mancuerna_horixontal),
                error = painterResource(id = R.drawable.ic_mancuerna_oblicua),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val repsText = if (ejercicio.reps.any { it.isNotBlank() }) ejercicio.reps.joinToString(", ") + " reps" else ""
                val seriesText = "${ejercicio.series} series"
                val descansoText = "${ejercicio.descanso}s descanso"
                val details = listOfNotNull(
                    seriesText.takeIf { ejercicio.series > 0 },
                    repsText.takeIf { it.isNotEmpty() },
                    descansoText.takeIf { ejercicio.descanso > 0 }
                ).joinToString("  •  ")
                if (details.isNotBlank()) {
                    Text(text = details, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}