package com.example.version00.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Representa visualmente una serie dentro de un ejercicio.
 * Muestra datos anteriores (peso y repeticiones), permite ingresar valores nuevos,
 * marcar si se completó la serie y visualizar el tiempo de descanso.
 *
 * @param serieIndex Índice de la serie (para mostrar el número de orden).
 * @param previousPeso Peso sugerido basado en historial.
 * @param previousReps Repeticiones sugeridas basadas en historial.
 * @param descansoSegundos Descanso recomendado después de esta serie (en segundos).
 * @param onCheckedChange Callback al marcar como completada la serie.
 */
@Composable
fun SerieEjercicioItem(
    serieIndex: Int,
    previousPeso: String,
    previousReps: String,
    descansoSegundos: Int,
    onCheckedChange: (Boolean) -> Unit
) {
    // Estados locales para los valores ingresados por el usuario
    var pesoActual by remember { mutableStateOf("") }
    var repsActuales by remember { mutableStateOf("") }
    var recamara by remember { mutableStateOf("") } // RIR: repeticiones en reserva
    var completado by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Fila principal con campos de texto e historial
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Número de serie
            Text(text = "${serieIndex + 1}", modifier = Modifier.width(30.dp), color = Color.White)

            // Peso y repeticiones sugeridos
            Text(
                text = "$previousPeso × $previousReps",
                modifier = Modifier.width(80.dp),
                color = Color.LightGray
            )

            // Campos de entrada: peso, repeticiones, RIR
            OutlinedTextField(
                value = pesoActual,
                onValueChange = { pesoActual = it },
                label = { Text("Kg") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
            )

            OutlinedTextField(
                value = repsActuales,
                onValueChange = { repsActuales = it },
                label = { Text("Reps") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
            )

            OutlinedTextField(
                value = recamara,
                onValueChange = { recamara = it },
                label = { Text("RIR") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
            )

            // Checkbox para marcar si la serie fue completada
            Checkbox(
                checked = completado,
                onCheckedChange = {
                    completado = it
                    onCheckedChange(it)
                },
                colors = CheckboxDefaults.colors(checkedColor = Color.Green)
            )
        }

        // Texto con el tiempo de descanso en formato mm:ss
        Text(
            text = String.format("%d:%02d", descansoSegundos / 60, descansoSegundos % 60),
            color = Color.Cyan,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
        )

        // Línea divisoria entre series
        Divider(color = Color.DarkGray, thickness = 1.dp)
    }
}
