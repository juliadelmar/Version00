package com.example.version00.ui.screens.actividades

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
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
    val sharedPreferences = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

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
            ) {
                SvgFatigueHighlighter(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    backgroundImageRes = R.drawable.cuerpo_frente,
                    svgImageRes = R.drawable._cuerpo_delantero,
                    fatigaPorId = fatigaPorId,
                    svgViewBoxWidth = 300f,
                    svgViewBoxHeight = 600f
                )

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
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
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
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Regiones más entrenadas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cuerpo_frenteo),
                        contentDescription = "Frontal",
                        modifier = Modifier.height(120.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.cuerpo_frenteo), // cámbialo si tienes vista posterior
                        contentDescription = "Espalda",
                        modifier = Modifier.height(120.dp)
                    )
                }
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
    "pectorales" to setOf("pectoral_derecho", "pectoral_izquierdo"),
    "biceps" to setOf("biceps_derecho", "biceps_izquierdo"),
    "triceps braquial" to setOf("triceps_derecho", "triceps_izquierdo"),
    "recto abdominal" to setOf("abdominal_1", "abdominal_2", "abdominal_3", "abdominal_4"),
    "deltoides" to setOf("hombro_derecho", "hombro_izquierdo"),
    "cuadriceps" to setOf("recto_femoral_derecho", "recto_femoral_izquierdo"),
    "soleo" to setOf("soleo_derecho", "soleo_izquierdo"),
    "oblicuo externo" to setOf("oblicuo_derecho", "oblicuo_izquierdo"),
    "aductor largo" to setOf("aductor_largo_derecho", "aductor_largo_izquierdo"),
    "gastrocnemio" to setOf("gastrocnemio_medial_derecho", "gastrocnemio_medial_izquierdo")
)
