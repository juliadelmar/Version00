package com.example.version00.ui.screens.actividades

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.version00.ui.viewmodel.CalendarioMenstrualViewModel
import com.example.version00.ui.viewmodel.PeriodoConfirmado
import com.example.version00.ui.viewmodel.Predicciones
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

val colorEntreno = Color(0xFF1976D2)
val colorPeriodoConfirmado = Color(0xFFD32F2F)
val colorPeriodoProbableBorde = Color(0xFFE57373)
val colorFertil = Color(0xFF388E3C)
val colorOvulacion = Color(0xFF0288D1)
val colorHoyBorde = Color(0xFF7B1FA2)
val colorFondoDiaHoy = Color(0xFFF3E5F5)

@Composable
fun CalendarioEntrenamientoScreen(
    navController: NavHostController,
    diasConEntrenamiento: List<LocalDate>,
    obtenerHistorialParaFecha: (LocalDate) -> List<String>,
    cicloViewModel: CalendarioMenstrualViewModel
) {
    var mesActual by remember { mutableStateOf(YearMonth.now()) }
    var diaSeleccionado by remember { mutableStateOf<LocalDate?>(null) }
    val periodosConfirmados by cicloViewModel.periodosConfirmados
    val predicciones by cicloViewModel.predicciones
    val periodoActivoSinFin = cicloViewModel.periodoActivoSinFin

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CalendarioHeader(
                    mesActual = mesActual,
                    onMesAnterior = { mesActual = mesActual.minusMonths(1) },
                    onMesSiguiente = { mesActual = mesActual.plusMonths(1) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CalendarioGridActividades(
                    mesActual = mesActual,
                    diasConEntrenamiento = diasConEntrenamiento,
                    periodosConfirmados = periodosConfirmados,
                    predicciones = predicciones,
                    onDiaClick = { diaSeleccionado = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                CalendarioLeyenda()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Toca un día para ver tus rutinas o registrar tu ciclo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }

    diaSeleccionado?.let { fecha ->
        val eventos = obtenerHistorialParaFecha(fecha)
        val esInicioPeriodo = periodosConfirmados.any { it.inicio == fecha }
        val puedeMarcarFin = periodoActivoSinFin != null && !fecha.isBefore(periodoActivoSinFin.inicio)

        AlertDialog(
            onDismissRequest = { diaSeleccionado = null },
            confirmButton = {
                TextButton(onClick = { diaSeleccionado = null }) { Text("Cerrar") }
            },
            title = {
                Text("Acciones para ${fecha.dayOfMonth}/${fecha.monthValue}/${fecha.year}")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (eventos.isNotEmpty()) {
                        Text("Entrenamientos registrados:")
                        eventos.forEach { evento -> Text("• $evento") }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (periodoActivoSinFin == null && !esInicioPeriodo) {
                        Button(onClick = {
                            cicloViewModel.marcarInicioPeriodo(fecha)
                            diaSeleccionado = null
                        }) {
                            Text("Marcar Inicio de Periodo")
                        }
                    }

                    if (puedeMarcarFin && periodoActivoSinFin?.inicio != fecha) {
                        Button(onClick = {
                            cicloViewModel.marcarFinPeriodo(fecha)
                            diaSeleccionado = null
                        }) {
                            Text("Marcar Fin de Periodo")
                        }
                    }

                    if (esInicioPeriodo) {
                        Button(
                            onClick = {
                                cicloViewModel.desmarcarPeriodo(fecha)
                                diaSeleccionado = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text("Desmarcar Periodo")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun CalendarioGridActividades(
    mesActual: YearMonth,
    diasConEntrenamiento: List<LocalDate>,
    periodosConfirmados: List<PeriodoConfirmado>,
    predicciones: Predicciones,
    onDiaClick: (LocalDate) -> Unit
) {
    val hoy = LocalDate.now()
    val primerDiaDelMes = mesActual.atDay(1)
    val offsetDias = (primerDiaDelMes.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
    val diasEnMes = mesActual.lengthOfMonth()
    val diasAMostrar = (1..diasEnMes).map { mesActual.atDay(it) }
    val placeholdersInicio = List(offsetDias) { null }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 380.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        userScrollEnabled = false
    ) {
        items(placeholdersInicio.size) {
            Box(modifier = Modifier.aspectRatio(1f))
        }

        items(diasAMostrar) { dia ->
            val esHoy = dia == hoy
            val esPeriodoConfirmado = periodosConfirmados.any { it.contiene(dia) }
            val esOvulacion = dia in predicciones.diasOvulacion && !esPeriodoConfirmado
            val esFertil = dia in predicciones.diasFertiles && !esPeriodoConfirmado && !esOvulacion
            val esPeriodoProbable = dia in predicciones.diasPeriodoProbables && !esPeriodoConfirmado && !esOvulacion && !esFertil
            val tieneEntrenamiento = dia in diasConEntrenamiento

            var modifierDia = Modifier
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickable { onDiaClick(dia) }

            var textColor = MaterialTheme.colorScheme.onSurface

            when {
                esPeriodoConfirmado -> {
                    modifierDia = modifierDia.background(colorPeriodoConfirmado)
                    textColor = MaterialTheme.colorScheme.onPrimary
                }
                esOvulacion -> {
                    modifierDia = modifierDia.background(colorOvulacion.copy(alpha = 0.9f))
                    textColor = MaterialTheme.colorScheme.onPrimary
                }
                esFertil -> {
                    modifierDia = modifierDia.background(colorFertil.copy(alpha = 0.8f))
                    textColor = MaterialTheme.colorScheme.onPrimary
                }
                esPeriodoProbable -> {
                    modifierDia = modifierDia.border(2.dp, colorPeriodoProbableBorde, CircleShape)
                    if (esHoy) textColor = MaterialTheme.colorScheme.primary
                }
                esHoy -> {
                    modifierDia = modifierDia
                        .border(2.dp, colorHoyBorde, CircleShape)
                        .background(colorFondoDiaHoy.copy(alpha = 0.3f))
                    textColor = MaterialTheme.colorScheme.primary
                }
            }

            Box(modifier = modifierDia, contentAlignment = Alignment.Center) {
                Text(
                    text = dia.dayOfMonth.toString(),
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal
                )
                if (tieneEntrenamiento) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = colorEntreno,
                            radius = 4.dp.toPx(),
                            center = Offset(size.width * 0.5f, size.height * 0.80f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarioHeader(
    mesActual: YearMonth,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onMesAnterior) {
            Icon(Icons.Filled.KeyboardArrowLeft, "Mes Anterior", tint = MaterialTheme.colorScheme.primary)
        }
        Text(
            text = "${mesActual.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.titlecase(Locale("es")) }} ${mesActual.year}",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onMesSiguiente) {
            Icon(Icons.Filled.KeyboardArrowRight, "Mes Siguiente", tint = MaterialTheme.colorScheme.primary)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(Modifier.fillMaxWidth()) {
        listOf("Lu", "Ma", "Mi", "Ju", "Vi", "Sá", "Do").forEach { dia ->
            Text(
                text = dia,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CalendarioLeyenda() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Leyenda:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        LeyendaItem(color = colorEntreno, label = "Día de Entrenamiento (Punto Azul)")
        LeyendaItem(color = colorPeriodoConfirmado, label = "Periodo Confirmado")
        LeyendaItem(color = colorFertil, label = "Días Fértiles")
        LeyendaItem(color = colorOvulacion, label = "Día de Ovulación")
        LeyendaItem(borderColor = colorPeriodoProbableBorde, label = "Periodo Probable (Borde)", isBordered = true)
        LeyendaItem(borderColor = colorHoyBorde, label = "Día Actual (Borde)", isBordered = true, hasSlightBackground = true)
    }
}

@Composable
fun LeyendaItem(
    label: String,
    color: Color? = null,
    borderColor: Color? = null,
    isBordered: Boolean = false,
    hasSlightBackground: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        var boxModifier = Modifier
            .size(16.dp)
            .clip(CircleShape)

        if (isBordered && borderColor != null) {
            boxModifier = boxModifier.border(1.5.dp, borderColor, CircleShape)
            if (hasSlightBackground) {
                boxModifier = boxModifier.background(colorFondoDiaHoy.copy(alpha = 0.5f))
            }
        } else if (color != null) {
            boxModifier = boxModifier.background(color)
        }

        Box(modifier = boxModifier)

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
