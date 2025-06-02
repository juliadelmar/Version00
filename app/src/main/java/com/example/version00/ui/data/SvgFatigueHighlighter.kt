package com.example.version00.ui.data

import androidx.compose.runtime.Composable
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SvgFatigueHighlighter(
    modifier: Modifier = Modifier,
    backgroundImageRes: Int,
    svgImageRes: Int,
    fatigaPorId: Map<String, Double>,
    svgViewBoxWidth: Float,
    svgViewBoxHeight: Float,
) {
    val context = LocalContext.current
    val svgParts = remember(svgImageRes, context) {
        if (svgImageRes != 0) {
            val parts = parseVectorDrawableFile(context, svgImageRes)
            parts
        } else emptyList()
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = backgroundImageRes),
            contentDescription = "Fondo",
            modifier = Modifier
                .fillMaxSize(0.83f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Fit
        )

        if (svgParts.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                if (svgViewBoxWidth <= 0f || svgViewBoxHeight <= 0f) return@Canvas

                val scaleX = canvasWidth / svgViewBoxWidth
                val scaleY = canvasHeight / svgViewBoxHeight
                val finalScale = minOf(scaleX, scaleY)

                val scaledSvgWidth = svgViewBoxWidth * finalScale
                val scaledSvgHeight = svgViewBoxHeight * finalScale
                val offsetX = (canvasWidth - scaledSvgWidth) / 2f
                val offsetY = (canvasHeight - scaledSvgHeight) / 2f
                val extraShiftRight = 56f
                val extraShiftDown = 12f // 18 - 30dp = -12f para subir el SVG 0.8 cm

                translate(left = offsetX + extraShiftRight, top = offsetY + extraShiftDown) {
                    scale(scale = finalScale) {
                        svgParts.forEach { part ->
                            val fatiga = fatigaPorId[part.id] ?: 0.0
                            val color = when {
                                fatiga >= 4.0 -> Color.Red.copy(alpha = 0.7f) // Muy fatigado
                                fatiga >= 2.5 -> Color.Yellow.copy(alpha = 0.7f) // En recuperación
                                fatiga >= 1.0 -> Color.Green.copy(alpha = 0.6f) // Recuperado
                                fatiga > 0.0 -> Color.Cyan.copy(alpha = 0.5f) // Débil pero funcional
                                else -> Color.Gray.copy(alpha = 0.3f) // Sin datos
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
        }
    }
}
