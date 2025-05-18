package com.example.version00.ui.screens.cuerpo

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.chart.line.LineChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class RegistroPeso(
    val fecha: String = "",
    val peso: Double = 0.0,
    val meta: Double = 0.0,
    val imc: Double? = null,
    val grasa: Double? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedidasCorporalesScreen() {
    val tabs = listOf("Peso", "Medidas", "Avanzado")
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuerpo") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.DarkGray)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.DarkGray,
                contentColor = Color.White
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> PantallaPeso()
                1 -> TabMedidas()
                2 -> TabAvanzado()
            }
        }
    }
}

@Composable
fun PantallaPeso() {
    val contexto = LocalContext.current
    var mostrarDialogo by remember { mutableStateOf(false) }

    val historial = remember {
        mutableStateListOf(
            RegistroPeso("27 oct. 2024", 66.0, 52.5),
            RegistroPeso("19 feb. 2025", 63.5, 52.5),
            RegistroPeso("26 abr. 2025", 58.5, 52.5),
            RegistroPeso("15 jun. 2025", 56.2, 52.5),
            RegistroPeso("09 oct. 2025", 54.8, 52.5),
            RegistroPeso("02 feb. 2026", 52.5, 52.5)
        )
    }

    val actual = historial.lastOrNull()
    val diferencia = (actual?.peso ?: 0.0) - (actual?.meta ?: 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(colors = CardDefaults.cardColors(Color(0xFF2C2C2C))) {
            Column(Modifier.padding(16.dp)) {
                Text("${actual?.peso} kg", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFFF9800))
                Text(actual?.fecha ?: "", color = Color.Gray)
            }
        }

        Card(colors = CardDefaults.cardColors(Color(0xFF2C2C2C))) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Actual", color = Color(0xFFFF9800)); Text("${actual?.peso} kg", color = Color.White) }
                    Column { Text("Meta", color = Color(0xFF03A9F4)); Text("${actual?.meta} kg", color = Color.White) }
                    Column { Text("Diferencia", color = Color.Gray); Text("${"%.1f".format(diferencia)} kg", color = Color.White) }
                }

                Spacer(modifier = Modifier.height(12.dp))
                LineaPesoChartVico(historial)
            }
        }

        Card(colors = CardDefaults.cardColors(Color(0xFF2C2C2C))) {
            Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                Column { Text("IMC", color = Color.Gray); Text("${actual?.imc ?: "--"}", color = Color.White) }
                Column { Text("Grasa", color = Color.Gray); Text("${actual?.grasa ?: "--"}%", color = Color.White) }
                Column { Text("Peso ideal", color = Color.Gray); Text("49–66 kg", color = Color.White) }
            }
        }

        Button(
            onClick = { mostrarDialogo = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC80)),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+ Agregar", color = Color.Black)
        }
    }

    if (mostrarDialogo) {
        DialogAgregarPeso(
            onDismiss = { mostrarDialogo = false },
            onGuardar = { nuevo ->
                historial.add(nuevo)
                mostrarDialogo = false
                Toast.makeText(contexto, "Peso guardado", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun LineaPesoChartVico(historial: List<RegistroPeso>) {
    val modelo = entryModelOf(*historial.mapIndexed { i, r -> i to r.peso }.toTypedArray())
    val producer = remember { ChartEntryModelProducer(modelo) }

    LineChart(
        chart = lineChart(), // Usar lineChart() en Vico 2.x
        modelProducer = producer,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun DialogAgregarPeso(onDismiss: () -> Unit, onGuardar: (RegistroPeso) -> Unit) {
    var peso by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("") }
    var imc by remember { mutableStateOf("") }
    var grasa by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (peso.isNotBlank()) {
                    val registro = RegistroPeso(
                        fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        peso = peso.toDoubleOrNull() ?: 0.0,
                        meta = meta.toDoubleOrNull() ?: 0.0,
                        imc = imc.toDoubleOrNull(),
                        grasa = grasa.toDoubleOrNull()
                    )
                    onGuardar(registro)
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Agregar peso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso actual (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = meta, onValueChange = { meta = it }, label = { Text("Meta (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = imc, onValueChange = { imc = it }, label = { Text("IMC") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = grasa, onValueChange = { grasa = it }, label = { Text("% Grasa") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        }
    )
}

@Composable
fun TabMedidas() {
    val medidas = remember {
        mutableStateMapOf(
            "Hombros" to "",
            "Pecho" to "",
            "Cintura" to "",
            "Abdomen" to "",
            "Cadera" to "",
            "Brazo izquierdo" to "",
            "Brazo derecho" to "",
            "Muslo izquierdo" to "",
            "Muslo derecho" to "",
            "Pantorrilla izquierda" to "",
            "Pantorrilla derecha" to ""
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(medidas.keys.toList()) { zona ->
            OutlinedTextField(
                value = medidas[zona] ?: "",
                onValueChange = { medidas[zona] = it },
                label = { Text(zona) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Cyan,
                    focusedLabelColor = Color.Cyan,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E)
                )


            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Guardar medidas
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Guardar medidas", color = Color.White)
            }
        }
    }
}

@Composable
fun TabAvanzado() {
    val campos = remember {
        mutableStateMapOf(
            "Altura (cm)" to "",
            "Peso (kg)" to "",
            "IMC" to "",
            "% Grasa" to "",
            "Peso graso (kg)" to "",
            "Peso magro (kg)" to "",
            "Peso óseo (kg)" to "",
            "Pliegue tricipital (mm)" to "",
            "Pliegue abdominal (mm)" to "",
            "Pliegue subescapular (mm)" to "",
            "Pliegue suprailiaco (mm)" to "",
            "Diámetro de muñeca (cm)" to "",
            "Diámetro de fémur (cm)" to ""
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(campos.keys.toList()) { label ->
            OutlinedTextField(
                value = campos[label] ?: "",
                onValueChange = { campos[label] = it },
                label = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Cyan,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Cyan,
                    focusedLabelColor = Color.Cyan,
                    unfocusedLabelColor = Color.LightGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E)
                )

            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Guardar avanzado
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Guardar avanzado", color = Color.White)
            }
        }
    }
}