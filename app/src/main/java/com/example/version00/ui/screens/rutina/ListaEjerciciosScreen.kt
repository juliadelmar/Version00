@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

import EjerciciosViewModel
import android.util.Log // Asegúrate de importar Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.model.Ejercicio
// import com.example.version00.ui.navigation.AppScreens


private val DarkGrayAlpha30 = Color.DarkGray.copy(alpha = 0.3f)
private val TextFieldFocusedBorderColor = Color(0xFFB39DDB)
private val TextFieldUnfocusedBorderColor = Color(0xFF9575CD)
@Composable
fun ListaEjerciciosScreen(
    viewModel: EjerciciosViewModel,
    rutinaIdContext: Int,
    navController: NavHostController,
    onBack: () -> Unit
) {
    // Se ejecuta una sola vez al entrar en la pantalla: inicia la carga de ejercicios desde Firebase o caché
    LaunchedEffect(Unit) {
        viewModel.cargarEjercicios()
    }

    // Acceso directo a estados observables del ViewModel
    val ejerciciosPaginados = viewModel.ejerciciosFiltradosPaginados
    val cargando = viewModel.cargando
    val error = viewModel.error
    val busqueda = viewModel.busquedaPorTexto
    val hayMas = viewModel.hayMasEjerciciosPorMostrar

    val filtroEquipamientoActual = viewModel.filtroEquipamiento
    val filtroMusculoActual = viewModel.filtroMusculo
    val filtroDificultadActual = viewModel.filtroDificultad

    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            // Barra superior con botón para volver atrás
            TopAppBar(
                title = { Text("Seleccionar ejercicio", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.DarkGray.copy(alpha = 0.2f))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Búsqueda por texto (nombre del ejercicio)
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.actualizarBusquedaPorTexto(it) },
                label = { Text("Buscar ejercicio", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFB39DDB),
                    unfocusedBorderColor = Color(0xFF9575CD),
                    cursorColor = Color.White
                )
            )

            // Filtros por equipamiento, músculo y dificultad
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {

                FiltroDropdown(
                    label = stringResource(R.string.filtro_equipamiento),
                    opciones = FiltrosDisponibles.EQUIPAMIENTOS,
                    seleccionActual = filtroEquipamientoActual,
                    onSeleccionar = {
                        Log.d("UIScreenDebug", "onSeleccionar Equipamiento: $it")
                        viewModel.actualizarFiltroEquipamiento(it)
                    },
                    modifier = Modifier.weight(1f)
                )
                FiltroDropdown(
                    label = stringResource(R.string.filtro_musculo),
                    opciones = FiltrosDisponibles.MUSCULOS,
                    seleccionActual = filtroMusculoActual,
                    onSeleccionar = {
                        Log.d("UIScreenDebug", "onSeleccionar Músculo: $it")
                        viewModel.actualizarFiltroMusculo(it)
                    },
                    modifier = Modifier.weight(1f)
                )
                FiltroDropdown(
                    label = stringResource(R.string.filtro_dificultad),
                    opciones = FiltrosDisponibles.DIFICULTADES,
                    seleccionActual = filtroDificultadActual,
                    onSeleccionar = {
                        Log.d("UIScreenDebug", "onSeleccionar Dificultad: $it")
                        viewModel.actualizarFiltroDificultad(it)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Mostrar resultados según estado
            when {
                cargando && ejerciciosPaginados.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error al cargar ejercicios: $error", color = Color.Red)
                    }
                }
                ejerciciosPaginados.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron ejercicios", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        // Elementos de la lista de ejercicios
                        items(ejerciciosPaginados, key = { it._id }) { ejercicio ->
                            ItemEjercicio(ejercicio) {
                                navController.navigate("detalleEjercicio/${rutinaIdContext}/${ejercicio._id}")
                            }
                        }

                        // Botón para cargar más ejercicios
                        if (hayMas) {
                            item {
                                Button(
                                    onClick = { viewModel.mostrarMasEjercicios() },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Mostrar más", color = Color.White)
                                }
                            }
                        }

                        // Indicador de carga adicional al final de la lista
                        if (cargando && ejerciciosPaginados.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// FiltroDropdown y ItemEjercicio permanecen igual que en la respuesta anterior.
// ... (código de FiltroDropdown e ItemEjercicio aquí) ...
@Composable
fun FiltroDropdown(
    label: String,
    opciones: List<String>,
    seleccionActual: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = seleccionActual,
            onValueChange = {},
            label = { Text(label) },
            textStyle = MaterialTheme.typography.bodySmall,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                disabledBorderColor = Color.Gray,
                focusedContainerColor = DarkGrayAlpha30.copy(alpha = 0.1f),
                unfocusedContainerColor = DarkGrayAlpha30.copy(alpha = 0.1f),
                disabledContainerColor = DarkGrayAlpha30.copy(alpha = 0.1f),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.LightGray,
                disabledLabelColor = Color.LightGray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.DarkGray)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, style = MaterialTheme.typography.bodySmall, color = Color.White) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(textColor = Color.White)
                )
            }
        }
    }
}

@Composable
fun ItemEjercicio(
    ejercicio: Ejercicio,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGrayAlpha30)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ejercicio.urlGif,
                contentDescription = ejercicio.nombre,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.mancuerna_horixontal),
                error = painterResource(id = R.drawable.ic_mancuerna_oblicua)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = ejercicio.nombre, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Text(
                    text = "${ejercicio.nivelDificultad} - ${ejercicio.tipoDeEjercicio}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
        }
    }
}