package com.example.version00.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        CalendarioHorizontal()
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
