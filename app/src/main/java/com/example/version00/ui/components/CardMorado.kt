package com.example.version00.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.version00.R

@Composable
fun RecuadroMorado(onClick: () -> Unit) {
    var progreso by remember { mutableStateOf(0.75f) }

    Card(
        backgroundColor = Color(0xFF2B1F30), // Morado oscuro
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Entrenamiento\npersonalizado",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(26.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progreso",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${(progreso * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = progreso,
                    color = Color(0xFFB58EDC),
                    backgroundColor = Color.White,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(10.dp)
                )
            }

            // Mancuerna 1 (arriba derecha)
            Image(
                painter = painterResource(id = R.drawable.mancuerna_horixontal),
                contentDescription = "Mancuerna superior",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp) // desplazamiento fino
            )
            Image(
                painter = painterResource(id = R.drawable.ic_mancuerna_oblicua),
                contentDescription = "Mancuerna inferior",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-31).dp, y = 45.dp) // más abajo y hacia la izquierda
            )
        }
    }
}
