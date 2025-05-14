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

@Composable
fun FaseCard(titulo: String, semanas: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Más fino
            .background(Color(0xFF2B1F30))
            .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    fontSize = 16.sp
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
            Spacer(modifier = Modifier.width(15.dp))

        }
    }
}
