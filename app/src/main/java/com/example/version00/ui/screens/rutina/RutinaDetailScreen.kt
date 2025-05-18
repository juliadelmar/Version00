package com.example.version00.ui.screens.rutina

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaDetailScreen(
    rutinaId: Int,
    navController: NavHostController,
    firebaseViewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val ejerciciosDeRutina = remember { mutableStateListOf<EjercicioGuardado>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val contexto = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var initialLoadDone by remember { mutableStateOf(false) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val refreshTrigger = remember { mutableStateOf(false) }

    LaunchedEffect(rutinaId, refreshTrigger.value) {
        isLoading = true
        firebaseViewModel.obtenerEjerciciosDeRutina(rutinaId) { listaEjercicios ->
            ejerciciosDeRutina.clear()
            ejerciciosDeRutina.addAll(listaEjercicios)
            isLoading = false
            initialLoadDone = true
        }
    }

    LaunchedEffect(currentBackStackEntry) {
        val cameFromEdit = currentBackStackEntry?.savedStateHandle?.remove<Boolean>("ejercicio_actualizado_o_creado") == true
        if (cameFromEdit) {
            refreshTrigger.value = !refreshTrigger.value
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Rutina #$rutinaId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (isLoading && !initialLoadDone) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (ejerciciosDeRutina.isEmpty() && initialLoadDone) {
                    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay ejercicios en esta rutina",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(ejerciciosDeRutina, key = { it.id }) { ejercicio ->
                            EjercicioRutinaItem(ejercicio = ejercicio)
                        }
                    }
                }
            }

            // BOTONES INFERIORES
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        navController.navigate("${AppDestinations.LISTA_EJERCICIOS_ROUTE}/$rutinaId")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir ejercicio")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir ejercicio", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (ejerciciosDeRutina.isNotEmpty()) {
                            navController.navigate("${AppDestinations.TRAINING_ROUTE}/$rutinaId")
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Añade ejercicios antes de comenzar.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Comenzar rutina")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comenzar rutina", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))


            }
        }
    }
}

@Composable
fun EjercicioRutinaItem(ejercicio: EjercicioGuardado) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.3f)),
                placeholder = painterResource(id = R.drawable.mancuerna_horixontal),
                error = painterResource(id = R.drawable.ic_mancuerna_oblicua),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(ejercicio.nombre, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))

                val details = buildList {
                    if (ejercicio.series > 0) add("${ejercicio.series} series")
                    if (ejercicio.reps.isNotEmpty()) add(ejercicio.reps.joinToString("/") + " reps")
                    if (ejercicio.descanso > 0) add("${ejercicio.descanso}s descanso")
                }

                if (details.isNotEmpty()) {
                    Text(
                        text = details.joinToString("  •  "),
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
