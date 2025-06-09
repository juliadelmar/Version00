
package com.example.version00.ui.screens.cuerpo

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.version00.ui.data.DatosAvanzados
import com.example.version00.ui.data.MedidasDetalladas
import com.example.version00.ui.data.RegistroPeso
import com.example.version00.ui.viewmodel.HistorialViewModel
import com.example.version00.ui.viewmodel.MedidasViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedidasCorporalesScreen(
    navController: NavHostController,
    viewModel: MedidasViewModel = viewModel()
) {
    val tabs = listOf("Peso", "Medidas", "Avanzado")
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.inicializar(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cuerpo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
                0 -> TabPeso(viewModel)
                1 -> TabMedidas(viewModel)
                2 -> TabAvanzado(viewModel)
            }
        }
    }
}
@Composable
fun DialogAgregarPeso(
    onDismiss: () -> Unit,
    onGuardar: (peso: Double, meta: Double, imc: Double?, grasa: Double?) -> Unit,
    ultimaMeta: Double,
    viewModel: MedidasViewModel // Añadido para usar el cálculo IMC
) {
    var peso by remember { mutableStateOf("") }
    var metaInput by remember { mutableStateOf("") }
    var imc by remember { mutableStateOf("") }
    var grasa by remember { mutableStateOf("") }

    val contexto = LocalContext.current
    val alturaStr by viewModel.altura.collectAsState()
    val altura = alturaStr.toDoubleOrNull()

    // Autoasignar meta al iniciar
    LaunchedEffect(ultimaMeta) {
        if (metaInput.isBlank() && ultimaMeta > 0.0) {
            metaInput = ultimaMeta.toString()
        }
    }

    // Calcular IMC automáticamente si se ingresa peso
    LaunchedEffect(peso, altura) {
        val pesoDouble = peso.toDoubleOrNull()
        if (pesoDouble != null && altura != null && altura > 0) {
            imc = viewModel.calcularIMC(pesoDouble, altura)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val pesoDouble = peso.toDoubleOrNull()
                val metaDouble = metaInput.toDoubleOrNull()
                val imcDouble = imc.toDoubleOrNull()
                val grasaDouble = grasa.toDoubleOrNull()

                if (pesoDouble != null && pesoDouble > 0) {
                    onGuardar(pesoDouble, metaDouble ?: ultimaMeta, imcDouble, grasaDouble)
                } else {
                    Toast.makeText(contexto, "Por favor, introduce un peso válido.", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Agregar peso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso actual (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = metaInput,
                    onValueChange = { metaInput = it },
                    label = { Text("Meta (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = imc,
                    onValueChange = { },
                    label = { Text("IMC (calculado)") },
                    readOnly = true,
                    singleLine = true
                )
                OutlinedTextField(
                    value = grasa,
                    onValueChange = { grasa = it },
                    label = { Text("% Grasa (opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    )
}


@Composable
fun TabPeso(viewModel: MedidasViewModel) {
    val contexto = LocalContext.current
    var mostrarDialogoAgregarPeso by remember { mutableStateOf(false) }
    var mostrarDialogoAlturaEdad by remember { mutableStateOf(false) }

    val historial by viewModel.historialPeso.collectAsState()
    val actual = remember(historial) { historial.lastOrNull() }
    val diferencia = (actual?.peso ?: 0.0) - (actual?.meta ?: 0.0)
    val ultimaMeta = actual?.meta ?: 0.0

    val altura by viewModel.altura.collectAsState()
    val edad by viewModel.edad.collectAsState()

    val imcCalculado = remember(actual?.peso, altura) {
        val peso = actual?.peso ?: return@remember "--"
        val alturaDouble = altura.toDoubleOrNull()
        if (alturaDouble != null && alturaDouble > 0) {
            viewModel.calcularIMC(peso, alturaDouble)
        } else "--"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(
            onClick = { mostrarDialogoAlturaEdad = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("⚠️ ¿No ves tu IMC o grasa? Toca aquí para agregar altura y edad.", color = MaterialTheme.colorScheme.primary)
        }

        Card(colors = CardDefaults.cardColors(Color(0xFF2C2C2C))) {
            Column(Modifier.padding(16.dp)) {
                Text("${actual?.peso ?: "--"} kg", style = MaterialTheme.typography.headlineMedium, color = Color(0xFFFF9800))
                Text(actual?.fecha ?: "Sin fecha", color = Color.Gray)
            }
        }

        Card(colors = CardDefaults.cardColors(Color(0xFF2C2C2C))) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("Actual", color = Color(0xFFFF9800)); Text("${actual?.peso ?: "--"} kg", color = Color.White) }
                    Column { Text("Meta", color = Color(0xFF03A9F4)); Text("${actual?.meta ?: "--"} kg", color = Color.White) }
                    Column { Text("Diferencia", color = Color.Gray); Text("${"%.1f".format(diferencia)} kg", color = Color.White) }
                }

                Spacer(modifier = Modifier.height(12.dp))
                if (historial.isNotEmpty()) {
                    LineaPesoChartCanvas(historial)
                } else {
                    Text("No hay datos suficientes para mostrar el gráfico.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }

        Card(colors = CardDefaults.cardColors(Color(0xFF2C2C2C))) {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("IMC", color = Color.Gray)
                    Text(imcCalculado, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Grasa", color = Color.Gray)
                    Text("${actual?.grasa?.let { "$it%" } ?: "--%"}", color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Peso ideal", color = Color.Gray)
                    Text("49–66 kg", color = Color.White)
                }
            }
        }

        Button(
            onClick = { mostrarDialogoAgregarPeso = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC80)),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("+ Agregar", color = Color.Black)
        }
    }

    if (mostrarDialogoAgregarPeso) {
        DialogAgregarPeso(
            onDismiss = { mostrarDialogoAgregarPeso = false },
            onGuardar = { peso, meta, imc, grasa ->
                viewModel.agregarRegistroPeso(peso, meta, imc, grasa)
                mostrarDialogoAgregarPeso = false
                Toast.makeText(contexto, "Peso guardado", Toast.LENGTH_SHORT).show()
            },
            ultimaMeta = ultimaMeta
        )
    }

    if (mostrarDialogoAlturaEdad) {
        DialogAgregarAlturaEdad(
            alturaActual = altura,
            edadActual = edad,
            onGuardar = { nuevaAltura, nuevaEdad ->
                val altDouble = nuevaAltura.toDoubleOrNull()
                val edadInt = nuevaEdad.toIntOrNull()
                if (altDouble != null && edadInt != null) {
                    viewModel.guardarAlturaYEdad(altDouble, edadInt)
                    Toast.makeText(contexto, "Altura y edad guardadas", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(contexto, "Datos inválidos", Toast.LENGTH_SHORT).show()
                }
                mostrarDialogoAlturaEdad = false
            },
            onDismiss = { mostrarDialogoAlturaEdad = false }
        )
    }
}
@Composable
fun DialogAgregarAlturaEdad(
    alturaActual: String,
    edadActual: String,
    onGuardar: (altura: String, edad: String) -> Unit,
    onDismiss: () -> Unit
) {
    var altura by remember { mutableStateOf(alturaActual) }
    var edad by remember { mutableStateOf(edadActual) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (altura.isNotBlank() && edad.isNotBlank()) {
                    onGuardar(altura, edad)
                }
                onDismiss()
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Introduce tu altura y edad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it },
                    label = { Text("Altura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad (años)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    )
}

@Composable
fun DialogAlturaEdad(
    onDismiss: () -> Unit,
    onGuardar: (altura: String, edad: String) -> Unit
) {
    var altura by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (altura.isNotBlank() && edad.isNotBlank()) {
                    onGuardar(altura, edad)
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Agregar altura y edad") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it },
                    label = { Text("Altura (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad (años)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    )
}
@Composable
fun TabMedidas(viewModel: MedidasViewModel) {
    val medidasDetalladasState by viewModel.medidasDetalladas.collectAsState()
    val context = LocalContext.current

    var hombros by remember { mutableStateOf(medidasDetalladasState.hombros) }
    var pecho by remember { mutableStateOf(medidasDetalladasState.pecho) }
    var cintura by remember { mutableStateOf(medidasDetalladasState.cintura) }
    var abdomen by remember { mutableStateOf(medidasDetalladasState.abdomen) }
    var cadera by remember { mutableStateOf(medidasDetalladasState.cadera) }
    var brazoIzquierdo by remember { mutableStateOf(medidasDetalladasState.brazoIzquierdo) }
    var brazoDerecho by remember { mutableStateOf(medidasDetalladasState.brazoDerecho) }
    var musloIzquierdo by remember { mutableStateOf(medidasDetalladasState.musloIzquierdo) }
    var musloDerecho by remember { mutableStateOf(medidasDetalladasState.musloDerecho) }
    var pantorrillaIzquierda by remember { mutableStateOf(medidasDetalladasState.pantorrillaIzquierda) }
    var pantorrillaDerecha by remember { mutableStateOf(medidasDetalladasState.pantorrillaDerecha) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        val fields = listOf(
            "Hombros" to (hombros to { value: String -> hombros = value }),
            "Pecho" to (pecho to { value: String -> pecho = value }),
            "Cintura" to (cintura to { value: String -> cintura = value }),
            "Abdomen" to (abdomen to { value: String -> abdomen = value }),
            "Cadera" to (cadera to { value: String -> cadera = value }),
            "Brazo izquierdo" to (brazoIzquierdo to { value: String -> brazoIzquierdo = value }),
            "Brazo derecho" to (brazoDerecho to { value: String -> brazoDerecho = value }),
            "Muslo izquierdo" to (musloIzquierdo to { value: String -> musloIzquierdo = value }),
            "Muslo derecho" to (musloDerecho to { value: String -> musloDerecho = value }),
            "Pantorrilla izquierda" to (pantorrillaIzquierda to { value: String -> pantorrillaIzquierda = value }),
            "Pantorrilla derecha" to (pantorrillaDerecha to { value: String -> pantorrillaDerecha = value })
        )

        items(fields) { (label, valueSetterPair) ->
            val (value, setter) = valueSetterPair
            OutlinedTextField(
                value = value,
                onValueChange = setter,
                label = { Text(label) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val nuevasMedidas = MedidasDetalladas(
                        hombros, pecho, cintura, abdomen, cadera, brazoIzquierdo, brazoDerecho,
                        musloIzquierdo, musloDerecho, pantorrillaIzquierda, pantorrillaDerecha
                    )
                    viewModel.guardarMedidasDetalladas(nuevasMedidas)
                    Toast.makeText(context, "Medidas guardadas", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar medidas")
            }
        }
    }
}

@Composable
fun TabAvanzado(viewModel: MedidasViewModel) {
    val datos by viewModel.datosAvanzados.collectAsState()
    val context = LocalContext.current

    var alturaCm by remember { mutableStateOf(datos.alturaCm) }
    var pesoKg by remember { mutableStateOf(datos.pesoKg) }
    var imc by remember { mutableStateOf(datos.imc) }
    var grasaPorcentaje by remember { mutableStateOf(datos.porcentajeGrasa) }

    val altura = viewModel.altura.collectAsState().value.toDoubleOrNull()
    LaunchedEffect(pesoKg, altura) {
        if (pesoKg.toDoubleOrNull() != null && altura != null && altura > 0) {
            imc = viewModel.calcularIMC(pesoKg.toDouble(), altura)
        }
    }
    fun calcularIMC(): String {
        val altura = alturaCm.toFloatOrNull()?.div(100f) ?: return ""
        val peso = pesoKg.toFloatOrNull() ?: return ""
        return String.format("%.2f", peso / (altura * altura))
    }

    fun calcularGrasa(): String {
        val imcNum = imc.toFloatOrNull() ?: return ""
        return String.format("%.1f", (1.2 * imcNum + 0.23 * 25 - 5.4)) // Edad fija de ejemplo
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            OutlinedTextField(
                value = alturaCm,
                onValueChange = { alturaCm = it },
                label = { Text("Altura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = pesoKg,
                onValueChange = {
                    pesoKg = it
                    imc = calcularIMC()
                    grasaPorcentaje = calcularGrasa()
                },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = imc,
                onValueChange = { imc = it },
                label = { Text("IMC (calculado)") },
                readOnly = true,
                singleLine = true
            )

        }
        item {
            OutlinedTextField(
                value = grasaPorcentaje,
                onValueChange = { grasaPorcentaje = it },
                label = { Text("% Grasa") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val nuevos = datos.copy(
                        alturaCm = alturaCm,
                        pesoKg = pesoKg,
                        imc = imc,
                        porcentajeGrasa = grasaPorcentaje
                    )
                    viewModel.guardarDatosAvanzados(nuevos)
                    Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}
@Composable
fun LineaPesoChartCanvas(
    historial: List<RegistroPeso>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Cyan,
    axisColor: Color = Color.Gray,
    textColor: Color = Color.White
) {
    if (historial.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay datos para el gráfico.", color = Color.Gray)
        }
        return
    }

    val density = LocalDensity.current
    val textPaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = density.run { 12.sp.toPx() }
            color = textColor.toArgb()
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    val xAxisTextPaint = remember {
        Paint().asFrameworkPaint().apply {
            isAntiAlias = true
            textSize = density.run { 10.sp.toPx() }
            color = textColor.toArgb()
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        val yAxisLabelHorizontalPadding = 8.dp.toPx()
        val paddingVertical = 20.dp.toPx()
        val paddingHorizontal = 10.dp.toPx()

        val maxPesoLabel = String.format("%.1f", historial.maxOfOrNull { it.peso } ?: 100.0)
        val yAxisLabelWidth = textPaint.measureText(maxPesoLabel) + yAxisLabelHorizontalPadding * 2
        val xAxisLabelHeight = 30.dp.toPx()

        val chartDrawableWidth = size.width - yAxisLabelWidth - paddingHorizontal * 2
        val chartDrawableHeight = size.height - (2 * paddingVertical) - xAxisLabelHeight
        val chartOriginX = yAxisLabelWidth + paddingHorizontal
        val chartOriginY = paddingVertical

        val minPeso = historial.minOfOrNull { it.peso }?.toFloat() ?: 0f
        var maxPeso = historial.maxOfOrNull { it.peso }?.toFloat() ?: 100f
        if (minPeso == maxPeso) maxPeso += 10f
        val pesoRange = (maxPeso - minPeso).takeIf { it > 0 } ?: 1f

        val numYLabels = 5

        // Dibujar ejes
        drawLine(
            color = axisColor,
            start = Offset(chartOriginX, chartOriginY),
            end = Offset(chartOriginX, chartOriginY + chartDrawableHeight),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = axisColor,
            start = Offset(chartOriginX, chartOriginY + chartDrawableHeight),
            end = Offset(chartOriginX + chartDrawableWidth, chartOriginY + chartDrawableHeight),
            strokeWidth = 1.dp.toPx()
        )

        // Etiquetas y cuadrícula del eje Y
        for (i in 0..numYLabels) {
            val yValue = minPeso + (pesoRange / numYLabels) * i
            val yPosOnCanvas = chartOriginY + chartDrawableHeight - ((yValue - minPeso) / pesoRange * chartDrawableHeight)

            drawLine(
                color = axisColor.copy(alpha = 0.3f),
                start = Offset(chartOriginX, yPosOnCanvas),
                end = Offset(chartOriginX + chartDrawableWidth, yPosOnCanvas),
                strokeWidth = 0.5.dp.toPx()
            )
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", yValue),
                chartOriginX - yAxisLabelWidth / 2, // Centered in the allocated space for Y labels
                yPosOnCanvas + textPaint.textSize / 3,
                textPaint
            )
        }

        // Etiquetas y cuadrícula del eje X
        if (historial.isNotEmpty()) {
            val numXPointsToLabel = minOf(historial.size, 5)
            val step = (historial.size -1) / (numXPointsToLabel -1).coerceAtLeast(1)

            for (i in 0 until numXPointsToLabel) {
                val dataIndex = (i * step).coerceAtMost(historial.size -1)
                val registro = historial[dataIndex]
                val xPosOnCanvas = chartOriginX + (dataIndex.toFloat() / (historial.size - 1).coerceAtLeast(1).toFloat() * chartDrawableWidth)
                val labelText = registro.fecha.parseToLocalDate()?.format(formatoFechaSalidaAxis) ?: ""
                drawLine(
                    color = axisColor.copy(alpha = 0.3f),
                    start = Offset(xPosOnCanvas, chartOriginY),
                    end = Offset(xPosOnCanvas, chartOriginY + chartDrawableHeight),
                    strokeWidth = 0.5.dp.toPx()
                )
                drawContext.canvas.nativeCanvas.drawText(
                    labelText,
                    xPosOnCanvas,
                    chartOriginY + chartDrawableHeight + xAxisLabelHeight / 1.5f,
                    xAxisTextPaint
                )
            }
        }

        // Dibujar la línea del gráfico
        if (historial.size > 1) {
            val path = Path()
            historial.forEachIndexed { index, registro ->
                val x = chartOriginX + (index.toFloat() / (historial.size - 1).toFloat() * chartDrawableWidth)
                val y = chartOriginY + chartDrawableHeight - ((registro.peso.toFloat() - minPeso) / pesoRange * chartDrawableHeight)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(color = lineColor, radius = 3.dp.toPx(), center = Offset(x, y))
            }
            drawPath(path = path, color = lineColor, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
        } else if (historial.size == 1) {
            val registro = historial.first()
            val x = chartOriginX + chartDrawableWidth / 2
            val y = chartOriginY + chartDrawableHeight - ((registro.peso.toFloat() - minPeso) / pesoRange * chartDrawableHeight)
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y))
        }
    }
}

val formatoFechaEntrada: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy") // ViewModel also uses this
val formatoFechaSalidaAxis: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM")

fun String.parseToLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, formatoFechaEntrada)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun Color.toArgb(): Int {
    return android.graphics.Color.argb(
        (this.alpha * 255).toInt(),
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt()
    )
}

@Composable
fun DialogAgregarPeso(
    onDismiss: () -> Unit,
    onGuardar: (peso: Double, meta: Double, imc: Double?, grasa: Double?) -> Unit,
    ultimaMeta: Double
) {
    var peso by remember { mutableStateOf("") }
    var metaInput by remember { mutableStateOf("") } // Renamed to avoid conflict
    var imc by remember { mutableStateOf("") }
    var grasa by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    LaunchedEffect(ultimaMeta) {
        if (metaInput.isBlank() && ultimaMeta > 0.0) {
            metaInput = ultimaMeta.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val pesoDouble = peso.toDoubleOrNull()
                if (pesoDouble != null && pesoDouble > 0) {
                    onGuardar(
                        pesoDouble,
                        metaInput.toDoubleOrNull() ?: ultimaMeta, // Use ViewModel's ultimaMeta as fallback
                        imc.toDoubleOrNull(),
                        grasa.toDoubleOrNull()
                    )
                } else {
                    Toast.makeText(contexto, "Por favor, introduce un peso válido.", Toast.LENGTH_SHORT).show()
                }
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Agregar peso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = peso, onValueChange = { peso = it }, label = { Text("Peso actual (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = metaInput, onValueChange = { metaInput = it }, label = { Text("Meta (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = imc, onValueChange = { imc = it }, label = { Text("IMC (opcional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = grasa, onValueChange = { grasa = it }, label = { Text("% Grasa (opcional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }
        }
    )
}
