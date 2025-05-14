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

@Composable
fun CircularProgress(progress: Float = 0.7f) {
    val porcentaje = (progress * 100).toInt()

    Box(
        contentAlignment = Alignment.Center,

    ) {
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 6.dp, // más fino
            color = Color(0xFFB39DDB),
            backgroundColor = Color(0xFF2B1F30)
        )
        Text(
            text = "$porcentaje%",
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
