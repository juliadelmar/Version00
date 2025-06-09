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
    val MUSCULOS = listOf(NINGUNO, "Pectoral", "Bíceps", "Tríceps", "Espalda", "Hombros", "Pierna", "Cuádriceps", "Isquiotibiales", "Gemelos", "Glúteo", "Abdomen", "Antebrazo", "Trapecio", "Oblicuos")
   }
// Clase de ViewModel para manejar la lógica de los ejercicios
class EjerciciosViewModel : ViewModel() {
    // Lista que contiene todos los ejercicios cargados de la API, sin filtrar ni paginar
    private val _ejerciciosCompletos = mutableStateListOf<Ejercicio>()

    // Estado de carga y error
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Filtros y búsqueda
    var busquedaPorTexto by mutableStateOf("")
        private set
    var filtroEquipamiento by mutableStateOf(FiltrosDisponibles.NINGUNO)
        private set
    var filtroDificultad by mutableStateOf(FiltrosDisponibles.DIFICULTADES.first())
        private set
    var filtroMusculo by mutableStateOf(FiltrosDisponibles.MUSCULOS.first())
        private set

    // Paginación
    private val _ejerciciosPorPagina = 30
    var cantidadMostrada by mutableStateOf(_ejerciciosPorPagina)
        private set

    // Job para la carga de ejercicios
    private var cargaJob: Job? = null

    // Lista interna que se filtra completamente antes de paginar
    private val _ejerciciosFiltradosInternos: List<Ejercicio>
        get() {
            var listaFiltrada = _ejerciciosCompletos.toList()

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

    // Expone la lista paginada de ejercicios filtrados
    val ejerciciosFiltradosPaginados: List<Ejercicio>
        get() = _ejerciciosFiltradosInternos.take(cantidadMostrada)

    // Indica si hay más ejercicios por mostrar
    val hayMasEjerciciosPorMostrar: Boolean
        get() = cantidadMostrada < _ejerciciosFiltradosInternos.size

    // Funciones para actualizar los filtros y la búsqueda
    fun actualizarBusquedaPorTexto(nuevaBusqueda: String) {
        Log.d("EjerciciosVM", "Actualizando búsqueda por texto a: $nuevaBusqueda")
        busquedaPorTexto = nuevaBusqueda
        reiniciarPaginacion()
    }

    fun actualizarFiltroEquipamiento(nuevoFiltro: String) {
        Log.d("EjerciciosVM", "Actualizando filtro de equipamiento a: $nuevoFiltro")
        filtroEquipamiento = nuevoFiltro
        reiniciarPaginacion()
    }

    fun actualizarFiltroDificultad(nuevoFiltro: String) {
        Log.d("EjerciciosVM", "Actualizando filtro de dificultad a: $nuevoFiltro")
        filtroDificultad = nuevoFiltro
        reiniciarPaginacion()
    }

    fun actualizarFiltroMusculo(nuevoFiltro: String) {
        Log.d("EjerciciosVM", "Actualizando filtro de músculo a: $nuevoFiltro")
        filtroMusculo = nuevoFiltro
        reiniciarPaginacion()
    }

    // Función para mostrar más ejercicios
    fun mostrarMasEjercicios() {
        if (hayMasEjerciciosPorMostrar) {
            Log.d("EjerciciosVM", "Mostrando más ejercicios. Antes: $cantidadMostrada. Total filtrados: ${_ejerciciosFiltradosInternos.size}")
            cantidadMostrada += _ejerciciosPorPagina
            Log.d("EjerciciosVM", "Mostrando más ejercicios. Después: $cantidadMostrada")
        } else {
            Log.d("EjerciciosVM", "No hay más ejercicios para mostrar. Mostrados: $cantidadMostrada, Total filtrados: ${_ejerciciosFiltradosInternos.size}")
        }
    }

    // Función para reiniciar la paginación
    private fun reiniciarPaginacion() {
        Log.d("EjerciciosVM", "Reiniciando paginación a $_ejerciciosPorPagina")
        cantidadMostrada = _ejerciciosPorPagina
    }

    // Función para cargar los ejercicios desde la API
    fun cargarEjercicios(forzarRecarga: Boolean = false) {
        if (_ejerciciosCompletos.isNotEmpty() && !forzarRecarga) {
            Log.d("EjerciciosVM", "Ejercicios ya cargados y no se fuerza recarga, omitiendo.")
            return
        }

        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            cargando = true
            error = null
            try {
                Log.d("EjerciciosVM", "Iniciando carga de ejercicios desde la API...")
                val resultado = RetrofitClient.api.obtenerEjercicios()
                _ejerciciosCompletos.clear()
                _ejerciciosCompletos.addAll(resultado)
                Log.d("EjerciciosVM", "Ejercicios cargados desde API: ${_ejerciciosCompletos.size}")
                reiniciarPaginacion()
            } catch (e: Exception) {
                Log.e("EjerciciosVM", "Error al cargar ejercicios", e)
                error = e.message ?: "Error desconocido"
                _ejerciciosCompletos.clear()
            } finally {
                cargando = false
                Log.d("EjerciciosVM", "Carga de ejercicios finalizada. Cargando: $cargando")
            }
        }
    }

    // Función para cancelar el job si el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        cargaJob?.cancel()
    }
}
