package com.example.version00.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.temporal.ChronoUnit

// Constantes para las preferencias compartidas
private const val PREFS_CALENDARIO_NAME = "calendario_menstrual_prefs_v3"
private const val PREF_KEY_PERIODOS_CONFIRMADOS_JSON = "periodos_confirmados_json"
private const val PREF_KEY_DURACION_CICLO_MANUAL = "duracion_ciclo_manual"
private const val PREF_KEY_DURACION_SANGRADO_MANUAL = "duracion_sangrado_manual"
private const val PREF_KEY_NOTAS_DIARIAS_PREFIX = "nota_dia_"

// Clase de datos para representar un período menstrual confirmado
@Serializable
data class PeriodoConfirmado(val inicioStr: String, var finStr: String? = null) {
    // Propiedades derivadas para inicio y fin del período
    @Transient val inicio: LocalDate = LocalDate.parse(inicioStr)
    @Transient val fin: LocalDate? = finStr?.let { LocalDate.parse(it) }
    // Duración del sangrado en días
    @Transient val duracionSangrado: Int =
        if (fin != null && inicio <= fin) ChronoUnit.DAYS.between(inicio, fin).toInt() + 1 else 0

    // Verifica si un día específico está dentro del período
    fun contiene(dia: LocalDate): Boolean {
        val finReal = this.fin
        return if (finReal != null) !dia.isBefore(inicio) && !dia.isAfter(finReal) else dia.isEqual(inicio)
    }

    // Constructor secundario para crear un período a partir de fechas
    constructor(inicio: LocalDate, fin: LocalDate?) : this(inicio.toString(), fin?.toString())
}

// Clase de datos para representar predicciones del ciclo menstrual
data class Predicciones(
    val diasPeriodoProbables: Set<LocalDate> = emptySet(),
    val diasFertiles: Set<LocalDate> = emptySet(),
    val diasOvulacion: Set<LocalDate> = emptySet()
)

// Enumeración para representar las fases del ciclo menstrual
enum class FaseCiclo {
    NINGUNA, MENSTRUAL, FOLICULAR, OVULATORIA, LUTEA
}

// ViewModel para el calendario menstrual
class CalendarioMenstrualViewModel(context: Context) : ViewModel() {
    // Contexto de la aplicación
    private val appContext = context.applicationContext

    // Preferencias compartidas para almacenar datos
    private val sharedPreferences =
        appContext.getSharedPreferences(PREFS_CALENDARIO_NAME, Context.MODE_PRIVATE)

    // Estado de períodos y notas
    private val _periodosConfirmados = mutableStateOf<List<PeriodoConfirmado>>(emptyList())
    val periodosConfirmados: State<List<PeriodoConfirmado>> = _periodosConfirmados

    private val _notasDiarias = mutableStateMapOf<LocalDate, String>()
    val notasDiarias: SnapshotStateMap<LocalDate, String> = _notasDiarias

    // Duraciones personalizadas para el ciclo y sangrado
    private val _duracionCicloManual = mutableStateOf(28)
    val duracionCicloManual: State<Int> = _duracionCicloManual

    private val _duracionSangradoManual = mutableStateOf(5)
    val duracionSangradoManual: State<Int> = _duracionSangradoManual


    // Duración de fallback para onboarding (valor por defecto para el ciclo menstrual)
    private val _duracionCicloManualFallback = mutableStateOf(28)
    val duracionCicloManualFallback: State<Int> = _duracionCicloManualFallback

    // Función para actualizar la duración del ciclo menstrual de fallback
    fun actualizarDuracionCicloManualFallback(valor: Int) {
        // Establece el valor dentro del rango permitido (20-60 días)
        _duracionCicloManualFallback.value = valor.coerceIn(20, 60)
        // Recalcula todos los valores relacionados con el ciclo menstrual
        recalcularTodo()
    }

    // Última predicción reemplazada (almacena la última predicción que fue reemplazada)
    private val _ultimaPrediccionReemplazada = mutableStateOf<Predicciones?>(null)
    val ultimaPrediccionReemplazada: State<Predicciones?> = _ultimaPrediccionReemplazada

    // Promedios calculados y mostrables (valores promedio del ciclo menstrual y sangrado)
    private val _cicloPromedioCalculado = mutableStateOf<Int?>(null)
    private val _sangradoPromedioCalculado = mutableStateOf<Int?>(null)

    // Valores promedio del ciclo menstrual y sangrado (utilizados para mostrar en la interfaz de usuario)
    private val _cicloPromedio = mutableStateOf(28)
    val cicloPromedio: State<Int> = _cicloPromedio

    private val _sangradoPromedio = mutableStateOf(5)
    val sangradoPromedio: State<Int> = _sangradoPromedio

    // Predicción de días fértiles, ovulación y futuros períodos (almacena las predicciones actuales)
    private val _prediccionesActivas = mutableStateOf(Predicciones())
    val predicciones: State<Predicciones> get() = _prediccionesActivas

    // Estado actual del ciclo menstrual (día actual y fase actual)
    private val _diaActualDelCiclo = mutableStateOf<Int?>(null)
    val diaActualDelCiclo: State<Int?> = _diaActualDelCiclo

    private val _faseActualDelCiclo = mutableStateOf(FaseCiclo.NINGUNA)
    val faseActualDelCiclo: State<FaseCiclo> = _faseActualDelCiclo

    // Período activo sin fin (devuelve el período menstrual actual sin fecha de fin)
    val periodoActivoSinFin: PeriodoConfirmado?
        get() = _periodosConfirmados.value.find { it.finStr == null }

    // Ciclo efectivo para predicciones (utiliza el valor promedio calculado o el valor de fallback)
    val cicloEfectivoParaPredicciones: Int
        get() = _cicloPromedioCalculado.value ?: _duracionCicloManualFallback.value

    // Sangrado efectivo para predicciones (utiliza el valor promedio calculado o el valor manual)
    val sangradoEfectivoParaPredicciones: Int
        get() = _sangradoPromedioCalculado.value ?: _duracionSangradoManual.value

    // Inicialización del ViewModel (carga los datos guardados y recalcula todo)
    init {
        cargarDatosGuardados()
        recalcularTodo()
    }

    // Función para marcar el inicio de un período menstrual
    fun marcarInicioPeriodo(fecha: LocalDate) {
        // Verifica si ya hay un período activo sin fin y si la fecha de inicio es diferente
        if (periodoActivoSinFin != null && periodoActivoSinFin?.inicio != fecha) return
        // Verifica si ya hay un período con la misma fecha de inicio
        if (_periodosConfirmados.value.any { it.inicio == fecha }) return

        // Crea un nuevo período menstrual con la fecha de inicio proporcionada
        val nuevoPeriodo = PeriodoConfirmado(fecha, null)
        // Agrega el nuevo período a la lista de períodos confirmados y ordena la lista por fecha de inicio
        _periodosConfirmados.value =
            (_periodosConfirmados.value + nuevoPeriodo).sortedBy { it.inicio }
        // Recalcula todos los valores relacionados con el ciclo menstrual
        recalcularTodo()
        // Guarda los datos actualizados
        guardarDatos()
    }

    // Función para marcar el fin de un período menstrual
    fun marcarFinPeriodo(fecha: LocalDate) {
        // Obtiene el período activo sin fin
        val periodoActivo = periodoActivoSinFin
        // Verifica si hay un período activo y si la fecha de fin es posterior a la fecha de inicio
        if (periodoActivo != null && !fecha.isBefore(periodoActivo.inicio)) {
            // Obtiene el índice del período activo en la lista de períodos confirmados
            val indice = _periodosConfirmados.value.indexOf(periodoActivo)
            // Verifica si el índice es válido
            if (indice != -1) {
                // Crea una lista mutable a partir de la lista de períodos confirmados
                val lista = _periodosConfirmados.value.toMutableList()
                // Actualiza el período activo con la fecha de fin proporcionada
                lista[indice] = PeriodoConfirmado(periodoActivo.inicio, fecha)
                // Actualiza la lista de períodos confirmados y ordena la lista por fecha de inicio
                _periodosConfirmados.value = lista.sortedBy { it.inicio }
                // Recalcula todos los valores relacionados con el ciclo menstrual
                recalcularTodo()
                // Guarda los datos actualizados
                guardarDatos()
            }
        }
    }

    // Función para desmarcar un período menstrual
    fun desmarcarPeriodo(fechaInicio: LocalDate) {
        // Filtra la lista de períodos confirmados para eliminar el período con la fecha de inicio proporcionada
        _periodosConfirmados.value =
            _periodosConfirmados.value.filterNot { it.inicio == fechaInicio }
        // Recalcula todos los valores relacionados con el ciclo menstrual
        recalcularTodo()
        // Guarda los datos actualizados
        guardarDatos()
    }

    // Función para actualizar la duración del ciclo menstrual manual
    fun actualizarDuracionCicloManual(nuevaDuracion: Int) {
        // Establece la nueva duración dentro del rango permitido (20-60 días)
        _duracionCicloManual.value = nuevaDuracion.coerceIn(20, 60)
        // Recalcula todos los valores relacionados con el ciclo menstrual
        recalcularTodo()
        // Guarda los datos actualizados
        guardarDatos()
    }

    // Función para actualizar la duración del sangrado manual
    fun actualizarDuracionSangradoManual(nuevaDuracion: Int) {
        // Establece la nueva duración dentro del rango permitido (1-15 días)
        _duracionSangradoManual.value = nuevaDuracion.coerceIn(1, 15)
        // Recalcula todos los valores relacionados con el ciclo menstrual
        recalcularTodo()
        // Guarda los datos actualizados
        guardarDatos()
    }

    // Función para guardar una nota diaria
    fun guardarNotaDia(fecha: LocalDate, nota: String) {
        // Verifica si la nota no está vacía y la almacena en la lista de notas diarias
        if (nota.isNotBlank()) _notasDiarias[fecha] = nota else _notasDiarias.remove(fecha)
        // Guarda la nota específica en las preferencias compartidas
        guardarNotaEspecifica(fecha, nota)
    }

    // Función para recalcular todos los valores relacionados con el ciclo menstrual
    private fun recalcularTodo() {
        // Recalcula los promedios del ciclo menstrual y sangrado
        recalcularPromedios()
        // Recalcula las predicciones activas
        recalcularPrediccionesActivas()
        // Recalcula la información del ciclo menstrual actual
        recalcularInfoCicloActual()
    }

    // Función para recalcular los promedios del ciclo menstrual y sangrado
    private fun recalcularPromedios() {
        // Obtiene la lista de períodos confirmados con fecha de fin
        val periodos = _periodosConfirmados.value.filter { it.fin != null }
        // Obtiene la lista de duraciones de sangrado
        val promedios = periodos.map { it.duracionSangrado }.filter { it > 0 }

        // Calcula el promedio del ciclo menstrual
        val cicloCalc = if (periodos.size < 2) null else {
            // Calcula la diferencia en días entre las fechas de inicio de los períodos
            periodos.map { it.inicio }.sorted().zipWithNext { a, b ->
                ChronoUnit.DAYS.between(a, b).toInt()
            }.average().toInt().coerceIn(20, 60)
        }

        // Calcula el promedio del sangrado
        val sangradoCalc = promedios.takeIf { it.isNotEmpty() }?.average()?.toInt()?.coerceIn(1, 15)

        // Actualiza los valores promedio calculados
        _cicloPromedioCalculado.value = cicloCalc
        _sangradoPromedioCalculado.value = sangradoCalc

        // Actualiza los valores promedio mostrables
        _cicloPromedio.value = cicloCalc ?: _duracionCicloManualFallback.value
        _sangradoPromedio.value = sangradoCalc ?: 5
    }

    // Función para recalcular las predicciones activas
    private fun recalcularPrediccionesActivas() {
        // Obtiene las predicciones actuales
        val anterior = _prediccionesActivas.value
        // Obtiene la fecha más reciente confirmada (ya sea el fin de un período o su inicio si no tiene fin)
        val confirmadoHasta = _periodosConfirmados.value.maxOfOrNull { it.fin ?: it.inicio }

        // Obtiene el ciclo efectivo para predicciones (promedio calculado o valor de fallback)
        val ciclo = cicloEfectivoParaPredicciones
        // Obtiene el sangrado efectivo para predicciones (promedio calculado o valor manual)
        val sangrado = sangradoEfectivoParaPredicciones

        // Conjuntos para almacenar las nuevas predicciones
        val nuevasFertiles = mutableSetOf<LocalDate>()
        val nuevasOvulacion = mutableSetOf<LocalDate>()
        val nuevosPeriodoProbables = mutableSetOf<LocalDate>()

        // Obtiene la fecha de inicio del último período confirmado
        var fechaInicio = _periodosConfirmados.value.maxOfOrNull { it.inicio } ?: return

        // Repite el cálculo para los próximos 6 ciclos
        repeat(6) {
            // Calcula la fecha de fin del período actual
            val finPeriodo = fechaInicio.plusDays((sangrado - 1).toLong())

            // Agrega los días del período actual a la lista de días probables
            for (i in 0 until sangrado) {
                val dia = fechaInicio.plusDays(i.toLong())
                // Verifica si el día es posterior a la fecha confirmada hasta ahora
                if (confirmadoHasta == null || dia.isAfter(confirmadoHasta)) {
                    nuevosPeriodoProbables.add(dia)
                }
            }

            // Calcula la fecha de ovulación
            val ovulacion = fechaInicio.plusDays((ciclo - 14).toLong())
            // Verifica si la fecha de ovulación es posterior a la fecha confirmada hasta ahora
            if (confirmadoHasta == null || ovulacion.isAfter(confirmadoHasta)) {
                nuevasOvulacion.add(ovulacion)
            }

            // Agrega los días fértiles alrededor de la fecha de ovulación
            for (offset in -5..1) {
                val fertil = ovulacion.plusDays(offset.toLong())
                // Verifica si el día fértil es posterior a la fecha confirmada hasta ahora
                if (confirmadoHasta == null || fertil.isAfter(confirmadoHasta)) {
                    nuevasFertiles.add(fertil)
                }
            }

            // Avanza a la fecha de inicio del próximo ciclo
            fechaInicio = fechaInicio.plusDays(ciclo.toLong())
        }

        // Fusiona las nuevas predicciones con las anteriores, eliminando solapes
        _prediccionesActivas.value = Predicciones(
            diasPeriodoProbables = anterioresSinSolape(
                anterior.diasPeriodoProbables,
                confirmadoHasta
            ) + nuevosPeriodoProbables,
            diasFertiles = anterioresSinSolape(
                anterior.diasFertiles,
                confirmadoHasta
            ) + nuevasFertiles,
            diasOvulacion = anterioresSinSolape(
                anterior.diasOvulacion,
                confirmadoHasta
            ) + nuevasOvulacion
        )
    }

    // Función para obtener las fechas anteriores sin solape con las fechas confirmadas
    private fun anterioresSinSolape(
        anteriores: Set<LocalDate>,
        confirmadoHasta: LocalDate?
    ): Set<LocalDate> {
        // Si no hay fecha confirmada, devuelve un conjunto vacío
        if (confirmadoHasta == null) return emptySet()
        // Filtra las fechas anteriores que sean anteriores o iguales a la fecha confirmada
        return anteriores.filter { it <= confirmadoHasta }.toSet()
    }

    // Función para recalcular la información del ciclo menstrual actual
    private fun recalcularInfoCicloActual() {
        // Obtiene la fecha actual
        val hoy = LocalDate.now()
        // Obtiene el último período menstrual confirmado que no sea posterior a la fecha actual
        val ultimoInicio = _periodosConfirmados.value
            .filter { !it.inicio.isAfter(hoy) }
            .maxByOrNull { it.inicio }

        // Si no hay período menstrual confirmado, establece el día actual y la fase actual en null y NINGUNA, respectivamente
        if (ultimoInicio == null) {
            _diaActualDelCiclo.value = null
            _faseActualDelCiclo.value = FaseCiclo.NINGUNA
            return
        }

        // Obtiene el ciclo efectivo para predicciones
        val ciclo = cicloEfectivoParaPredicciones
        // Calcula el día actual del ciclo menstrual
        _diaActualDelCiclo.value = ChronoUnit.DAYS.between(ultimoInicio.inicio, hoy).toInt() + 1

        // Calcula la fecha de ovulación
        val ovulacion = ultimoInicio.inicio.plusDays((ciclo - 14).toLong())
        // Calcula el inicio y fin del período fértil
        val inicioFertil = ovulacion.minusDays(5)
        val finFertil = ovulacion.plusDays(1)

        // Establece la fase actual del ciclo menstrual según la fecha actual
        _faseActualDelCiclo.value = when {
            // Si la fecha actual está dentro del período menstrual, la fase es MENSTRUAL
            ultimoInicio.contiene(hoy) -> FaseCiclo.MENSTRUAL
            // Si la fecha actual es la fecha de ovulación, la fase es OVULATORIA
            hoy == ovulacion -> FaseCiclo.OVULATORIA
            // Si la fecha actual está dentro del período fértil, la fase es FOLICULAR
            hoy in inicioFertil..finFertil -> FaseCiclo.FOLICULAR
            // Si la fecha actual es anterior al inicio del período fértil, la fase es FOLICULAR
            hoy.isBefore(inicioFertil) -> FaseCiclo.FOLICULAR
            // De lo contrario, la fase es LUTEA
            else -> FaseCiclo.LUTEA
        }
    }

    // Función para cargar los datos guardados en las preferencias compartidas
    private fun cargarDatosGuardados() {
        // Obtiene el JSON que representa la lista de períodos menstruales confirmados
        val json = sharedPreferences.getString(PREF_KEY_PERIODOS_CONFIRMADOS_JSON, null)
        // Deserializa el JSON en una lista de períodos menstruales confirmados
        _periodosConfirmados.value = try {
            if (json != null) Json.decodeFromString(
                ListSerializer(PeriodoConfirmado.serializer()), json
            ).sortedBy { it.inicio } else emptyList()
        } catch (e: Exception) {
            // Loguea el error y devuelve una lista vacía si hay un error al deserializar el JSON
            Log.e("CalendarioVM", "Error cargando JSON", e)
            emptyList()
        }

        // Carga la duración del ciclo menstrual manual y la duración del sangrado manual desde las preferencias compartidas
        _duracionCicloManual.value = sharedPreferences.getInt(PREF_KEY_DURACION_CICLO_MANUAL, 28)
        _duracionSangradoManual.value =
            sharedPreferences.getInt(PREF_KEY_DURACION_SANGRADO_MANUAL, 5)

        // Carga las notas diarias desde las preferencias compartidas
        sharedPreferences.all.filterKeys { it.startsWith(PREF_KEY_NOTAS_DIARIAS_PREFIX) }
            .forEach { (k, v) ->
                try {
                    // Obtiene la fecha y la nota asociada
                    val fecha = LocalDate.parse(k.removePrefix(PREF_KEY_NOTAS_DIARIAS_PREFIX))
                    val nota = v as? String ?: ""
                    // Agrega la nota a la lista de notas diarias si no está vacía
                    if (nota.isNotEmpty()) _notasDiarias[fecha] = nota
                } catch (e: Exception) {
                    // Loguea el error si hay un error al cargar la nota
                    Log.e("CalendarioVM", "Error cargando nota $k", e)
                }
            }
    }

    // Función para guardar los datos en las preferencias compartidas
    private fun guardarDatos() {
        try {
            // Serializa la lista de períodos menstruales confirmados en un JSON
            val json = Json.encodeToString(
                ListSerializer(PeriodoConfirmado.serializer()),
                _periodosConfirmados.value
            )
            // Guarda el JSON y las duraciones manuales en las preferencias compartidas
            sharedPreferences.edit()
                .putString(PREF_KEY_PERIODOS_CONFIRMADOS_JSON, json)
                .putInt(PREF_KEY_DURACION_CICLO_MANUAL, _duracionCicloManual.value)
                .putInt(PREF_KEY_DURACION_SANGRADO_MANUAL, _duracionSangradoManual.value)
                .apply()
        } catch (e: Exception) {
            // Loguea el error si hay un error al guardar los datos
            Log.e("CalendarioVM", "Error guardando JSON", e)
        }
    }

    // Función para guardar una nota específica en las preferencias compartidas
    private fun guardarNotaEspecifica(fecha: LocalDate, nota: String) {
        // Crea la clave para la nota específica
        val key = PREF_KEY_NOTAS_DIARIAS_PREFIX + fecha.toString()
        // Guarda la nota en las preferencias compartidas si no está vacía, o la elimina si lo está
        with(sharedPreferences.edit()) {
            if (nota.isNotBlank()) putString(key, nota) else remove(key)
            apply()
        }
    }

    // Clase de fábrica para crear instancias del ViewModel
    class CalendarioViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        // Función para crear una instancia del ViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Verifica si la clase es asignable desde CalendarioMenstrualViewModel
            if (modelClass.isAssignableFrom(CalendarioMenstrualViewModel::class.java)) {
                // Crea una instancia del ViewModel con el contexto proporcionado
                @Suppress("UNCHECKED_CAST")
                return CalendarioMenstrualViewModel(context) as T
            }
            // Lanza una excepción si la clase no es válida
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

  
