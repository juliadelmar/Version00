package com.example.version00.ui.screens

import android.content.Context
import com.example.version00.data.ResistenciaBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import java.util.*

data class Ejercicio(
    val id: Int,
    val nombre: String,
    val url: String,
    val activacion: Map<String, Map<String, Int>>,
    val series: Int,
    val repeticiones: Int,
    val rir: Int,

    ) {
}

data class DiaEntrenamiento(
    val nombre: String,
    val ejercicios: List<Ejercicio>
)

data class SemanaEntrenamiento(
    val nombre: String,
    val dias: List<DiaEntrenamiento>
)

data class FaseEntrenamiento(
    val nombre: String,
    val semanas: List<SemanaEntrenamiento>
)

data class RutinaPredefinida(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val fases: List<FaseEntrenamiento>
)

object RutinasPredefinidasProvider {

    val rutinaCompleta = RutinaPredefinida(
        nombre = "Rutina Base 12 semanas",
        fases = listOf(
            FaseEntrenamiento("1: Resistencia", ResistenciaBuilder.build()),

        )
    )

    fun guardarEnSharedPreferences(context: Context) {
        val json = Gson().toJson(rutinaCompleta)
        val prefs = context.getSharedPreferences("rutinas", Context.MODE_PRIVATE)
        prefs.edit().putString("rutina_predefinida", json).apply()
    }

    fun guardarEnFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("usuarios/$uid/rutinasPredefinidas/${rutinaCompleta.id}")
            .setValue(rutinaCompleta)
    }
}
