package com.example.version00.ui.screens.actividades

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.version00.R
import com.example.version00.ui.data.SvgFatigueHighlighter
import com.example.version00.ui.model.EstadisticasEntrenamiento
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FatigaMuscularView(viewModel: RutinaFirebaseViewModel = viewModel()) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val fatiga = remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    val estadisticas = remember { mutableStateOf(EstadisticasEntrenamiento()) }

    LaunchedEffect(uid) {
        uid?.let {
            viewModel.obtenerFatigaActual(it) { fatiga.value = it }
            viewModel.obtenerEstadisticasDeRutinas(7) {
                estadisticas.value = it
            }
        }
    }

    val fatigaPorId = remember(fatiga.value) {
        fatiga.value.flatMap { (musculo, nivel) ->
            MUSCULOS_FATIGA_ID[musculo.normalizado()]?.map { id -> id to nivel } ?: emptyList()
        }.toMap()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Fatiga", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SvgFatigueHighlighter(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    backgroundImageRes = R.drawable.cuerpo_frente,
                    svgImageRes = R.drawable._cuerpo_delantero,
                    fatigaPorId = fatigaPorId,
                    svgViewBoxWidth = 300f,
                    svgViewBoxHeight = 600f
                )

                SvgFatigueHighlighter(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    backgroundImageRes = R.drawable.cuerpo_espalda_,
                    svgImageRes = R.drawable.cuerpo_espalda,
                    fatigaPorId = fatigaPorId,
                    svgViewBoxWidth = 300f,
                    svgViewBoxHeight = 600f
                )
            }



                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        LegendDot("Debilitado", Color.Cyan)
                        LegendDot("Recuperado", Color.Green)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        LegendDot("En recuperación", Color.Yellow)
                        LegendDot("Fatigado", Color.Red)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Ver Detalles →",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Estadísticas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("7 días", "14 días", "28 días", "90 días").forEach { periodo ->
                Button(
                    onClick = {
                        val dias = periodo.split(" ")[0].toIntOrNull() ?: 7
                        Log.d("BotonPeriodo", "🟢 Botón pulsado: $dias días")
                        uid?.let {
                            viewModel.obtenerEstadisticasDeRutinas(dias) { estadisticas.value = it }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        periodo,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Calorías", color = MaterialTheme.colorScheme.onSurface)
                    Text("${estadisticas.value.calorias} kcal", color = MaterialTheme.colorScheme.onSurface)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Carga", color = MaterialTheme.colorScheme.onSurface)
                    Text("${estadisticas.value.carga.toInt()} kg", color = MaterialTheme.colorScheme.onSurface)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Repeticiones", color = MaterialTheme.colorScheme.onSurface)
                    Text("${estadisticas.value.reps}", color = MaterialTheme.colorScheme.onSurface)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Series", color = MaterialTheme.colorScheme.onSurface)
                    Text("${estadisticas.value.series}", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun LegendDot(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, shape = RoundedCornerShape(50)))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
    }
}

fun String.normalizado(): String = lowercase()
    .replace(Regex("[áàäâ]"), "a")
    .replace(Regex("[éèëê]"), "e")
    .replace(Regex("[íìïî]"), "i")
    .replace(Regex("[óòöô]"), "o")
    .replace(Regex("[úùüû]"), "u")
    .replace("ñ", "n")

val MUSCULOS_FATIGA_ID = mapOf(
    "pectorales" to setOf("pectoral_derecho", "pectoral_izquierdo", "pectorales_derecho", "pectorales_izquierdo"),
    "biceps" to setOf("biceps_derecho", "biceps_izquierdo"),
    "triceps braquial" to setOf("triceps_derecho", "triceps_izquierdo"),
    "recto abdominal" to setOf("abdominal_1", "abdominal_2", "abdominal_3", "abdominal_4", "abdominal_5", "abdominal_6", "abdominal_7", "abdominal_8"),
    "deltoides" to setOf("hombro_derecho", "hombro_izquierdo"),
    "cuadriceps" to setOf("recto_femoral_derecho", "recto_femoral_izquierdo", "vasto_lateral_derecho", "vasto_lateral_izquierdo", "vasto_intermedio_derecho", "vasto_intermedio_izquierdo"),
    "soleo" to setOf("soleo_derecho", "soleo_izquierdo"),
    "oblicuo externo" to setOf("oblicuo_derecho", "oblicuo_izquierdo"),
    "oblicuo inferior" to setOf("oblicuo_inferior_derecho", "oblicuo_inferior_izquierdo"),
    "aductor largo" to setOf("aductor_largo_derecho", "aductor_largo_izquierdo"),
    "sartorio" to setOf("sartorio_derecho", "sartorio_izquierdo"),
    "gastrocnemio" to setOf("gastrocnemio_medial_derecho", "gastrocnemio_medial_izquierdo", "gastrocnemio_lateral_derecho", "gastrocnemio_lateral_izquierdo"),
    "trapecio superior" to setOf("trapecio_superior", "trapecio_superior_izquierdo", "trapecio_superior_derecho"),
    "esternocleidomastoideo" to setOf("esternocleidomastoideo_derecho", "esternocleidomastoideo_izquierdo"),
    "antebrazo superior" to setOf("antebrazo_superior_derecho", "antebrazo_superior_izquierdo"),
    "antebrazo inferior" to setOf("antebrazo_inferior_derecho", "antebrazo_inferior_izquierdo"),
    "Trapecio superior" to setOf("Trapecio_Superior_Izquierdo", "Trapecio_Superior_Derecho"),
    "Deltoides posterior" to setOf("Deltoides_Posterior_Izquierdo", "Deltoides_Posterior_Derecho"),
    "Infraespinoso" to setOf("Infraespinoso_Izquierdo", "Infraespinoso_Derecho"),
    "Dorsal ancho" to setOf("Dorsal_Ancho_Izquierdo", "Dorsal_Ancho_Derecho"),
    "Glúteo medio" to setOf("gluteo_medio_izquierdo", "gluteo_medio_derecho"),
    "Glúteo mayor" to setOf("gluteo_mayor_izquierdo", "gluteo_mayor_derecho"),
    "Isquiotibiales" to setOf("Isquiotibiales_Izquierdo", "Isquiotibiales_Derecho"),
    "Gastrocnemio" to setOf("Gastrocnemio_Izquierdo", "Gastrocnemio_Derecho")
)
