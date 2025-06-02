// package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf // Sigue siendo útil si quieres observar la lista _ejerciciosCompletos directamente
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue // Para delegados by
import androidx.compose.runtime.setValue // Para delegados by
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.network.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object FiltrosDisponibles {
    const val NINGUNO = "Todos" // O "Cualquiera" o "Sin filtro"
    val EQUIPAMIENTOS = listOf(NINGUNO, "Peso corporal", "Mancuernas", "Barra", "Máquina", "Bandas elásticas")
    val DIFICULTADES = listOf(NINGUNO, "Principiante", "Intermedio", "Avanzado")
    // Unificamos MUSCULOS y PARTE_DEL_CUERPO. Usaremos MUSCULOS.
    val MUSCULOS = listOf(NINGUNO, "Pectoral", "Bíceps", "Tríceps", "Espalda", "Hombros", "Pierna", "Cuádriceps", "Isquiotibiales", "Gemelos", "Glúteo", "Abdomen", "Antebrazo", "Trapecio", "Oblicuos")
    // PARTE_DEL_CUERPO ya no es necesario si MUSCULOS es suficientemente granular
    // val PARTE_DEL_CUERPO = listOf(NINGUNO, "Pecho", "Espalda", "Hombros", "Piernas", "Glúteos", "Abdomen", "Brazos (general)", "Bíceps", "Tríceps")
}

class EjerciciosViewModel : ViewModel() {
    // Lista que contiene TODOS los ejercicios cargados de la API, sin filtrar ni paginar.
    private val _ejerciciosCompletos = mutableStateListOf<Ejercicio>()

    var cargando by mutableStateOf(false)
        private set // Solo el ViewModel puede modificarlo internamente
    var error by mutableStateOf<String?>(null)
        private set

    var busquedaPorTexto by mutableStateOf("")
        private set // Se actualiza con actualizarBusquedaPorTexto
    var filtroEquipamiento by mutableStateOf(FiltrosDisponibles.NINGUNO)
        private set // Se actualiza con actualizarFiltroEquipamiento
    var filtroDificultad by mutableStateOf(FiltrosDisponibles.DIFICULTADES.first()) // Usar .first() para asegurar un valor inicial válido
        private set // Se actualiza con actualizarFiltroDificultad
    var filtroMusculo by mutableStateOf(FiltrosDisponibles.MUSCULOS.first()) // Usar .first() y la lista MUSCULOS
        private set // Se actualiza con actualizarFiltroMusculo

    // --- PAGINACIÓN ---
    private val _ejerciciosPorPagina = 30
    var cantidadMostrada by mutableStateOf(_ejerciciosPorPagina) // Cuántos mostrar
        private set

    private var cargaJob: Job? = null

    // Lista interna que se filtra completamente antes de paginar
    private val _ejerciciosFiltradosInternos: List<Ejercicio>
        get() {
            var listaFiltrada = _ejerciciosCompletos.toList() // Trabajar con una copia

            val queryTexto = busquedaPorTexto.trim().lowercase()
            if (queryTexto.isNotBlank()) {
                listaFiltrada = listaFiltrada.filter { ejercicio ->
                    ejercicio.nombre.lowercase().contains(queryTexto) ||
                            ejercicio.musculosTrabajados.principales.any { it.lowercase().contains(queryTexto) } ||
                            ejercicio.musculosTrabajados.secundarios.any { it.lowercase().contains(queryTexto) } ||
                            ejercicio.tipoDeEjercicio.lowercase().contains(queryTexto) ||
                            ejercicio.equipo.lowercase().contains(queryTexto) ||
                            ejercicio.nivelDificultad.lowercase().contains(queryTexto)
                }
            }

            if (filtroEquipamiento != FiltrosDisponibles.NINGUNO) {
                listaFiltrada = listaFiltrada.filter {
                    it.equipo.equals(filtroEquipamiento, ignoreCase = true)
                }
            }

            if (filtroDificultad != FiltrosDisponibles.NINGUNO) {
                listaFiltrada = listaFiltrada.filter {
                    it.nivelDificultad.equals(filtroDificultad, ignoreCase = true)
                }
            }

            if (filtroMusculo != FiltrosDisponibles.NINGUNO) {
                listaFiltrada = listaFiltrada.filter {
                    it.musculosTrabajados.principales.any { musculo -> musculo.equals(filtroMusculo, ignoreCase = true) } ||
                            it.musculosTrabajados.secundarios.any { musculo -> musculo.equals(filtroMusculo, ignoreCase = true) }
                }
            }
            return listaFiltrada
        }

    // Expone solo la porción paginada de los ejercicios filtrados. ESTA ES LA LISTA QUE LA UI DEBERÍA OBSERVAR.
    val ejerciciosFiltradosPaginados: List<Ejercicio>
        get() = _ejerciciosFiltradosInternos.take(cantidadMostrada)

    // Para saber si hay más ejercicios por mostrar (después de filtrar)
    val hayMasEjerciciosPorMostrar: Boolean
        get() = cantidadMostrada < _ejerciciosFiltradosInternos.size

    fun actualizarBusquedaPorTexto(nuevaBusqueda: String) {
        busquedaPorTexto = nuevaBusqueda
        reiniciarPaginacion()
    }

    fun actualizarFiltroEquipamiento(nuevoFiltro: String) {
        filtroEquipamiento = nuevoFiltro
        reiniciarPaginacion()
    }

    fun actualizarFiltroDificultad(nuevoFiltro: String) {
        filtroDificultad = nuevoFiltro
        reiniciarPaginacion()
    }

    fun actualizarFiltroMusculo(nuevoFiltro: String) {
        filtroMusculo = nuevoFiltro
        reiniciarPaginacion()
    }

    fun mostrarMasEjercicios() {
        if (hayMasEjerciciosPorMostrar) {
            Log.d("EjerciciosVM", "Mostrando más. Antes: $cantidadMostrada. Total filtrados: ${_ejerciciosFiltradosInternos.size}")
            cantidadMostrada += _ejerciciosPorPagina
            Log.d("EjerciciosVM", "Mostrando más. Después: $cantidadMostrada")
        } else {
            Log.d("EjerciciosVM", "No hay más ejercicios para mostrar. Mostrados: $cantidadMostrada, Total filtrados: ${_ejerciciosFiltradosInternos.size}")
        }
    }

    private fun reiniciarPaginacion() {
        Log.d("EjerciciosVM", "Reiniciando paginación a $_ejerciciosPorPagina")
        cantidadMostrada = _ejerciciosPorPagina
    }

    fun cargarEjercicios(forzarRecarga: Boolean = false) {
        if (_ejerciciosCompletos.isNotEmpty() && !forzarRecarga) {
            Log.d("EjerciciosVM", "Ejercicios ya cargados y no se fuerza recarga, omitiendo.")
            // Asegurarse de que la paginación se aplique incluso si no se recarga
            // Esto podría no ser necesario si la UI se recompone correctamente con los estados existentes.
            // reiniciarPaginacion() // Considera si esto es necesario o causa un reset no deseado al volver a la pantalla
            return
        }
        cargaJob?.cancel() // Cancela la carga anterior si existe
        cargaJob = viewModelScope.launch {
            cargando = true
            error = null
            try {
                Log.d("EjerciciosVM", "Iniciando carga de ejercicios desde la API...")
                val resultado = RetrofitClient.api.obtenerEjercicios()
                _ejerciciosCompletos.clear()
                _ejerciciosCompletos.addAll(resultado)
                Log.d("EjerciciosVM", "Ejercicios cargados desde API: ${_ejerciciosCompletos.size}")
                reiniciarPaginacion() // Muy importante: resetear la paginación después de cargar nuevos datos.

            } catch (e: Exception) {
                Log.e("EjerciciosVM", "Error al cargar ejercicios", e)
                error = e.message ?: "Error desconocido al obtener ejercicios."
                _ejerciciosCompletos.clear() // Limpiar en caso de error
            } finally {
                cargando = false
                Log.d("EjerciciosVM", "Carga de ejercicios finalizada. Cargando: $cargando")
            }
        }
    }

    // Opcional: para cancelar el job si el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        cargaJob?.cancel()
    }
}