package com.example.version00.ui.data

// Data class for detailed body measurements
data class MedidasDetalladas(
    val hombros: String = "",
    val pecho: String = "",
    val cintura: String = "",
    val abdomen: String = "",
    val cadera: String = "",
    val brazoIzquierdo: String = "",
    val brazoDerecho: String = "",
    val musloIzquierdo: String = "",
    val musloDerecho: String = "",
    val pantorrillaIzquierda: String = "",
    val pantorrillaDerecha: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}

// Data class for advanced body data
data class DatosAvanzados(
    val alturaCm: String = "",           // e.g., "175"
    val pesoKg: String = "",             // Current weight, can be useful here for context or if advanced calculations depend on it
    val imc: String = "",                // Calculated or entered
    val porcentajeGrasa: String = "",    // e.g., "15.5"
    val pesoGrasoKg: String = "",
    val pesoMagroKg: String = "",
    val pesoOseoKg: String = "",
    val pliegueTricipitalMm: String = "",
    val pliegueAbdominalMm: String = "",
    val pliegueSubescapularMm: String = "",
    val pliegueSuprailiacoMm: String = "",
    val diametroMunecaCm: String = "",
    val diametroFemurCm: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "")
}

// Data class for weight log entries
data class RegistroPeso(
    val fecha: String = "",      // Format "dd MMM yyyy", e.g., "27 Oct 2024"
    val peso: Double = 0.0,
    val meta: Double = 0.0,
    val imc: Double? = null,     // BMI, can be null
    val grasa: Double? = null    // Body fat percentage, can be null
) {
    // No-argument constructor for Firebase
    constructor() : this("", 0.0, 0.0, null, null)
}