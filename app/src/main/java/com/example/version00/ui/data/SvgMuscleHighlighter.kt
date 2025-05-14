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

// Asumimos que tienes una función parseVectorDrawableFile similar a esta:
// fun parseVectorDrawableFile(context: Context, drawableResId: Int): List<SvgPart> { /* ... tu lógica de parseo ... */ }
// Esta función es CRÍTICA. Debe generar objetos Path que representen las áreas a rellenar.

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SvgMuscleHighlighter(
    modifier: Modifier = Modifier,
    backgroundImageRes: Int,
    svgImageRes: Int,
    highlightedMuscleIds: Set<String>,
    svgViewBoxWidth: Float,
    svgViewBoxHeight: Float,
    // Añade la función de parseo como parámetro si es más flexible, o mantenla como está.
    // Por ahora, asumiré que existe una función global o importada `parseVectorDrawableFile`.
    // Aquí necesitaríamos la implementación de `parseVectorDrawableFile` para estar seguros.
    // Ejemplo: val parsedParts: (Context, Int) -> List<SvgPart> = ::parseVectorDrawableFile
) {
    val context = LocalContext.current
    // Esta es una función hipotética que debes haber implementado.
    // Es crucial que esta función devuelva Path objetos que sean áreas cerradas
    // si quieres rellenarlos.
    val svgParts = remember(svgImageRes, context) {
        // Aquí se llama a tu función de parseo. Asegúrate de que esta función
        // crea objetos Path que pueden ser rellenados (es decir, formas cerradas).
        // Si parseVectorDrawableFile no está definida globalmente, deberás pasarla
        // o implementarla de forma que sea accesible aquí.
        // Por ejemplo: com.example.version00.ui.data.parseVectorDrawableFile(context, svgImageRes)
        // o simplemente parseVectorDrawableFile(context, svgImageRes) si está en el mismo paquete/importada.
        // *** Reemplaza esto con tu llamada real a parseVectorDrawableFile ***
        // parseVectorDrawableFile(context, svgImageRes)
        // Ejemplo para que compile (DEBES REEMPLAZAR ESTO CON TU LÓGICA REAL):
        if (svgImageRes != 0) { // Comprobación simple para evitar crash si es 0
            com.example.version00.ui.data.parseVectorDrawableFile(context, svgImageRes) // Asumiendo que está en este paquete
        } else {
            emptyList()
        }
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = backgroundImageRes),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
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

                translate(left = offsetX, top = offsetY) {
                    scale(scale = finalScale) {
                        svgParts.forEach { part ->
                            val color = if (part.id in highlightedMuscleIds) {
                                highlightedMuscleColor
                            } else {
                                defaultMuscleColor
                            }
                            drawPath(
                                path = part.composePath,
                                color = color,
                                style = Fill // ESTA ES LA CLAVE para asegurar el relleno.
                                // Aunque `Fill` es el predeterminado para la sobrecarga
                                // de `drawPath` que toma un `color`, ser explícito
                                // no hace daño y clarifica la intención.
                            )
                        }
                    }
                }
            }
        } else {
            // Solo loguea si svgImageRes no era 0, para evitar ruido si se pasó 0 intencionalmente.
            if (svgImageRes != 0) {
                Log.w("SvgHighlighter", "No SVG parts to draw for resource ID $svgImageRes. Check parsing or if resource is valid.")
            }
        }
    }
}
