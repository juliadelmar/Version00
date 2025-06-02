package com.example.version00.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Indicador circular de progreso con un porcentaje en el centro.
 *
 * @param progress Valor del progreso entre 0f y 1f (por defecto 70%).
 */
@Composable
fun CircularProgress(progress: Float = 0.7f) {
    val porcentaje = (progress * 100).toInt()

    Box(
        contentAlignment = Alignment.Center, // Centra el texto dentro del círculo
    ) {
        // Círculo de progreso
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 6.dp,
            color = Color(0xFFB39DDB),          // Color del progreso
            backgroundColor = Color(0xFF2B1F30) // Color del fondo del círculo
        )

        // Texto con el porcentaje en el centro
        Text(
            text = "$porcentaje%",
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
