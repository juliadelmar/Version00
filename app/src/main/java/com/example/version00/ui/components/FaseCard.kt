package com.example.version00.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.version00.R

/**
 * Tarjeta horizontal que representa una fase del entrenamiento.
 * Muestra título, rango de semanas y una imagen decorativa.
 *
 * @param titulo Nombre de la fase (ej. "Fase de fuerza").
 * @param semanas Texto con duración (ej. "Semana 1-4").
 */
@Composable
fun FaseCard(
    titulo: String,
    semanas: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) Color(0xFF7B1FA2) else Color(0xFF2B1F30)
    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 2.dp,
            color = Color(0xFFE1BEE7),
            shape = RoundedCornerShape(12.dp)
        )
    } else Modifier

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .then(borderModifier)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = titulo,
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = semanas,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            Image(
                painter = painterResource(id = R.drawable.mancuerna_horixontal),
                contentDescription = "Mancuerna",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer(rotationZ = 90f)
                    .offset(x = (-6).dp, y = (-10).dp)
            )
        }
    }
}
