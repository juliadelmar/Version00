package com.example.version00.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun CalendarioHorizontal(diasConEjercicio: List<LocalDate>) {
    val today = LocalDate.now()
    val startDate = today.minusDays(30)
    val endDate = today.plusDays(30)
    val days = remember {
        generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .toList()
    }

    val listState = rememberLazyListState()
    var selectedMonth by remember { mutableStateOf(today.month) }

    // DEBUG LOGS
    LaunchedEffect(diasConEjercicio) {
        Log.d("Calendario", "Fechas con ejercicio: ${'$'}diasConEjercicio")
    }

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
        // Recuadro morado fijo para el mes
        Card(
            backgroundColor = Color(0xFFB58EDC),
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            modifier = Modifier
                .width(60.dp)
                .height(70.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
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

        // LazyRow scrollable para los días
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
                        .clickable { }
                        .width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val formatter = DateTimeFormatter.ISO_DATE
                    if (diasConEjercicio.any { it.format(formatter) == date.format(formatter) }) {
                        Log.d("Calendario", "✅ Coincidencia en: ${'$'}date")
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

                    Text(
                        text = date.dayOfMonth.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
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
