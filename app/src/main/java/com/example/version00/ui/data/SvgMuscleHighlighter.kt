package com.example.version00.ui.data // O el paquete donde quieras que esté

// Importa Fill explícitamente si quieres ser muy claro
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource


// Estado para los colores de las partes del SVG
val defaultMuscleColor = Color.Gray.copy(alpha = 0.5f)
val highlightedMuscleColor = Color.Red.copy(alpha = 0.7f)

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SvgMuscleHighlighter(
    modifier: Modifier = Modifier,
    backgroundImageRes: Int,
    svgImageRes: Int,
    activacionPorId: Map<String, Int>,
    svgViewBoxWidth: Float,
    svgViewBoxHeight: Float,
) {
    val context = LocalContext.current
    val svgParts = remember(svgImageRes, context) {
        if (svgImageRes != 0) {
            val parts = parseVectorDrawableFile(context, svgImageRes)
            parts.forEach { Log.d("SVG_DEBUG", "ID disponible: ${it.id}") }
            parts
        } else emptyList()
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = backgroundImageRes),
            contentDescription = "Fondo",
            modifier = Modifier
                .fillMaxSize(0.85f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Fit
        )

        if (svgParts.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                if (svgViewBoxWidth <= 0f || svgViewBoxHeight <= 0f) {
                    Log.e("SvgHighlighter", "svgViewBoxWidth or svgViewBoxHeight is zero or negative. Cannot scale.")
                    return@Canvas
                }

                val scaleX = canvasWidth / svgViewBoxWidth
                val scaleY = canvasHeight / svgViewBoxHeight
                val finalScale = minOf(scaleX, scaleY)

                val scaledSvgWidth = svgViewBoxWidth * finalScale
                val scaledSvgHeight = svgViewBoxHeight * finalScale
                val offsetX = (canvasWidth - scaledSvgWidth) / 2f
                val offsetY = (canvasHeight - scaledSvgHeight) / 2f
                val extraShiftRight = 62f
                val extraShiftDown = 18f

                translate(left = offsetX + extraShiftRight, top = offsetY + extraShiftDown) {
                    scale(scale = finalScale) {
                        svgParts.forEach { part ->
                            val activacion = activacionPorId[part.id] ?: 0
                            val color = when {
                                activacion >= 50 -> Color.Red.copy(alpha = 0.7f)
                                activacion >= 33 -> Color.Green.copy(alpha = 0.7f)
                                activacion > 0 -> Color.LightGray.copy(alpha = 0.7f)
                                else -> Color.Gray.copy(alpha = 0.3f)
                            }
                            drawPath(
                                path = part.composePath,
                                color = color,
                                style = Fill
                            )
                        }
                    }
                }
            }
        } else {
            if (svgImageRes != 0) {
                Log.w("SvgHighlighter", "No SVG parts to draw for resource ID $svgImageRes.")
            }
        }
    }
}
