package com.example.version00.ui.screens.rutina

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.navigation.AppDestinations
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import kotlinx.coroutines.launch
// ... (otros imports sin cambios)
import androidx.compose.material.icons.filled.FitnessCenter // Icono alternativo para el título

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RutinaDetailScreen(
    rutinaId: Int, // <<--- CAMBIO: Int a String
    rutinaNombre: String, // El nombre ya se pasa, podemos usarlo en el título
    navController: NavHostController,
    firebaseViewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val ejerciciosDeRutina = remember { mutableStateListOf<EjercicioGuardado>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // val contexto = LocalContext.current // No se usa, se puede quitar si no es necesario para otra cosa
    var isLoading by remember { mutableStateOf(true) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val refreshTrigger = remember { mutableStateOf(false) } // Para forzar la recarga

    LaunchedEffect(rutinaId, refreshTrigger.value) {
        isLoading = true
        // Asegúrate que firebaseViewModel.obtenerEjerciciosDeRutina espera un String para rutinaId
        firebaseViewModel.obtenerEjerciciosDeRutina(rutinaId) { listaEjercicios ->
            ejerciciosDeRutina.clear()
            ejerciciosDeRutina.addAll(listaEjercicios)
            isLoading = false
            initialLoadDone = true
        }
    }

    // Refrescar si se vuelve de la pantalla de edición/creación de ejercicio
    LaunchedEffect(currentBackStackEntry) {
        val cameFromEdit = currentBackStackEntry?.savedStateHandle?.remove<Boolean>("ejercicio_actualizado_o_creado") == true
        if (cameFromEdit) {
            refreshTrigger.value = !refreshTrigger.value // Cambia el valor para activar el LaunchedEffect de arriba
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(rutinaNombre.ifBlank { "Detalle de Rutina" }) }, // Usar el nombre de la rutina
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = { // Opcional: añadir un icono o texto que identifique la rutina
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                        Icon(Icons.Filled.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(text="#${rutinaId}...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) // Mostrar parte del ID
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp), // Un poco de elevación
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar padding del Scaffold
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading && !initialLoadDone) { // Mostrar progreso solo en la carga inicial
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (ejerciciosDeRutina.isEmpty() && initialLoadDone) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp), // Más padding
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Esta rutina aún no tiene ejercicios.\n¡Añade algunos para empezar!",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize() // Asegurar que LazyColumn llene el espacio
                    ) {
                        items(ejerciciosDeRutina, key = { it.id }) { ejercicio ->
                            val dismissState = rememberDismissState(
                                confirmStateChange = {
                                    if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                                        // Acción de borrado aquí, antes de que el item desaparezca visualmente
                                        // para evitar que se recomponga brevemente en su sitio original
                                        firebaseViewModel.eliminarEjercicioDeRutina(rutinaId, ejercicio.id) // <<--- Pasar String
                                        ejerciciosDeRutina.remove(ejercicio) // Actualizar UI inmediatamente
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "${ejercicio.nombre} eliminado",
                                                withDismissAction = true
                                            )
                                        }
                                        true // Confirmar el cambio de estado
                                    } else false
                                }
                            )

                            // No es necesario el LaunchedEffect aquí si confirmStateChange ya maneja la lógica
                            // if (dismissState.isDismissed(DismissDirection.EndToStart) ||
                            //     dismissState.isDismissed(DismissDirection.StartToEnd)
                            // ) {
                            //     LaunchedEffect(ejercicio.id) { // Usar ejercicio.id para que se lance solo una vez por item
                            //         val ejercicioAEliminar = ejerciciosDeRutina.find { it.id == ejercicio.id }
                            //         if (ejercicioAEliminar != null) {
                            //             firebaseViewModel.eliminarEjercicioDeRutina(rutinaId, ejercicioAEliminar.id) // <<--- Pasar String
                            //             ejerciciosDeRutina.remove(ejercicioAEliminar) // Actualizar UI
                            //             scope.launch {
                            //                 snackbarHostState.showSnackbar(
                            //                     message = "${ejercicioAEliminar.nombre} eliminado",
                            //                     withDismissAction = true
                            //                 )
                            //             }
                            //         }
                            //     }
                            // }

                            SwipeToDismiss(
                                state = dismissState,
                                directions = setOf(
                                    DismissDirection.StartToEnd, // Habilitar ambos sentidos si se desea
                                    DismissDirection.EndToStart
                                ),
                                dismissThresholds = { FractionalThreshold(0.4f) }, // Umbral más sensible
                                background = {
                                    val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                                    val color = when (direction) {
                                        DismissDirection.StartToEnd -> Color.Red.copy(alpha = 0.7f)
                                        DismissDirection.EndToStart -> Color.Red.copy(alpha = 0.7f)
                                        else -> Color.Transparent
                                    }
                                    val alignment = when (direction) {
                                        DismissDirection.StartToEnd -> Alignment.CenterStart
                                        DismissDirection.EndToStart -> Alignment.CenterEnd
                                        else -> Alignment.Center
                                    }
                                    val icon = Icons.Default.Delete

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color, shape = RoundedCornerShape(12.dp))
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = alignment
                                    ) {
                                        Icon(icon, contentDescription = "Eliminar", tint = Color.White)
                                    }
                                },
                                dismissContent = {
                                    EjercicioRutinaItem(
                                        ejercicio = ejercicio,
                                        onClick = {
                                            // Pasar rutinaId como String
                                            navController.navigate("${AppDestinations.EDIT_EJERCICIO_RUTINA_ROUTE}/$rutinaId/${ejercicio.id}")
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Botones fijos en la parte inferior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) // Fondo para separar visualmente
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        // Pasar rutinaId como String
                        navController.navigate("${AppDestinations.LISTA_EJERCICIOS_ROUTE}/$rutinaId")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), // Bordes redondeados
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null) // Content description no es necesario para iconos decorativos si el texto lo explica
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir ejercicio")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (ejerciciosDeRutina.isNotEmpty()) {
                            // Pasar rutinaId como String
                            navController.navigate("${AppDestinations.TRAINING_ROUTE}/$rutinaId")
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Añade ejercicios a la rutina antes de comenzar.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = ejerciciosDeRutina.isNotEmpty(), // Deshabilitar si no hay ejercicios
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comenzar rutina")
                }
            }
        }
    }
}

// ... (EjercicioRutinaItem sin cambios, asumiendo que está bien)
// El composable EjercicioRutinaItem ya está bien diseñado.

@Composable
fun EjercicioRutinaItem(
    ejercicio: EjercicioGuardado,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }, // ✅ CLICK!
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                placeholder = painterResource(id = R.drawable.mancuerna_horixontal),
                error = painterResource(id = R.drawable.ic_mancuerna_oblicua),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ejercicio.nombre,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                val details = buildList {
                    if (ejercicio.series > 0) add("${ejercicio.series} series")
                    if (ejercicio.reps.isNotEmpty()) add(ejercicio.reps.joinToString("/") + " reps")
                    if (ejercicio.descanso > 0) add("${ejercicio.descanso}s descanso")
                }

                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString("  •  "),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

