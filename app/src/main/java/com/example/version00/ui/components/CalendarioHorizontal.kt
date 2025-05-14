package com.example.version00.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarioHorizontal() {
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

    // Cambiar el mes mostrado al deslizar
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collectLatest { index ->
                if (index in days.indices) {
                    selectedMonth = days[index].month
                }
            }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
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
                    color = Color.White,
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

                Column(
                    modifier = Modifier
                        .clickable { }
                        .width(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = Color.White,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")),
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalendarioHorizontal() {
    CalendarioHorizontal()
}
