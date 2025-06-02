package com.example.version00.ui.data

import com.example.version00.ui.viewmodel.FaseCiclo

/**
 * Contiene los valores sugeridos para un ejercicio:
 * - peso en kilogramos,
 * - repeticiones (reps),
 * - repeticiones en reserva (RIR).
 */
data class SugerenciaCarga(
    val pesoSugerido: Float,
    val repsSugeridas: Int,
    val rirSugerido: Int
)

/**
 * Versión más directa del modelo de sugerencia (idéntica funcionalidad).
 */
data class Sugerencia(
    val pesoSugerido: Float,
    val repsSugeridas: Int,
    val rirSugerido: Int
)

/**
 * Objeto que calcula automáticamente la carga recomendada (peso, reps, RIR)
 * según la fase del ciclo menstrual y el historial del ejercicio.
 *
 * Utiliza lógica específica para cada fase:
 * - MENSTRUAL: reduce peso y repeticiones, más RIR.
 * - FOLICULAR: peso normal, RIR bajo.
 * - OVULATORIA: aumento leve de peso y reps, sin RIR.
 * - LÚTEA: ligera reducción, RIR moderado.
 */
object SugeridorDeCarga {
    /**
     * Genera una [Sugerencia] personalizada.
     *
     * @param fase Fase actual del ciclo menstrual.
     * @param pesoAnterior Peso usado en el ejercicio anterior.
     * @param repsAnterior Repeticiones realizadas anteriormente.
     *
     * @return [Sugerencia] ajustada a la fase del ciclo.
     */
    fun generarSugerencia(
        fase: FaseCiclo,
        pesoAnterior: Float,
        repsAnterior: Int
    ): Sugerencia {
        return when (fase) {
            FaseCiclo.MENSTRUAL -> Sugerencia(
                pesoSugerido = (pesoAnterior * 0.85f).coerceAtLeast(1f),
                repsSugeridas = (repsAnterior - 1).coerceAtLeast(6),
                rirSugerido = 3
            )
            FaseCiclo.FOLICULAR -> Sugerencia(
                pesoSugerido = (pesoAnterior * 1.00f).coerceAtLeast(1f),
                repsSugeridas = repsAnterior,
                rirSugerido = 1
            )
            FaseCiclo.OVULATORIA -> Sugerencia(
                pesoSugerido = (pesoAnterior * 1.05f).coerceAtLeast(1f),
                repsSugeridas = (repsAnterior + 1).coerceAtMost(15),
                rirSugerido = 0
            )
            FaseCiclo.LUTEA -> Sugerencia(
                pesoSugerido = (pesoAnterior * 0.90f).coerceAtLeast(1f),
                repsSugeridas = (repsAnterior - 1).coerceAtLeast(6),
                rirSugerido = 2
            )
            else -> Sugerencia(
                pesoSugerido = pesoAnterior.coerceAtLeast(1f),
                repsSugeridas = repsAnterior,
                rirSugerido = 1
            )
        }
    }
}
