@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.version00.ui.screens.rutina

import android.util.Log
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
import com.example.version00.ui.viewmodel.EjerciciosViewModel
import com.example.version00.ui.viewmodel.FiltrosDisponibles

@Composable
fun ListaEjerciciosScreen(
    viewModel: EjerciciosViewModel,
    rutinaIdContext: Int,
    navController: NavHostController,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.cargarEjercicios()
    }

    val ejerciciosFiltrados by rememberUpdatedState(viewModel.ejerciciosFiltrados)
    val cargando by viewModel.cargando
    val error by viewModel.error
    val busqueda by viewModel.busquedaPorTexto
    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.volver), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.seleccionar_ejercicio),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { viewModel.actualizarBusquedaPorTexto(it) },
                    label = { Text(stringResource(R.string.buscar_ejercicio), color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color(0xFFB39DDB),
                        unfocusedBorderColor = Color(0xFF9575CD),
                        focusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.LightGray
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    FiltroDropdown(
                        label = "Equipamiento",
                        opciones = FiltrosDisponibles.EQUIPAMIENTOS,
                        seleccionActual = viewModel.filtroEquipamiento.value,
                        onSeleccionar = { viewModel.filtroEquipamiento.value = it },
                        modifier = Modifier.weight(1f)
                    )
                    FiltroDropdown(
                        label = "Músculo",
                        opciones = FiltrosDisponibles.PARTE_DEL_CUERPO,
                        seleccionActual = viewModel.filtroMusculo.value,
                        onSeleccionar = { viewModel.filtroMusculo.value = it },
                        modifier = Modifier.weight(1f)
                    )
                    FiltroDropdown(
                        label = "Dificultad",
                        opciones = FiltrosDisponibles.DIFICULTADES,
                        seleccionActual = viewModel.filtroDificultad.value,
                        onSeleccionar = { viewModel.filtroDificultad.value = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                when {
                    cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                    error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = Color.Red)
                    }
                    ejerciciosFiltrados.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No se encontraron ejercicios", color = Color.Gray)
                    }
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(ejerciciosFiltrados, key = { it._id }) { ejercicio ->
                            ItemEjercicio(ejercicio = ejercicio, onClick = {
                                navController.navigate("detalleEjercicio/${rutinaIdContext}/${ejercicio._id}")
                            })
                        }
                    }
                }
            }
        }
    }
}

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
            label = { Text(label, fontSize = MaterialTheme.typography.bodySmall.fontSize) },
            textStyle = MaterialTheme.typography.bodySmall,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, fontSize = MaterialTheme.typography.bodySmall.fontSize) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    }
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
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.3f))
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
