package com.example.version00.ui.model

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
data class EjercicioGuardado(
    val id: Int = 0,
    val nombre: String = "",
    val urlGif: String = "",
    val series: Int = 0,
    val descanso: Int = 0,
    val reps: List<String> = emptyList(),
    val pesos: List<String> = emptyList(),
    val notas: String = ""
)


data class SerieEjercicio(
    val repeticiones: Int = 0,
    val peso: Float = 0f
)
