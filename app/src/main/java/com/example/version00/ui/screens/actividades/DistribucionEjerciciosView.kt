package com.example.version00.ui.screens.actividades


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel

@Composable
fun DistribucionEjerciciosView(viewModel: RutinaFirebaseViewModel = viewModel()) {
    val context = LocalContext.current
    val diasSeleccionados = remember { mutableStateOf(7) }
    val tipoEjercicioData = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val zonaMuscularData = remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    LaunchedEffect(diasSeleccionados.value) {

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            "Distribución de Ejercicios",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botones de selección de período
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf(7, 14, 28, 90).forEach { dias ->
                Button(
                    onClick = { diasSeleccionados.value = dias },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (diasSeleccionados.value == dias)
                            MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("$dias días", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico circular por tipo de ejercicio
        Text("Por Tipo de Ejercicio", style = MaterialTheme.typography.titleMedium)
        PieChart(
            data = tipoEjercicioData.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ranking de zonas más entrenadas
        Text("Top Zonas Musculares (Carga)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        zonaMuscularData.value
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .forEach { (zona, carga) ->
                Text("• $zona: ${carga.toInt()} kg", fontSize = 14.sp)
            }
    }
}

@Composable
fun PieChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Color(0xFFEF5350),
        Color(0xFFAB47BC),
        Color(0xFF42A5F5),
        Color(0xFF26A69A),
        Color(0xFFFFA726),
        Color(0xFF66BB6A),
        Color(0xFF7E57C2),
        Color(0xFF78909C)
    )
) {
    if (data.isEmpty()) {
        Text("Sin datos", modifier = modifier.padding(24.dp))
        return
    }

    val total = data.values.sum().toFloat()
    val sweepAngles = data.map { (_, valor) -> 360f * (valor / total) }
    val entries = data.entries.toList()

    Canvas(modifier = modifier) {
        var startAngle = 0f
        entries.forEachIndexed { index, entry ->
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngles[index],
                useCenter = true,
                size = Size(size.width, size.height)
            )
            startAngle += sweepAngles[index]
        }

        // Texto central
        drawIntoCanvas {
            val text = "Total: ${total.toInt()}"
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 36f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            it.nativeCanvas.drawText(
                text,
                size.width / 2,
                size.height / 2,
                paint
            )
        }
    }
}
