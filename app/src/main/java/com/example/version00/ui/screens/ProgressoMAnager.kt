package com.example.version00.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object ProgresoManager {

    private const val PREF_NAME = "ProgresoEntrenamiento"
    private const val KEY_PREFIX = "dia_" // ✅ Con guion bajo final

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun marcarDiaComoCompletado(context: Context, faseIndex: Int, semanaIndex: Int, diaIndex: Int) {
        val key = "$KEY_PREFIX${faseIndex}_${semanaIndex}_$diaIndex" // ✅ Sin doble guion bajo
        Log.d("PROGRESO", "✅ Guardando día completado: $key")
        getPrefs(context).edit().putBoolean(key, true).apply()
    }

    fun esDiaCompletado(context: Context, faseIndex: Int, semanaIndex: Int, diaIndex: Int): Boolean {
        val key = "$KEY_PREFIX${faseIndex}_${semanaIndex}_$diaIndex"
        return getPrefs(context).getBoolean(key, false)
    }

    fun contarDiasCompletados(
        context: Context,
        faseIndex: Int,
        semanas: Int,
        diasPorSemana: List<Int>
    ): Pair<Int, List<Int>> {
        val prefs = getPrefs(context)
        var totalCompletados = 0
        val completadosPorSemana = mutableListOf<Int>()

        for (semanaIndex in 0 until semanas) {
            var completadosSemana = 0
            for (diaIndex in 0 until diasPorSemana[semanaIndex]) {
                val key = "$KEY_PREFIX${faseIndex}_${semanaIndex}_$diaIndex"
                val isDone = prefs.getBoolean(key, false)
                Log.d("PROGRESO", "🔍 Leyendo $key = $isDone")
                if (isDone) {
                    completadosSemana++
                    totalCompletados++
                }
            }
            completadosPorSemana.add(completadosSemana)
        }

        return totalCompletados to completadosPorSemana
    }

    fun resetearProgreso(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
