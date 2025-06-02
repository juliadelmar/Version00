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

private const val PREFS_CALENDARIO_NAME = "calendario_menstrual_prefs_v3"
private const val PREF_KEY_PERIODOS_CONFIRMADOS_JSON = "periodos_confirmados_json"
private const val PREF_KEY_DURACION_CICLO_MANUAL = "duracion_ciclo_manual"
private const val PREF_KEY_DURACION_SANGRADO_MANUAL = "duracion_sangrado_manual"
private const val PREF_KEY_NOTAS_DIARIAS_PREFIX = "nota_dia_"

@Serializable
data class PeriodoConfirmado(val inicioStr: String, var finStr: String? = null) {
    @Transient val inicio: LocalDate = LocalDate.parse(inicioStr)
    @Transient val fin: LocalDate? = finStr?.let { LocalDate.parse(it) }
    @Transient val duracionSangrado: Int =
        if (fin != null && inicio <= fin) ChronoUnit.DAYS.between(inicio, fin).toInt() + 1 else 0

    fun contiene(dia: LocalDate): Boolean {
        val finReal = this.fin
        return if (finReal != null) !dia.isBefore(inicio) && !dia.isAfter(finReal) else dia.isEqual(inicio)
    }

    constructor(inicio: LocalDate, fin: LocalDate?) : this(inicio.toString(), fin?.toString())
}

data class Predicciones(
    val diasPeriodoProbables: Set<LocalDate> = emptySet(),
    val diasFertiles: Set<LocalDate> = emptySet(),
    val diasOvulacion: Set<LocalDate> = emptySet()
)

enum class FaseCiclo {
    NINGUNA, MENSTRUAL, FOLICULAR, OVULATORIA, LUTEA
}

class CalendarioMenstrualViewModel(context: Context) : ViewModel() {

    private val appContext = context.applicationContext
    private val sharedPreferences = appContext.getSharedPreferences(PREFS_CALENDARIO_NAME, Context.MODE_PRIVATE)

    // Estado de periodos y notas
    private val _periodosConfirmados = mutableStateOf<List<PeriodoConfirmado>>(emptyList())
    val periodosConfirmados: State<List<PeriodoConfirmado>> = _periodosConfirmados

    private val _notasDiarias = mutableStateMapOf<LocalDate, String>()
    val notasDiarias: SnapshotStateMap<LocalDate, String> = _notasDiarias

    // Duraciones personalizadas
    private val _duracionCicloManual = mutableStateOf(28)
    val duracionCicloManual: State<Int> = _duracionCicloManual

    private val _duracionSangradoManual = mutableStateOf(5)
    val duracionSangradoManual: State<Int> = _duracionSangradoManual

    // Duración de fallback para onboarding
    private val _duracionCicloManualFallback = mutableStateOf(28)
    val duracionCicloManualFallback: State<Int> = _duracionCicloManualFallback
    fun actualizarDuracionCicloManualFallback(valor: Int) {
        _duracionCicloManualFallback.value = valor.coerceIn(20, 60)
        recalcularTodo()
    }
    private val _ultimaPrediccionReemplazada = mutableStateOf<Predicciones?>(null)
    val ultimaPrediccionReemplazada: State<Predicciones?> = _ultimaPrediccionReemplazada

    // Promedios calculados y mostrables
    private val _cicloPromedioCalculado = mutableStateOf<Int?>(null)
    private val _sangradoPromedioCalculado = mutableStateOf<Int?>(null)

    private val _cicloPromedio = mutableStateOf(28)
    val cicloPromedio: State<Int> = _cicloPromedio

    private val _sangradoPromedio = mutableStateOf(5)
    val sangradoPromedio: State<Int> = _sangradoPromedio

    // Predicción de días fértiles, ovulación y futuros periodos
    private val _prediccionesActivas = mutableStateOf(Predicciones())
    val predicciones: State<Predicciones> get() = _prediccionesActivas

    // Estado actual del ciclo
    private val _diaActualDelCiclo = mutableStateOf<Int?>(null)
    val diaActualDelCiclo: State<Int?> = _diaActualDelCiclo

    private val _faseActualDelCiclo = mutableStateOf(FaseCiclo.NINGUNA)
    val faseActualDelCiclo: State<FaseCiclo> = _faseActualDelCiclo

    val periodoActivoSinFin: PeriodoConfirmado?
        get() = _periodosConfirmados.value.find { it.finStr == null }

    val cicloEfectivoParaPredicciones: Int
        get() = _cicloPromedioCalculado.value ?: _duracionCicloManualFallback.value

    val sangradoEfectivoParaPredicciones: Int
        get() = _sangradoPromedioCalculado.value ?: _duracionSangradoManual.value

    init {
        cargarDatosGuardados()
        recalcularTodo()
    }

    fun marcarInicioPeriodo(fecha: LocalDate) {
        if (periodoActivoSinFin != null && periodoActivoSinFin?.inicio != fecha) return
        if (_periodosConfirmados.value.any { it.inicio == fecha }) return

        val nuevoPeriodo = PeriodoConfirmado(fecha, null)
        _periodosConfirmados.value = (_periodosConfirmados.value + nuevoPeriodo).sortedBy { it.inicio }
        recalcularTodo()
        guardarDatos()
    }

    fun marcarFinPeriodo(fecha: LocalDate) {
        val periodoActivo = periodoActivoSinFin
        if (periodoActivo != null && !fecha.isBefore(periodoActivo.inicio)) {
            val indice = _periodosConfirmados.value.indexOf(periodoActivo)
            if (indice != -1) {
                val lista = _periodosConfirmados.value.toMutableList()
                lista[indice] = PeriodoConfirmado(periodoActivo.inicio, fecha)
                _periodosConfirmados.value = lista.sortedBy { it.inicio }
                recalcularTodo()
                guardarDatos()
            }
        }
    }

    fun desmarcarPeriodo(fechaInicio: LocalDate) {
        _periodosConfirmados.value = _periodosConfirmados.value.filterNot { it.inicio == fechaInicio }
        recalcularTodo()
        guardarDatos()
    }

    fun actualizarDuracionCicloManual(nuevaDuracion: Int) {
        _duracionCicloManual.value = nuevaDuracion.coerceIn(20, 60)
        recalcularTodo()
        guardarDatos()
    }

    fun actualizarDuracionSangradoManual(nuevaDuracion: Int) {
        _duracionSangradoManual.value = nuevaDuracion.coerceIn(1, 15)
        recalcularTodo()
        guardarDatos()
    }

    fun guardarNotaDia(fecha: LocalDate, nota: String) {
        if (nota.isNotBlank()) _notasDiarias[fecha] = nota else _notasDiarias.remove(fecha)
        guardarNotaEspecifica(fecha, nota)
    }

    private fun recalcularTodo() {
        recalcularPromedios()
        recalcularPrediccionesActivas()
        recalcularInfoCicloActual()
    }

    private fun recalcularPromedios() {
        val periodos = _periodosConfirmados.value.filter { it.fin != null }
        val promedios = periodos.map { it.duracionSangrado }.filter { it > 0 }

        val cicloCalc = if (periodos.size < 2) null else {
            periodos.map { it.inicio }.sorted().zipWithNext { a, b ->
                ChronoUnit.DAYS.between(a, b).toInt()
            }.average().toInt().coerceIn(20, 60)
        }

        val sangradoCalc = promedios.takeIf { it.isNotEmpty() }?.average()?.toInt()?.coerceIn(1, 15)

        _cicloPromedioCalculado.value = cicloCalc
        _sangradoPromedioCalculado.value = sangradoCalc

        _cicloPromedio.value = cicloCalc ?: _duracionCicloManualFallback.value
        _sangradoPromedio.value = sangradoCalc ?: 5
    }

    private fun recalcularPrediccionesActivas() {
        val anterior = _prediccionesActivas.value
        val confirmadoHasta = _periodosConfirmados.value.maxOfOrNull { it.fin ?: it.inicio }

        val ciclo = cicloEfectivoParaPredicciones
        val sangrado = sangradoEfectivoParaPredicciones

        val nuevasFertiles = mutableSetOf<LocalDate>()
        val nuevasOvulacion = mutableSetOf<LocalDate>()
        val nuevosPeriodoProbables = mutableSetOf<LocalDate>()

        var fechaInicio = _periodosConfirmados.value.maxOfOrNull { it.inicio } ?: return

        repeat(6) {
            val finPeriodo = fechaInicio.plusDays((sangrado - 1).toLong())

            for (i in 0 until sangrado) {
                val dia = fechaInicio.plusDays(i.toLong())
                if (confirmadoHasta == null || dia.isAfter(confirmadoHasta)) {
                    nuevosPeriodoProbables.add(dia)
                }
            }

            val ovulacion = fechaInicio.plusDays((ciclo - 14).toLong())
            if (confirmadoHasta == null || ovulacion.isAfter(confirmadoHasta)) {
                nuevasOvulacion.add(ovulacion)
            }

            for (offset in -5..1) {
                val fertil = ovulacion.plusDays(offset.toLong())
                if (confirmadoHasta == null || fertil.isAfter(confirmadoHasta)) {
                    nuevasFertiles.add(fertil)
                }
            }

            fechaInicio = fechaInicio.plusDays(ciclo.toLong())
        }

        // 🔄 Fusionamos con lo anterior
        _prediccionesActivas.value = Predicciones(
            diasPeriodoProbables = anterioresSinSolape(anterior.diasPeriodoProbables, confirmadoHasta) + nuevosPeriodoProbables,
            diasFertiles = anterioresSinSolape(anterior.diasFertiles, confirmadoHasta) + nuevasFertiles,
            diasOvulacion = anterioresSinSolape(anterior.diasOvulacion, confirmadoHasta) + nuevasOvulacion
        )
    }
    private fun anterioresSinSolape(
        anteriores: Set<LocalDate>,
        confirmadoHasta: LocalDate?
    ): Set<LocalDate> {
        if (confirmadoHasta == null) return emptySet()
        return anteriores.filter { it <= confirmadoHasta }.toSet()
    }


    private fun recalcularInfoCicloActual() {
        val hoy = LocalDate.now()
        val ultimoInicio = _periodosConfirmados.value
            .filter { !it.inicio.isAfter(hoy) }
            .maxByOrNull { it.inicio }

        if (ultimoInicio == null) {
            _diaActualDelCiclo.value = null
            _faseActualDelCiclo.value = FaseCiclo.NINGUNA
            return
        }

        val ciclo = cicloEfectivoParaPredicciones
        _diaActualDelCiclo.value = ChronoUnit.DAYS.between(ultimoInicio.inicio, hoy).toInt() + 1

        val ovulacion = ultimoInicio.inicio.plusDays((ciclo - 14).toLong())
        val inicioFertil = ovulacion.minusDays(5)
        val finFertil = ovulacion.plusDays(1)

        _faseActualDelCiclo.value = when {
            ultimoInicio.contiene(hoy) -> FaseCiclo.MENSTRUAL
            hoy == ovulacion -> FaseCiclo.OVULATORIA
            hoy in inicioFertil..finFertil -> FaseCiclo.FOLICULAR
            hoy.isBefore(inicioFertil) -> FaseCiclo.FOLICULAR
            else -> FaseCiclo.LUTEA
        }
    }

    private fun cargarDatosGuardados() {
        val json = sharedPreferences.getString(PREF_KEY_PERIODOS_CONFIRMADOS_JSON, null)
        _periodosConfirmados.value = try {
            if (json != null) Json.decodeFromString(
                ListSerializer(PeriodoConfirmado.serializer()), json
            ).sortedBy { it.inicio } else emptyList()
        } catch (e: Exception) {
            Log.e("CalendarioVM", "Error cargando JSON", e)
            emptyList()
        }

        _duracionCicloManual.value = sharedPreferences.getInt(PREF_KEY_DURACION_CICLO_MANUAL, 28)
        _duracionSangradoManual.value = sharedPreferences.getInt(PREF_KEY_DURACION_SANGRADO_MANUAL, 5)

        sharedPreferences.all.filterKeys { it.startsWith(PREF_KEY_NOTAS_DIARIAS_PREFIX) }.forEach { (k, v) ->
            try {
                val fecha = LocalDate.parse(k.removePrefix(PREF_KEY_NOTAS_DIARIAS_PREFIX))
                val nota = v as? String ?: ""
                if (nota.isNotEmpty()) _notasDiarias[fecha] = nota
            } catch (e: Exception) {
                Log.e("CalendarioVM", "Error cargando nota $k", e)
            }
        }
    }

    private fun guardarDatos() {
        try {
            val json = Json.encodeToString(
                ListSerializer(PeriodoConfirmado.serializer()),
                _periodosConfirmados.value
            )
            sharedPreferences.edit()
                .putString(PREF_KEY_PERIODOS_CONFIRMADOS_JSON, json)
                .putInt(PREF_KEY_DURACION_CICLO_MANUAL, _duracionCicloManual.value)
                .putInt(PREF_KEY_DURACION_SANGRADO_MANUAL, _duracionSangradoManual.value)
                .apply()
        } catch (e: Exception) {
            Log.e("CalendarioVM", "Error guardando JSON", e)
        }
    }

    private fun guardarNotaEspecifica(fecha: LocalDate, nota: String) {
        val key = PREF_KEY_NOTAS_DIARIAS_PREFIX + fecha.toString()
        with(sharedPreferences.edit()) {
            if (nota.isNotBlank()) putString(key, nota) else remove(key)
            apply()
        }
    }
}

class CalendarioViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarioMenstrualViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarioMenstrualViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
