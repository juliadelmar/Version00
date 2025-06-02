package com.example.version00.ui.screens.actividades

import android.content.Context // Import necesario
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext // Import necesario
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
    val context = LocalContext.current // Obtener contexto
    // Obtener SharedPreferences
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    // Ejemplo de uso (no se usa aquí directamente)
    // val ultimaFechaConsultada = sharedPreferences.getString("ultima_fecha_historico", "")

    var historial by remember { mutableStateOf<List<EntradaHistorial>>(emptyList()) }
    var diasConEjercicio by remember { mutableStateOf(emptyList<LocalDate>()) } // No se usa en este composable directamente, pero se mantiene

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    LaunchedEffect(Unit) {
        viewModel.obtenerHistorialFatiga { entradas ->
            historial = entradas
            // Esto podría ser útil si tuvieras un DatePicker que necesitara esta info
            // pero el DatePicker de Material3 no lo usa directamente así.
            diasConEjercicio = entradas.mapNotNull {
                try {
                    LocalDate.parse(it.fecha.take(10), formatter) // Tomar solo la parte de la fecha
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



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            "Histórico",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,

        )

        Spacer(modifier = Modifier.height(24.dp))

        val historialFiltrado = if (selectedDate != null) {
            historial.filter {
                val fechaCorta = it.fecha.take(10) // Extrae 'yyyy-MM-dd'
                fechaCorta == selectedDate.format(formatter)
            }
        } else {
            // Podrías decidir mostrar el historial del día actual por defecto o nada
            // historial.filter { it.fecha.take(10) == LocalDate.now().format(formatter) }
            emptyList() // O mostrar todo el historial: historial
        }


        Text(
            if (selectedDate != null) "Actividades para ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}"
            else "Selecciona una fecha para ver actividades",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (historialFiltrado.isEmpty() && selectedDate != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3A2B4D))
            ) {
                Text(
                    "No hay actividad para esta fecha.",
                    color = Color.LightGray,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }
        } else if (historialFiltrado.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historialFiltrado) { entrada ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1F38))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "${entrada.fecha} — ${entrada.rutinaNombre}", // Fecha completa con hora
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                entrada.musculos,
                                fontSize = 13.sp,
                                color = Color(0xFFCCCCCC)
                            )
                        }
                    }
                }
            }
        }
        // No mostrar nada si no hay fecha seleccionada y no hay historial por defecto.

        Spacer(modifier = Modifier.height(32.dp))
    }
}