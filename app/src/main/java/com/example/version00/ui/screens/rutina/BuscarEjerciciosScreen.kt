package com.example.version00.ui.screens.rutina

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.version00.R

// Definiciones de Path para los músculos (simplificadas para el ejemplo)
// Estas coordenadas y formas son solo ilustrativas. Necesitarás ajustarlas a tu imagen.
val pectoralIzquierdoPath = Path().apply {
    // Coordenadas para el pectoral izquierdo
    moveTo(150f, 120f)
    lineTo(200f, 120f)
    lineTo(200f, 180f)
    lineTo(150f, 180f)
    close()
}
val pectoralDerechoPath = Path().apply {
    // Coordenadas para el pectoral derecho
    moveTo(280f, 120f)
    lineTo(330f, 120f)
    lineTo(330f, 180f)
    lineTo(280f, 180f)
    close()
}
// ... otros paths para otros músculos ...

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElegirEjercicioScreen(
    onMusculoSeleccionado: (String) -> Unit,
    navController: NavHostController,
) {
    var vistaFrontal by remember { mutableStateOf(true) }
    var musculoHover by remember { mutableStateOf<String?>(null) } // Para feedback visual al pasar el mouse/dedo
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Mapa de músculos con sus paths
    val musculosPathsFrontal = mapOf(
        "pectoral_izquierdo" to pectoralIzquierdoPath,
        "pectoral_derecho" to pectoralDerechoPath
        // Añade más músculos aquí
    )
    // val musculosPathsTrasero = mapOf(...) // Para la vista trasera

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo oscuro en lugar de azul
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.elige_un_musculo), color = Color.White, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { vistaFrontal = !vistaFrontal }) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.girar_cuerpo), tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(stringResource(R.string.buscar_musculo), color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedBorderColor = Color(0xFFB39DDB),
                unfocusedBorderColor = Color(0xFF9575CD),
                focusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                // Aquí podrías filtrar una lista de músculos si es necesario,
                // o si el campo de búsqueda es para ejercicios, pasar el searchQuery
                // al ViewModel correspondiente.
                println("Búsqueda: $searchQuery")
            })
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f) // Ocupa el espacio restante
                .fillMaxWidth()
                .background(Color.DarkGray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                }
        ) {
            Image(
                painter = painterResource(id = if (vistaFrontal) R.drawable.cuerpo_frenteo else R.drawable.cuerpo_espalda
                ), // Necesitas cuerpo_espalda.png
                contentDescription = stringResource(R.string.cuerpo_humano),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )


            Canvas(modifier = Modifier
                .fillMaxSize()
                .clickable {

                    println("Canvas clickeado. Implementar hit-testing para músculos.")
                    onMusculoSeleccionado("pectoral_izquierdo") // Ejemplo
                }
            ) {
                val currentMusclePaths = if (vistaFrontal) musculosPathsFrontal else emptyMap() // musculosPathsTrasero
                currentMusclePaths.forEach { (nombreMusculo, path) ->
                    // Dibuja un contorno o relleno si el músculo está "seleccionado" o "hover"
                    // if (nombreMusculo == musculoHover || nombreMusculo == "ID_DEL_MUSCULO_YA_SELECCIONADO") {
                    drawPath(
                        path = path,
                        color = Color.Magenta.copy(alpha = 0.3f) // Color para resaltar (ejemplo)
                    )
                    drawPath(
                        path = path,
                        color = Color.Magenta, // Color para el borde
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // }
                }
                // Información de ayuda
                // drawContext.canvas.nativeCanvas.drawText("TODO: Implementar clic en músculos", center.x, size.height - 20.dp.toPx(), Paint().apply { color = android.graphics.Color.RED; textSize=16.sp.toPx() })
            }
            Text(
                "NOTA: La selección de músculos en la imagen es una demo visual.\nLa lógica de clic precisa requiere implementación avanzada.",
                color = Color.Yellow,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp)
            )
        }
    }
}