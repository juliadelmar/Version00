package com.example.version00.ui.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Modelo que representa un ejercicio físico completo, con todos sus datos técnicos.
 *
 * @param _id Identificador único del ejercicio.
 * @param nombre Nombre del ejercicio (ej. "Sentadilla frontal").
 * @param nivelDificultad Dificultad estimada (ej. "Intermedio").
 * @param urlGif Enlace o recurso al GIF de demostración.
 * @param equipo Tipo de equipo necesario (ej. "Barra", "Mancuerna").
 * @param tipoDeEjercicio Tipo (ej. "Fuerza", "Cardio", etc).
 * @param musculosTrabajados Grupos musculares implicados.
 * @param porcentajeDeActivacion Mapa detallado con activación por músculo y lado (ej. "derecho", "izquierdo").
 * @param instrucciones Lista de pasos para la ejecución correcta.
 * @param erroresComunes Lista de errores comunes que debe evitar el usuario.
 */
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

/**
 * Submodelo que representa los músculos principales y secundarios de un ejercicio.
 *
 * @param principales Lista de músculos principales (ej. "Cuádriceps").
 * @param secundarios Lista de músculos de apoyo o implicación secundaria.
 */
data class MusculosTrabajados(
    val principales: List<String>,
    val secundarios: List<String>
)

/**
 * Representa un ejercicio que ha sido guardado en una rutina personalizada por el usuario.
 * Se usa en Firebase. Se marca con @IgnoreExtraProperties para ignorar campos adicionales al leer de la DB.
 *
 * @param id ID local o referencial.
 * @param nombre Nombre del ejercicio.
 * @param urlGif Enlace al gif (puede ser visualizado en la rutina).
 * @param series Número total de series asignadas.
 * @param descanso Tiempo de descanso en segundos entre series.
 * @param porcentajeDeActivacion Mapa muscular (heredado del Ejercicio original).
 * @param reps Lista de repeticiones por serie (strings para facilitar manipulación desde UI).
 * @param pesos Lista de pesos usados por serie.
 * @param repsRecamara Lista de repeticiones en recámara (RIR) por serie.
 * @param notas Notas personalizadas del usuario.
 */
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

/**
 * Modelo para representar una única serie dentro de un ejercicio.
 * Se puede usar para estadísticas o edición rápida de ejercicios.
 *
 * @param repeticiones Número de repeticiones realizadas.
 * @param peso Peso levantado en esa serie.
 */
data class SerieEjercicio(
    val repeticiones: Int = 0,
    val peso: Float = 0f
)

/**
 * Estadísticas generales calculadas después de una rutina.
 * Usado en pantallas de resumen, fatiga y seguimiento.
 *
 * @param series Total de series realizadas.
 * @param reps Total de repeticiones realizadas.
 * @param ejercicios Número total de ejercicios ejecutados.
 * @param carga Suma total del volumen (peso × repeticiones).
 * @param calorias Estimación de calorías quemadas.
 */
data class EstadisticasEntrenamiento(
    var series: Int = 0,
    var reps: Int = 0,
    var ejercicios: Int = 0,
    var carga: Float = 0f,
    var calorias: Int = 0
)
