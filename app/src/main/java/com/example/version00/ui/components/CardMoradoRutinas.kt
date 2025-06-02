package com.example.version00.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.version00.R

/**
 * Tarjeta visual que representa una rutina existente.
 * Muestra el nombre de la rutina y dos imágenes decorativas.
 *
 * @param modifier Permite personalizar tamaño y posición externa.
 * @param nombreRutina Nombre que se muestra dentro de la tarjeta.
 * @param onClick Acción a ejecutar al pulsar la tarjeta.
 */
@Composable
fun CardMoradoRutinas(
    modifier: Modifier = Modifier,
    nombreRutina: String,
    onClick: () -> Unit
) {
    Card(
        backgroundColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = modifier
            .width(220.dp)
            .height(150.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 150.dp, height = 100.dp)
                .padding(8.dp)
                .clickable(onClick = onClick) // Tarjeta clickeable
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Título con el nombre de la rutina
                Text(
                    text = nombreRutina,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                // Imagen decorativa superior
                Image(
                    painter = painterResource(id = R.drawable.mancuerna_horixontal),
                    contentDescription = "Mancuerna superior",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 18.dp, y = 10.dp)
                )

                // Imagen decorativa inferior
                Image(
                    painter = painterResource(id = R.drawable.ic_mancuerna_oblicua),
                    contentDescription = "Mancuerna inferior",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-14).dp, y = 58.dp)
                )
            }
        }
    }
}
