package com.example.version00.ui.components

import androidx.compose.ui.graphics.Color

/**
 * Convierte un valor de activación (0 a 100) en un color que representa su intensidad.
 * El color es rojo para 100% y se va degradando hacia blanco a medida que la activación disminuye.
 *
 * @param activation Valor de activación muscular en porcentaje.
 * @return Color correspondiente a la intensidad.
 */
fun getColorFromActivation(activation: Float): Color {
    val clamped = activation.coerceIn(0f, 100f) // Asegura que el valor esté entre 0 y 100
    val intensity = clamped / 100f
    return Color(red = 1f, green = 1f - intensity, blue = 1f - intensity) // Rojo puro para 100%, blanco para 0%
}

/**
 * Mapeo entre nombres de músculos (según la API o base de datos) y sus IDs en el SVG.
 * Usado para aplicar color o animaciones en imágenes vectoriales del cuerpo.
 */
val apiToSvgId = mapOf(
    "Esternocleidomastoideo derecho" to "esternocleidomastoideo_derecho",
    "Esternocleidomastoideo izquierdo" to "esternocleidomastoideo_izquierdo",
    "Trapecio superior derecho" to "Trapecio_superior_derecho",
    "Trapecio superior izquierdo" to "trapecio_superior_izquierdo",
    "Deltoides anterior derecho" to "hombro_derecho",
    "Deltoides anterior izquierdo" to "hombro_izquierdo",
    "Pectoral mayor derecho" to "pectoral_derecho",
    "Pectoral mayor izquierdo" to "pectoral_izquierdo",
    "Bíceps derecho" to "biceps_derecho",
    "Bíceps izquierdo" to "biceps_izquierdo",
    "Antebrazo derecho" to "antebrazo_inferior_derecho",
    "Antebrazo izquierdo" to "antebrazo_inferior_izquierdo",
    "Recto abdominal" to "abdominal_4",
    "Oblicuo externo izquierdo" to "oblicuo_izquierdo",
    "Oblicuo externo derecho" to "oblicuo_derecho",
    "Cuádriceps derecho" to "recto_femoral_derecho",
    "Cuádriceps izquierdo" to "recto_femoral_izquierdo",
    "Vasto lateral derecho" to "vasto_lateral_derecho",
    "Vasto lateral izquierdo" to "vasto_lateral_izquierdo",
    "Aductor largo izquierdo" to "aductor_largo_izquierdo",
    "Aductor largo derecho" to "aductor_largo_derecho",
    "Sartorio izquierdo" to "sartorio_izquierdo",
    "Sartorio derecho" to "sartorio_derecho",
    "Gastrocnemio medial izquierdo" to "gastrocnemio_medial_izquierdo",
    "Gastrocnemio medial derecho" to "gastrocnemio_medial_derecho",
    "Gastrocnemio lateral izquierdo" to "gastrocnemio_lateral_izquierdo",
    "Gastrocnemio lateral derecho" to "gastrocnemio_lateral_derecho",
    "Sóleo izquierdo" to "soleo_izquierdo",
    "Sóleo derecho" to "soleo_derecho"
)
