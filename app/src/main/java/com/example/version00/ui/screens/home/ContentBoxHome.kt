package com.example.version00.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.version00.R
import com.example.version00.ui.components.CalendarioHorizontal
import com.example.version00.ui.components.CardMoradoRutinas
import com.example.version00.ui.components.RecuadroMorado
import com.example.version00.ui.data.Rutina
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.time.LocalDate

@Composable
fun ContentBoxHome(
    onNavigateToTraining: () -> Unit,
    onRutinaClick: (Rutina) -> Unit
) {
    val rutinas = listOf(
        Rutina(1, "Rutina 1"),
        Rutina(2, "Rutina 2"),
        Rutina(3, "Rutina 3")
    )
    val viewModel = remember { RutinaFirebaseViewModel() }
    var diasConEjercicio by remember { mutableStateOf(emptyList<LocalDate>()) }

    LaunchedEffect(Unit) {
        viewModel.obtenerDiasConEjercicio {
            diasConEjercicio = it
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        CalendarioHorizontal(diasConEjercicio)
        Spacer(modifier = Modifier.height(30.dp))

        RecuadroMorado(onClick = onNavigateToTraining)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.mmis_rutinas),
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 10.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(rutinas) { rutina ->
                CardMoradoRutinas(
                    nombreRutina = rutina.nombre,
                    onClick = { onRutinaClick(rutina) }
                )
            }
        }
    }
}
