package com.example.version00.ui.data

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

/**
 * Composable que dibuja un mapa visual de activación muscular
 * sobre una figura SVG superpuesta a una imagen de fondo.
 *
 * @param modifier Modificador externo (padding, tamaño, etc.).
 * @param backgroundImageRes Recurso de imagen de fondo (ej. silueta del cuerpo humano).
 * @param svgImageRes Recurso XML con el vector drawable (SVG parseable).
 * @param activacionPorId Mapa con valores de activación muscular por ID (0 a 100).
 * @param svgViewBoxWidth Ancho declarado del viewBox SVG original.
 * @param svgViewBoxHeight Alto declarado del viewBox SVG original.
 */
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

    // Parseamos el SVG y recordamos los paths
    val svgParts = remember(svgImageRes, context) {
        if (svgImageRes != 0) {
            val parts = parseVectorDrawableFile(context, svgImageRes)
            parts.forEach { Log.d("SVG_DEBUG", "ID disponible: ${it.id}") }
            parts
        } else emptyList()
    }

    // Contenedor que ajusta escala y alineación
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {

        // Imagen de fondo: silueta humana
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
                    Log.e("SvgHighlighter", "svgViewBoxWidth or svgViewBoxHeight is invalid.")
                    return@Canvas
                }

                val scaleX = canvasWidth / svgViewBoxWidth
                val scaleY = canvasHeight / svgViewBoxHeight
                val finalScale = minOf(scaleX, scaleY)

                val scaledSvgWidth = svgViewBoxWidth * finalScale
                val scaledSvgHeight = svgViewBoxHeight * finalScale
                val offsetX = (canvasWidth - scaledSvgWidth) / 2f
                val offsetY = (canvasHeight - scaledSvgHeight) / 2f
                val extraShiftRight = 25f
                val extraShiftDown = 18f

                // Dibujo SVG escalado y alineado
                translate(left = offsetX + extraShiftRight, top = offsetY + extraShiftDown) {
                    scale(scale = finalScale) {
                        svgParts.forEach { part ->
                            val activacion = activacionPorId[part.id] ?: 0
                            val color = when {
                                activacion >= 50 -> Color.Red.copy(alpha = 0.7f)        // Activación alta
                                activacion >= 33 -> Color.Green.copy(alpha = 0.7f)      // Activación media
                                activacion > 0   -> Color.LightGray.copy(alpha = 0.7f)  // Activación baja
                                else             -> Color.Gray.copy(alpha = 0.3f)       // No activo
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
        } else if (svgImageRes != 0) {
            Log.w("SvgHighlighter", "No se encontraron partes SVG para $svgImageRes.")
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SvgMuscleHighlighter2(
    modifier: Modifier = Modifier,
    backgroundImageRes: Int,
    svgImageRes: Int,
    activacionPorId: Map<String, Int>,
    svgViewBoxWidth: Float,
    svgViewBoxHeight: Float,
) {
    val context = LocalContext.current

    // Parseamos el SVG y recordamos los paths
    val svgParts = remember(svgImageRes, context) {
        if (svgImageRes != 0) {
            val parts = parseVectorDrawableFile(context, svgImageRes)
            parts.forEach { Log.d("SVG_DEBUG", "ID disponible: ${it.id}") }
            parts
        } else emptyList()
    }

    // Contenedor que ajusta escala y alineación
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {

        // Imagen de fondo: silueta humana
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
                    Log.e("SvgHighlighter", "svgViewBoxWidth or svgViewBoxHeight is invalid.")
                    return@Canvas
                }

                val scaleX = canvasWidth / svgViewBoxWidth
                val scaleY = canvasHeight / svgViewBoxHeight

                val scaleFactor = 0.85f  // Ajusta este valor para hacerlo más pequeño (0.5 es 50%, 1.0 es tamaño original)
                val finalScale = minOf(scaleX, scaleY) * scaleFactor

                val scaledSvgWidth = svgViewBoxWidth * finalScale
                val scaledSvgHeight = svgViewBoxHeight * finalScale
                val offsetX = (canvasWidth - scaledSvgWidth) / 2f
                val offsetY = (canvasHeight - scaledSvgHeight) / 2f

                val extraShiftRight = -80f
                val extraShiftDown = -85f

                // Dibujo SVG escalado y alineado
                translate(left = offsetX + extraShiftRight, top = offsetY + extraShiftDown) {
                    scale(scale = finalScale) {
                        svgParts.forEach { part ->
                            val activacion = activacionPorId[part.id] ?: 0
                            val color = when {
                                activacion >= 50 -> Color.Red.copy(alpha = 0.7f)        // Activación alta
                                activacion >= 33 -> Color.Green.copy(alpha = 0.7f)      // Activación media
                                activacion > 0   -> Color.LightGray.copy(alpha = 0.7f)  // Activación baja
                                else             -> Color.Gray.copy(alpha = 0.3f)       // No activo
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

        } else if (svgImageRes != 0) {
            Log.w("SvgHighlighter", "No se encontraron partes SVG para $svgImageRes.")
        }
    }
}

