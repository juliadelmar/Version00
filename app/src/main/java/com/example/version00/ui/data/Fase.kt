package com.example.version00.ui.data

/**
 * Modelo de datos que representa una fase de entrenamiento.
 *
 * @param titulo Nombre de la fase (ej. "Fase de fuerza").
 * @param semanas Rango de semanas o duración (ej. "Semana 1 a 4").
 */
data class Fase(
    val titulo: String,
    val semanas: String
)
