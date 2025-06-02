package com.example.version00.ui.model

import com.google.firebase.database.IgnoreExtraProperties

data class Ejercicio(
    val _id: Int,
    val nombre: String,
    val nivelDificultad: String,
    val urlGif: String,
    val equipo: String,
    val tipoDeEjercicio: String,
    val musculosTrabajados: MusculosTrabajados,
    val porcentajeDeActivacion: Map<String, Map<String, Int>>,
    val instrucciones: List<String>,
    val erroresComunes: List<String>
)

data class MusculosTrabajados(
    val principales: List<String>,
    val secundarios: List<String>
)
@IgnoreExtraProperties
data class EjercicioGuardado(
    val id: Int = 0,
    val nombre: String = "",
    val urlGif: String = "",
    val series: Int = 0,
    val descanso: Int = 0,
    var porcentajeDeActivacion: Map<String, Map<String, Int>> = emptyMap(),
    var reps: MutableList<String> = mutableListOf(),
    var pesos: MutableList<String> = mutableListOf(),
    var repsRecamara: MutableList<String> = mutableListOf(),
    val notas: String = ""
)


data class SerieEjercicio(
    val repeticiones: Int = 0,
    val peso: Float = 0f
)
data class EstadisticasEntrenamiento(
    var series: Int = 0,
    var reps: Int = 0,
    var ejercicios: Int = 0,
    var carga: Float = 0f,
    var calorias: Int = 0
)
