package com.example.version00.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SerieEjercicioItem(
    serieIndex: Int,
    previousPeso: String,
    previousReps: String,
    descansoSegundos: Int,
    onCheckedChange: (Boolean) -> Unit
) {
    var pesoActual by remember { mutableStateOf("") }
    var repsActuales by remember { mutableStateOf("") }
    var recamara by remember { mutableStateOf("") }
    var completado by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "${serieIndex + 1}", modifier = Modifier.width(30.dp), color = Color.White)

            Text(
                text = "$previousPeso × $previousReps",
                modifier = Modifier.width(80.dp),
                color = Color.LightGray
            )

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

            Checkbox(
                checked = completado,
                onCheckedChange = {
                    completado = it
                    onCheckedChange(it)
                },
                colors = CheckboxDefaults.colors(checkedColor = Color.Green)
            )
        }

        Text(
            text = String.format("%d:%02d", descansoSegundos / 60, descansoSegundos % 60),
            color = Color.Cyan,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
        )
        Divider(color = Color.DarkGray, thickness = 1.dp)
    }
}

