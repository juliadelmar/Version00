package com.example.version00.ui.data
import com.example.version00.ui.viewmodel.FaseCiclo

data class SugerenciaCarga(
    val pesoSugerido: Float,
    val repsSugeridas: Int,
    val rirSugerido: Int
)



data class Sugerencia(val pesoSugerido: Float, val repsSugeridas: Int, val rirSugerido: Int)

object SugeridorDeCarga {
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
