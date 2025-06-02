package com.example.version00.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.components.CircularProgress
import com.example.version00.ui.components.FaseCard
import com.example.version00.ui.components.SemanasExpandiblesUI
import com.example.version00.ui.data.Fase

@Composable
fun ContentTrainingScreen(
    rutinaId: Int,                     // ID de la rutina seleccionada (se puede usar para cargar contenido específico)
    navController: NavHostController   // Controlador de navegación para futuras acciones
) {
    // Lista de fases de entrenamiento
    val listaDeFases = listOf(
        Fase("1: Resistencia", "4 semanas"),
        Fase("2: Hipertrofia", "6 semanas"),
        Fase("3: Fuerza", "3 semanas")
    )

    // Semanas de la fase actual
    val semanas = listOf("Semana 1", "Semana 2", "Semana 3", "Semana 4")
    var semanaSeleccionada by remember { mutableStateOf(semanas[0]) } // Semana seleccionada (a futuro)
    var expanded by remember { mutableStateOf(false) }                // Estado expandido para la UI de semanas

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Imagen de fondo semitransparente con estrellas
        Image(
            painter = painterResource(id = R.drawable.stars_with_transparency),
            contentDescription = "Fondo estrellas",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, vertical = 24.dp)
        ) {
            // Título + progreso circular
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Entrenamiento personalizado",
                    color = Color.White,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(40.dp))
                CircularProgress() // Componente que muestra progreso general
            }

            // Lista de fases como tarjetas
            Text("Fases de entrenamiento", color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(listaDeFases) { fase ->
                    FaseCard(titulo = fase.titulo, semanas = fase.semanas)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progreso en la fase actual (ejemplo estático)
            Text("Fase 1: Resistencia", color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))

            Text("1 de 4 semanas", color = Color(0xFFE1BEE7))
            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = 0.25f, // 25% de progreso
                color = Color(0xFFB39DDB),
                backgroundColor = Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista expandible de semanas con ejercicios (si aplica)
            SemanasExpandiblesUI()
        }
    }
}
