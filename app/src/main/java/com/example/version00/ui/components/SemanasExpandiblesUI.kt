package com.example.version00.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SemanasExpandiblesUI() {
    val semanas = listOf(
        "Semana 1" to listOf("Piernas y Hombros", "Espalda, Bíceps y Abdomen", "Pecho, Tríceps y Abdomen"),
        "Semana 2" to listOf("Glúteos y Core", "Hombros y Cardio"),
        "Semana 3" to listOf("Full Body", "Push", "Pull"),
        "Semana 4" to listOf("Resistencia Cardio", "Fuerza Explosiva")
    )

    val expandida = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        semanas.forEach { (titulo, sesiones) ->
            Column(modifier = Modifier.fillMaxWidth()) {
                // Cabecera plegable
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandida.value = if (expandida.value == titulo) null else titulo
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (expandida.value == titulo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                if (expandida.value == titulo) {
                    sesiones.forEach { sesion ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2B1F30), shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = sesion,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }
            }
        }
    }
}
