package com.example.version00.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.navigation.AppDestinations
import com.example.version00.ui.screens.SemanaEntrenamiento
import com.example.version00.util.ProgresoManager

@Composable
fun SemanasExpandiblesUI(
    semanas: List<SemanaEntrenamiento>,
    navController: NavController,
    progresoPorSemana: List<Int>,
    faseIndex: Int
) {
    val estadoExpandido = remember { mutableStateListOf<Boolean>().apply { repeat(semanas.size) { add(false) } } }
    val context = LocalContext.current

    val diasCompletados = remember {
        mutableStateMapOf<String, Boolean>().apply {
            semanas.forEachIndexed { semanaIndex, semana ->
                semana.dias.forEachIndexed { diaIndex, _ ->
                    val key = "${semanaIndex}_$diaIndex"
                    this[key] = ProgresoManager.esDiaCompletado(context, faseIndex, semanaIndex, diaIndex)
                }
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        semanas.forEachIndexed { semanaIndex, semana ->

            val semanaDesbloqueada = semanaIndex == 0 || run {
                val anterior = semanaIndex - 1
                semanas[anterior].dias.indices.all { i ->
                    ProgresoManager.esDiaCompletado(context, faseIndex, anterior, i)
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = semanaDesbloqueada) {
                            estadoExpandido[semanaIndex] = !estadoExpandido[semanaIndex]
                        }
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Semana ${semanaIndex + 1}: ${semana.nombre}",
                            color = if (semanaDesbloqueada) Color.White else Color.LightGray,
                            fontSize = 16.sp
                        )
                        Text(
                            "${progresoPorSemana.getOrNull(semanaIndex) ?: 0} / ${semana.dias.size} días completados",
                            color = Color(0xFFD1C4E9),
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        imageVector = if (estadoExpandido[semanaIndex]) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = if (semanaDesbloqueada) Color.White else Color.Gray
                    )
                }

                if (estadoExpandido[semanaIndex]) {
                    semana.dias.forEachIndexed { diaIndex, dia ->
                        val key = "${semanaIndex}_$diaIndex"
                        val hecho = diasCompletados[key] == true
                        val anteriorKey = "${semanaIndex}_${diaIndex - 1}"
                        val anteriorHecho = diaIndex == 0 || diasCompletados[anteriorKey] == true
                        val desbloqueado = anteriorHecho

                        val estadoColor = when {
                            hecho -> Color(0xFF81C784)
                            desbloqueado -> Color(0xFF64B5F6)
                            else -> Color.Gray
                        }

                        val totalEjercicios = dia.ejercicios.size
                        val tiempoEstimado = (totalEjercicios * 3).coerceIn(10, 90)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF311B92)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .clickable(enabled = desbloqueado && !hecho) {
                                    val ejerciciosGuardados = dia.ejercicios.map { ejercicio ->
                                        EjercicioGuardado(
                                            id = ejercicio.id,
                                            nombre = ejercicio.nombre,
                                            urlGif = ejercicio.url,
                                            series = 3,
                                            descanso = 60,
                                            porcentajeDeActivacion = ejercicio.activacion ?: emptyMap(),
                                            reps = mutableListOf("12", "12", "12"),
                                            pesos = mutableListOf("0", "0", "0"),
                                            repsRecamara = mutableListOf("2", "2", "2"),
                                            notas = ""
                                        )
                                    }

                                    RutinaTemporalHolder.rutinaId = semanaIndex * 10 + diaIndex
                                    RutinaTemporalHolder.rutinaNombre = dia.nombre
                                    RutinaTemporalHolder.ejercicios = ejerciciosGuardados
                                    RutinaTemporalHolder.semanaIndex = semanaIndex
                                    RutinaTemporalHolder.diaIndex = diaIndex
                                    RutinaTemporalHolder.faseIndex = faseIndex

                                    navController.navigate("predefined_routine/$faseIndex/$semanaIndex/$diaIndex")
                                }
                                .padding(4.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(estadoColor, CircleShape)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(dia.nombre, color = Color.White, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "Duración estimada: ~$tiempoEstimado minutos • $totalEjercicios ejercicios",
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


object RutinaTemporalHolder {
    var faseIndex: Int = 0
    var rutinaId: Int = 0
    var rutinaNombre: String = ""
    var ejercicios: List<EjercicioGuardado> = emptyList()
    var semanaIndex: Int = 0
    var diaIndex: Int = 0
}