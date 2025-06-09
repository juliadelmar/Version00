package com.example.version00.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.data.*
import com.example.version00.ui.components.FaseCard
import com.example.version00.ui.components.SemanasExpandiblesUI
import com.example.version00.ui.data.Fase
import com.example.version00.ui.screens.FaseEntrenamiento
import com.example.version00.util.ProgresoManager

@Composable
fun ContentTrainingScreen(
    rutinaId: Int,
    navController: NavHostController
) {
    val context = LocalContext.current

    val fases = listOf(
        FaseEntrenamiento("Fase 1: Resistencia", ResistenciaBuilder.build()),
        FaseEntrenamiento("Fase 2: Hipertrofia", HipertrofiaBuilder.build()),
        FaseEntrenamiento("Fase 3: Fuerza", FuerzaBuilder.build())
    )

    var faseSeleccionadaIndex by remember { mutableStateOf(0) }
    val faseSeleccionada = fases[faseSeleccionadaIndex]

    // 🧠 Estado del progreso por fase
    var totalCompletados by remember { mutableStateOf(0) }
    var completadosPorSemana by remember { mutableStateOf(listOf<Int>()) }

    // 🧠 Estado del progreso general
    val diasPorSemana = faseSeleccionada.semanas.map { it.dias.size }
    val totalDias = diasPorSemana.sum()
    val progresoGeneral = if (totalDias == 0) 0f else totalCompletados.toFloat() / totalDias

    // 🔁 Recalcular progreso cuando cambia de fase
    LaunchedEffect(faseSeleccionadaIndex) {
        val (total, porSemana) = ProgresoManager.contarDiasCompletados(
            context = context,
            faseIndex = faseSeleccionadaIndex,
            semanas = faseSeleccionada.semanas.size,
            diasPorSemana = diasPorSemana
        )
        totalCompletados = total
        completadosPorSemana = porSemana
    }

    val listaDeFasesUI = listOf(
        Fase("1: Resistencia", "4 semanas"),
        Fase("2: Hipertrofia", "6 semanas"),
        Fase("3: Fuerza", "3 semanas")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.stars_with_transparency),
            contentDescription = "Fondo estrellas",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.2f
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Entrenamiento personalizado",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    CircularProgressIndicator(
                        progress = progresoGeneral,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFFB39DDB),
                        trackColor = Color.DarkGray
                    )
                }
            }

            item {
                Text("Fases de entrenamiento", color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(listaDeFasesUI) { faseUI ->
                        val index = listaDeFasesUI.indexOf(faseUI)
                        FaseCard(
                            titulo = faseUI.titulo,
                            semanas = faseUI.semanas,
                            modifier = Modifier.clickable {
                                faseSeleccionadaIndex = index
                            },
                            isSelected = (index == faseSeleccionadaIndex)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(faseSeleccionada.nombre, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progresoGeneral,
                    color = Color(0xFFB39DDB),
                    backgroundColor = Color.Gray.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                )
                Text(
                    "$totalCompletados de $totalDias días completados",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                SemanasExpandiblesUI(
                    semanas = faseSeleccionada.semanas,
                    navController = navController,
                    progresoPorSemana = completadosPorSemana,
                    faseIndex = faseSeleccionadaIndex
                )
            }
        }
    }
}
