// Archivo: RutinaPredefinidaScreen.kt
package com.example.version00.ui.screens.rutina

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.navigation.AppDestinations
import com.example.version00.util.ProgresoManager
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaPredefinidaScreen(
    rutinaId: Int,
    rutinaNombre: String,
    ejercicios: List<EjercicioGuardado>,
    navController: NavHostController,
    rutinaViewModel: RutinaFirebaseViewModel,
    faseIndex: Int,
    semanaIndex: Int,
    diaIndex: Int
) {
    val contexto = LocalContext.current
    val pesosUsados = remember { mutableStateMapOf<String, List<MutableState<String>>>() }
    val checksSeries = remember { mutableStateMapOf<String, List<MutableState<Boolean>>>() }
    var mostrarResumen by remember { mutableStateOf(false) }

    ejercicios.forEach { ejercicio ->
        val id = ejercicio.id.toString()
        if (pesosUsados[id].isNullOrEmpty()) {
            pesosUsados[id] = List(ejercicio.series) { mutableStateOf("0") }
            checksSeries[id] = List(ejercicio.series) { mutableStateOf(false) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(rutinaNombre) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(ejercicios) { ejercicio ->
                    val id = ejercicio.id.toString()
                    val pesos = pesosUsados[id]!!
                    val checks = checksSeries[id]!!

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = ejercicio.urlGif,
                                contentDescription = ejercicio.nombre,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Plan: ${ejercicio.series} series de ${ejercicio.reps.joinToString()} reps",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Etiquetas: Kg - Reps - RIR
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(48.dp))
                            listOf("Kg", "Reps", "RIR").forEach {
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    Text(it, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        repeat(ejercicio.series) { index ->
                            val isChecked = checks[index].value
                            val decoration = if (isChecked) TextDecoration.LineThrough else null
                            val reps = ejercicio.reps.getOrNull(index) ?: ""
                            val rir = ejercicio.repsRecamara.getOrNull(index) ?: ""

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checks[index].value = it }
                                )

                                Text("S${index + 1}", modifier = Modifier.width(32.dp))

                                EditableField(
                                    value = pesos[index].value,
                                    onChange = { pesos[index].value = it },
                                    label = "Kg",
                                    enabled = !isChecked,
                                    decoration = decoration,
                                    modifier = Modifier.weight(1f)
                                )

                                ReadOnlyField(
                                    text = reps,
                                    decoration = decoration,
                                    modifier = Modifier.weight(1f)
                                )

                                ReadOnlyField(
                                    text = rir,
                                    decoration = decoration,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { mostrarResumen = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Terminar rutina")
            }

            if (mostrarResumen) {
                RutinaResumenModal(
                    rutinaId = rutinaId,
                    rutinaNombre = rutinaNombre,
                    ejercicios = ejercicios,
                    pesosUsadosPorEjercicio = pesosUsados,
                    repsHechasPorEjercicio = ejercicios.associate {
                        it.id.toString() to it.reps.map { rep -> mutableStateOf(rep) }
                    },
                    repsRecamaraPorEjercicio = ejercicios.associate {
                        it.id.toString() to it.repsRecamara.map { rir -> mutableStateOf(rir) }
                    },
                    seriesCheckeadasPorEjercicio = checksSeries,
                    rutinaViewModel = rutinaViewModel,
                    navController = navController,
                    onDismiss = { mostrarResumen = false },

                    )
            }
        }
    }
}



@Composable
fun ReadOnlyField(
    text: String,
    decoration: TextDecoration?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = LocalTextStyle.current.copy(
                textDecoration = decoration,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}