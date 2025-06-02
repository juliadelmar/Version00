package com.example.version00.ui.components

// Imports necesarios para layout, estado, fechas y estilos
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Calendario horizontal que muestra 60 días (30 antes y 30 después del día actual).
 * Marca los días donde hay actividad con una barra morada.
 *
 * @param diasConEjercicio Lista de fechas donde el usuario ha hecho ejercicio.
 */
@Composable
fun CalendarioHorizontal(diasConEjercicio: List<LocalDate>) {
    val today = LocalDate.now()
    val startDate = today.minusDays(30)
    val endDate = today.plusDays(30)

    // Lista de fechas a mostrar
    val days = remember {
        generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .toList()
    }

    val listState = rememberLazyListState()
    var selectedMonth by remember { mutableStateOf(today.month) }

    // Log de depuración
    LaunchedEffect(diasConEjercicio) {
        Log.d("Calendario", "Fechas con ejercicio: $diasConEjercicio")
    }

    // Auto scroll al día actual al iniciar
    LaunchedEffect(Unit) {
        val todayIndex = days.indexOf(today)
        if (todayIndex != -1) {
            listState.scrollToItem(todayIndex)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Muestra el nombre del mes en un recuadro morado
        Card(
            backgroundColor = Color(0xFFB58EDC),
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            modifier = Modifier
                .width(60.dp)
                .height(70.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = selectedMonth.name.take(3).lowercase(Locale.getDefault())
                        .replaceFirstChar { it.titlecase(Locale.getDefault()) },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Lista horizontal de días
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(days.size) { index ->
                val date = days[index]
                Log.d("Calendario", "Día mostrado: $date")

                Column(
                    modifier = Modifier
                        .clickable { /* futuro: manejar selección */ }
                        .width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Si hay ejercicio en esa fecha, muestra una barra indicadora
                    val formatter = DateTimeFormatter.ISO_DATE
                    if (diasConEjercicio.any { it.format(formatter) == date.format(formatter) }) {
                        Log.d("Calendario", "✅ Coincidencia en: $date")
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(20.dp)
                                .background(Color(0xFFB58EDC), shape = RoundedCornerShape(50))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Día del mes
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Día de la semana en español (ej. lun, mar, etc.)
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
