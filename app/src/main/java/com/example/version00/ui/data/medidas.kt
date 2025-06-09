package com.example.version00.ui.data

/**
 * Representa medidas corporales básicas por zonas anatómicas.
 * Se utiliza para registrar progresos físicos en secciones como “Medidas”.
 *
 * @property hombros Medida de los hombros en cm.
 * @property pecho Medida del pecho en cm.
 * @property cintura Medida de la cintura en cm.
 * @property abdomen Medida del abdomen en cm.
 * @property cadera Medida de la cadera en cm.
 * @property brazoIzquierdo Circunferencia del brazo izquierdo.
 * @property brazoDerecho Circunferencia del brazo derecho.
 * @property musloIzquierdo Circunferencia del muslo izquierdo.
 * @property musloDerecho Circunferencia del muslo derecho.
 * @property pantorrillaIzquierda Circunferencia de la pantorrilla izquierda.
 * @property pantorrillaDerecha Circunferencia de la pantorrilla derecha.
 */
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
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", "")
}

/**
 * Representa datos antropométricos avanzados para análisis corporal.
 * Útil para mostrar composiciones corporales o generar estadísticas precisas.
 */
data class DatosAvanzados(
    val alturaCm: String = "",           // Altura en centímetros
    val pesoKg: String = "",             // Peso actual
    val imc: String = "",                // Índice de Masa Corporal (calculado o manual)
    val porcentajeGrasa: String = "",    // Porcentaje de grasa corporal
    val pesoGrasoKg: String = "",        // Peso graso estimado
    val pesoMagroKg: String = "",        // Peso libre de grasa
    val pesoOseoKg: String = "",         // Estimación de masa ósea
    val pliegueTricipitalMm: String = "",     // Pliegue tricipital en mm
    val pliegueAbdominalMm: String = "",       // Pliegue abdominal en mm
    val pliegueSubescapularMm: String = "",    // Pliegue subescapular en mm
    val pliegueSuprailiacoMm: String = "",     // Pliegue suprailiaco en mm
    val diametroMunecaCm: String = "",         // Diámetro de muñeca
    val diametroFemurCm: String = "" ,
    val edad: String
// Diámetro del fémur
) {

    // Constructor requerido por Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", "", "","")
}

/**
 * Registro de peso corporal y métricas relacionadas en una fecha específica.
 * Se usa para generar gráficos de progreso y controlar metas de peso.
 *
 * @property fecha Fecha del registro (formato "dd MMM yyyy").
 * @property peso Peso actual en kilogramos.
 * @property meta Meta de peso establecida.
 * @property imc Índice de masa corporal, opcional.
 * @property grasa Porcentaje de grasa corporal, opcional.
 */
data class RegistroPeso(
    val fecha: String = "",
    val peso: Double = 0.0,
    val meta: Double = 0.0,
    val imc: Double? = null,
    val grasa: Double? = null
) {
    // Constructor sin argumentos necesario para Firebase
    constructor() : this("", 0.0, 0.0, null, null)
}
