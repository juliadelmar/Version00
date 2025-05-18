package com.example.version00.ui.screens.rutina

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinaScreen(
    rutinaId: Int,
    ejercicios: List<EjercicioGuardado>,
    navController: NavHostController,
    rutinaNombre: String
) {
    val contexto = LocalContext.current
    val rutinaViewModel = remember { RutinaFirebaseViewModel() }
    var mostrarResumen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = rutinaNombre) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray.copy(alpha = 0.3f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
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
                    val series = ejercicio.series
                    val pesosUsados = remember { List(series) { mutableStateOf("") } }
                    val repsHechas = remember { List(series) { mutableStateOf("") } }
                    val repsRecamara = remember { List(series) { mutableStateOf("") } }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(ejercicio.nombre, style = MaterialTheme.typography.titleMedium, color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Plan: ${ejercicio.series} series de ${ejercicio.reps.joinToString()} reps", color = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            repeat(series) { index ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Text("Serie ${index + 1}", color = Color.White)

                                    OutlinedTextField(
                                        value = pesosUsados[index].value,
                                        onValueChange = { pesosUsados[index].value = it },
                                        label = { Text("Kg usados") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        singleLine = true,
                                    )

                                    OutlinedTextField(
                                        value = repsHechas[index].value,
                                        onValueChange = { repsHechas[index].value = it },
                                        label = { Text("Reps hechas") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        singleLine = true,
                                    )

                                    OutlinedTextField(
                                        value = repsRecamara[index].value,
                                        onValueChange = { repsRecamara[index].value = it },
                                        label = { Text("Reps en recámara (RIR)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                        singleLine = true,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { mostrarResumen = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Terminar rutina", color = Color.White)
            }
        }
    }

    if (mostrarResumen) {
        val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val totalSeries = ejercicios.sumOf { it.series }
        val totalReps = ejercicios.sumOf { it.reps.sumOf { rep -> rep.toIntOrNull() ?: 0 } }
        val totalCarga = ejercicios.sumOf { it.pesos.sumOf { peso -> peso.toIntOrNull() ?: 0 } }

        val fatigaPorMusculo = mapOf(
            "Pectoral Mayor" to 4.5,
            "Tríceps Braquial" to 3.2,
            "Deltoides Anterior" to 2.1
        )

        ModalBottomSheet(
            onDismissRequest = { mostrarResumen = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Informe de la Rutina", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(Color.DarkGray.copy(0.3f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Fecha: $fecha", color = Color.White)
                        Text("Ejercicios: ${ejercicios.size}", color = Color.White)
                        Text("Series totales: $totalSeries", color = Color.White)
                        Text("Reps totales: $totalReps", color = Color.White)
                        Text("Carga total: $totalCarga kg", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Fatiga muscular", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                fatigaPorMusculo.forEach { (musculo, fatiga) ->
                    Text("$musculo: ${"%.1f".format(fatiga)} puntos", color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        rutinaViewModel.guardarHistorialFatiga(
                            rutinaId = rutinaId,
                            rutinaNombre = rutinaNombre,
                            fatigaPorMusculo = fatigaPorMusculo,
                            onResult = { success, mensaje ->
                                Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                                if (success) mostrarResumen = false
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Guardar informe", color = Color.White)
                }
            }
        }
    }
}
