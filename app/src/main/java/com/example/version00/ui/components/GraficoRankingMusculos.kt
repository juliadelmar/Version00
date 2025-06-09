package com.example.version00.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun GraficoZonasEntrenadas(zonas: Map<String, Float>) {
    val maxValor = zonas.values.maxOrNull() ?: 1f
    val colores = listOf(Color(0xFF2196F3), Color(0xFFF44336), Color(0xFF4CAF50), Color(0xFFFFC107))

    Column(Modifier.padding(16.dp)) {
        Text("Zonas más entrenadas", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        zonas.entries.sortedByDescending { it.value }.take(5).forEachIndexed { index, (zona, valor) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(zona, modifier = Modifier.width(100.dp), fontSize = 13.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(valor / maxValor)
                            .background(colores.getOrElse(index) { Color.Gray })
                    )
                }
            }
        }
    }
}
