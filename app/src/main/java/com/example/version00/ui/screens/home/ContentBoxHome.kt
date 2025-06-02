package com.example.version00.ui.screens.home

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.components.CalendarioHorizontal
import com.example.version00.ui.components.CardMoradoRutinas
import com.example.version00.ui.components.RecuadroMorado
import com.example.version00.ui.data.Rutina
import com.example.version00.ui.navigation.AppDestinations
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.time.LocalDate

@Composable
fun ContentBoxHome(
    navController: NavHostController,
    onNavigateToTraining: () -> Unit,
    firebaseViewModel: RutinaFirebaseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    var rutinas by remember { mutableStateOf<List<Rutina>>(emptyList()) }
    var nuevaRutinaNombre by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var diasConEjercicio by remember { mutableStateOf(emptyList<LocalDate>()) }

    // 🔄 Cargar rutinas y días del calendario
    LaunchedEffect(Unit) {
        firebaseViewModel.obtenerRutinas { rutinasFirebase ->
            rutinas = rutinasFirebase
        }
        firebaseViewModel.obtenerDiasConEjercicio {
            diasConEjercicio = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {

        CalendarioHorizontal(diasConEjercicio)
        Spacer(modifier = Modifier.height(30.dp))

        RecuadroMorado(onClick = onNavigateToTraining)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.mmis_rutinas),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // 📦 Mostrar rutinas reales desde Firebase
            items(rutinas) { rutina ->
                CardMoradoRutinas(
                    nombreRutina = rutina.nombre,
                    onClick = {
                        val encodedNombre = Uri.encode(rutina.nombre)
                        navController.navigate("${AppDestinations.RUTINA_DETAIL_ROUTE}/${rutina.id}/$encodedNombre")
                    }
                )
            }

            // ➕ Card para nueva rutina
            item {
                Card(
                    modifier = Modifier
                        .size(width = 200.dp, height = 140.dp)
                        .clickable { /* opcional: abrir teclado */ },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = nuevaRutinaNombre,
                            onValueChange = { nuevaRutinaNombre = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Button(
                            onClick = {
                                if (nuevaRutinaNombre.isNotBlank()) {
                                    firebaseViewModel.crearNuevaRutina(nuevaRutinaNombre) { nuevaId ->
                                        if (nuevaId != null) {
                                            val encodedNombre = Uri.encode(nuevaRutinaNombre)
                                            navController.navigate("${AppDestinations.RUTINA_DETAIL_ROUTE}/$nuevaId/$encodedNombre")
                                            nuevaRutinaNombre = ""
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Crear")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Crear")
                        }
                    }
                }
            }
        }
    }
}
