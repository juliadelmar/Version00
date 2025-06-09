package com.example.version00.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GraficoTiposEjercicioCanvas(tipos: Map<String, Int>) {
    val total = tipos.values.sum().toFloat()
    val colores = listOf(
        Color(0xFFFF9800), Color(0xFF03A9F4), Color(0xFFE91E63),
        Color(0xFF4CAF50), Color(0xFF9C27B0), Color(0xFF607D8B)
    )

    val angulos = tipos.values.map { (it / total) * 360f }
    val etiquetas = tipos.keys.toList()

    Column(Modifier.padding(16.dp)) {
        Text("Distribución por tipo de ejercicio", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                var startAngle = -90f
                angulos.forEachIndexed { index, sweepAngle ->
                    drawArc(
                        color = colores.getOrElse(index) { Color.Gray },
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true
                    )
                    startAngle += sweepAngle
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        etiquetas.forEachIndexed { index, etiqueta ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(12.dp).background(colores.getOrElse(index) { Color.Gray }, shape = CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(etiqueta, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}
