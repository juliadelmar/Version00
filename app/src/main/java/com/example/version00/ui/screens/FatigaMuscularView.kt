package com.example.version00.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.version00.R

@Composable
fun FatigaMuscularView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Fatiga Muscular", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "Controlar los niveles de fatiga muscular",
            fontSize = 14.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.delantero),
                        contentDescription = "Cuerpo frontal",
                        modifier = Modifier.height(180.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cuerpo_frenteo),
                        contentDescription = "Cuerpo trasero",
                        modifier = Modifier.height(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Leyenda
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LegendDot("Debilitado", Color.Green)
                    LegendDot("Recuperado", Color.Blue)
                    LegendDot("En recuperación", Color.Yellow)
                    LegendDot("Fatigado", Color.Red)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Ver detalles →", color = Color(0xFFFF9800), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun LegendDot(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, shape = RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}
