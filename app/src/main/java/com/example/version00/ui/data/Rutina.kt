package com.example.version00.ui.data

/**
 * Modelo de datos que representa una rutina de entrenamiento.
 *
 * @param id Identificador único de la rutina (puede ser usado para orden o comparación).
 * @param nombre Nombre descriptivo de la rutina (ej. "Fuerza - Día 1").
 */
data class Rutina(
    val id: Int,
    val nombre: String,
)
