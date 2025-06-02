package com.example.version00.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun FaseCard(titulo: String, semanas: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Altura reducida
            .background(Color(0xFF2B1F30)) // Fondo oscuro
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Columna con texto
            Column {
                Text(
                    text = titulo,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = semanas,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            // Imagen decorativa girada (mancuerna)
            Image(
                painter = painterResource(id = R.drawable.mancuerna_horixontal),
                contentDescription = "Mancuerna",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer(rotationZ = 90f)
                    .offset(x = (-6).dp, y = (-10).dp)
            )

            Spacer(modifier = Modifier.width(15.dp))
        }
    }
}
