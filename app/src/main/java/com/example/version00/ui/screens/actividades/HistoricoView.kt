package com.example.version00.ui.screens.actividades

// Imports necesarios para UI, estado, fecha y lógica de negocio
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// Estructura de datos que representa una entrada del historial
data class EntradaHistorial(
    val fecha: String,          // Fecha en formato completo
    val rutinaNombre: String,   // Nombre de la rutina realizada
    val musculos: String        // Músculos implicados en esa rutina
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoView(viewModel: RutinaFirebaseViewModel = viewModel()) {
    val context = LocalContext.current

    // SharedPreferences: se puede usar para guardar última fecha seleccionada, etc.
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    // Estado para el historial obtenido de Firebase
    var historial by remember { mutableStateOf<List<EntradaHistorial>>(emptyList()) }

    // Se mantiene aunque no se usa directamente en esta vista. Podría ser útil si se quisiera
    // marcar días con actividad en un calendario más avanzado.
    var diasConEjercicio by remember { mutableStateOf(emptyList<LocalDate>()) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Cargar historial al ingresar en la vista
    LaunchedEffect(Unit) {
        viewModel.obtenerHistorialFatiga { entradas ->
            historial = entradas
            diasConEjercicio = entradas.mapNotNull {
                try {
                    LocalDate.parse(it.fecha.take(10), formatter) // Convertir a LocalDate solo la parte 'yyyy-MM-dd'
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    // Estado del selector de fecha de Material 3
    val datePickerState = rememberDatePickerState()

    // Convertir la fecha seleccionada (epoch millis) a LocalDate
    val selectedDate = datePickerState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    // UI Principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo negro para contraste con tarjetas
            .padding(16.dp)
    ) {
        Text(
            "Histórico",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Componente DatePicker de Material 3
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Filtrar el historial por la fecha seleccionada
        val historialFiltrado = if (selectedDate != null) {
            historial.filter {
                val fechaCorta = it.fecha.take(10)
                fechaCorta == selectedDate.format(formatter)
            }
        } else {
            emptyList() // También podrías mostrar todo por defecto o según una lógica propia
        }

        // Texto indicativo de la selección
        Text(
            if (selectedDate != null)
                "Actividades para ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}"
            else
                "Selecciona una fecha para ver actividades",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Si no hay actividades para esa fecha, mostrar una tarjeta informativa
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
        }
        // Si hay actividades, mostrarlas en una lista
        else if (historialFiltrado.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historialFiltrado) { entrada ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B1F38))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "${entrada.fecha} — ${entrada.rutinaNombre}",
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

        Spacer(modifier = Modifier.height(32.dp))
    }
}
