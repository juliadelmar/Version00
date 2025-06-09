package com.example.version00.ui.model
import android.content.Context

data class Reto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val objetivo: Int,
    val gifUrl: String = ""
)


object RetoPrefs {

    private const val PREF_NAME = "RetosPrefs"

    fun getProgreso(context: Context, retoId: Int): Int {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt("reto_$retoId", 0)
    }

    fun setProgreso(context: Context, retoId: Int, progreso: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt("reto_$retoId", progreso).apply()
    }

    fun resetProgreso(context: Context, retoId: Int) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove("reto_$retoId").apply()
    }

    fun resetearTodos(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
