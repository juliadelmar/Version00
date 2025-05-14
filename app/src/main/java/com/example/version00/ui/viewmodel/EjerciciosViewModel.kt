package com.example.version00.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.version00.ui.model.Ejercicio
import com.example.version00.ui.network.RetrofitClient
import kotlinx.coroutines.launch
object FiltrosDisponibles {
    const val NINGUNO = "Todos"
    val EQUIPAMIENTOS = listOf(NINGUNO, "Sin equipo", "Mancuernas", "Barra", "Máquina", "Bandas elásticas")
    val DIFICULTADES = listOf(NINGUNO, "Principiante", "Intermedio", "Avanzado")
    val MUSCULOS = listOf(NINGUNO, "Pectoral", "Bíceps", "Glúteo", "Abdomen", "Espalda", "Pierna")
    val PARTE_DEL_CUERPO = listOf(NINGUNO, "Pecho", "Espalda", "Hombros", "Piernas", "Glúteos", "Abdomen", "Brazos")
}

class EjerciciosViewModel : ViewModel() {
    private val _ejercicios = mutableStateListOf<Ejercicio>()
    val todosLosEjercicios: List<Ejercicio> get() = _ejercicios

    val cargando = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    val busquedaPorTexto = mutableStateOf("")
    val filtroEquipamiento = mutableStateOf(FiltrosDisponibles.NINGUNO)
    val filtroDificultad = mutableStateOf(FiltrosDisponibles.NINGUNO)
    val filtroMusculo = mutableStateOf(FiltrosDisponibles.NINGUNO)

    val ejerciciosFiltrados: List<Ejercicio>
        get() {
            var listaFiltrada = todosLosEjercicios

            val queryTexto = busquedaPorTexto.value.trim().lowercase()
            if (queryTexto.isNotBlank()) {
                listaFiltrada = listaFiltrada.filter { ejercicio ->
                    (ejercicio.nombre.lowercase().contains(queryTexto)) ||
                            ejercicio.musculosTrabajados.principales.any { it.contains(queryTexto, ignoreCase = true) } ||
                            ejercicio.musculosTrabajados.secundarios.any { it.contains(queryTexto, ignoreCase = true) }
                    (ejercicio.tipoDeEjercicio.lowercase().contains(queryTexto)) ||
                            (ejercicio.equipo.lowercase().contains(queryTexto)) ||
                            (ejercicio.nivelDificultad.lowercase().contains(queryTexto))
                }
            }

            if (filtroEquipamiento.value != FiltrosDisponibles.NINGUNO) {
                listaFiltrada = listaFiltrada.filter {
                    it.equipo.equals(filtroEquipamiento.value, ignoreCase = true)
                }
            }

            if (filtroDificultad.value != FiltrosDisponibles.NINGUNO) {
                listaFiltrada = listaFiltrada.filter {
                    it.nivelDificultad.equals(filtroDificultad.value, ignoreCase = true)
                }
            }

            if (filtroMusculo.value != FiltrosDisponibles.NINGUNO) {
                listaFiltrada = listaFiltrada.filter {
                    it.musculosTrabajados.principales.any { musculo -> musculo.equals(filtroMusculo.value, ignoreCase = true) } ||
                            it.musculosTrabajados.secundarios.any { musculo -> musculo.equals(filtroMusculo.value, ignoreCase = true) }
                }
            }

            return listaFiltrada
        }

    fun actualizarBusquedaPorTexto(nuevaBusqueda: String) {
        busquedaPorTexto.value = nuevaBusqueda
    }

    fun cargarEjercicios() {
        viewModelScope.launch {
            cargando.value = true
            error.value = null
            try {
                Log.d("EjerciciosVM", "Iniciando carga de ejercicios...")
                val resultado = RetrofitClient.api.obtenerEjercicios()
                _ejercicios.clear()
                _ejercicios.addAll(resultado)
                Log.d("EjerciciosVM", "Ejercicios cargados: ${resultado.size}")
            } catch (e: Exception) {
                Log.e("EjerciciosVM", "Error al cargar ejercicios", e)
                error.value = e.message ?: "Error desconocido al obtener ejercicios."
                _ejercicios.clear()
            } finally {
                cargando.value = false
                Log.d("EjerciciosVM", "Carga de ejercicios finalizada. Cargando: ${cargando.value}")
            }
        }
    }
}
