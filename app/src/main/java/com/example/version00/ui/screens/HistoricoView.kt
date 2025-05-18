package com.example.version00.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class EntradaHistorial(
    val fecha: String,
    val rutinaNombre: String,
    val musculos: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoView(viewModel: RutinaFirebaseViewModel = viewModel()) {
    var historial by remember { mutableStateOf<List<EntradaHistorial>>(emptyList()) }
    var diasConEjercicio by remember { mutableStateOf(emptyList<LocalDate>()) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Obtener historial al iniciar
    LaunchedEffect(Unit) {
        viewModel.obtenerHistorialFatiga { entradas ->
            historial = entradas

            // Extraer días con ejercicio del historial
            diasConEjercicio = entradas.mapNotNull {
                try {
                    LocalDate.parse(it.fecha, formatter)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Historial mensual", style = MaterialTheme.typography.titleLarge, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false
        )

        Spacer(modifier = Modifier.height(24.dp))

        val historialFiltrado = if (selectedDate != null) {
            historial.filter {
                it.fecha == selectedDate.format(formatter)
            }
        } else historial

        if (historialFiltrado.isEmpty()) {
            Text("No hay actividad para esta fecha.", color = Color.LightGray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historialFiltrado) { entrada ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${entrada.fecha}  ${entrada.rutinaNombre}", fontSize = 16.sp, color = Color.White)
                                Text(entrada.musculos, fontSize = 12.sp, color = Color.LightGray)
                            }
                            Text(">", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
