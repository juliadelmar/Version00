package com.example.version00.ui.screens.actividades

// Importaciones necesarias
import android.content.Context // Para acceder a SharedPreferences y guardar el índice de pestaña
import androidx.compose.foundation.layout.Column // Para organizar los elementos verticalmente
import androidx.compose.material.Tab // Componente de pestaña (Material 2)
import androidx.compose.material.TabRow // Contenedor de pestañas
import androidx.compose.material.Text // Componente de texto
import androidx.compose.runtime.* // Para estado y composición
import androidx.compose.ui.graphics.Color // Para definir colores
import androidx.compose.ui.platform.LocalContext // Permite obtener el contexto actual (Context)
import androidx.navigation.NavHostController // Controlador de navegación (por si lo necesitas más adelante)

// Constantes para las claves de SharedPreferences
private const val PREFS_NAME = "app_prefs" // Nombre del archivo de preferencias
private const val PREF_LAST_TAB_INDEX = "last_activities_tab_index" // Clave para guardar el índice de pestaña

@Composable
fun ActividadesScreen(navController: NavHostController) {
    // Obtiene el contexto actual
    val context = LocalContext.current

    // Carga las preferencias compartidas (SharedPreferences) una sola vez
    val sharedPreferences = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Lista con los títulos de las pestañas
    val tabTitles = listOf("Actividad", "Histórico")

    // Estado para guardar la pestaña seleccionada, recuperando el valor guardado en SharedPreferences
    var selectedTabIndex by remember {
        mutableStateOf(sharedPreferences.getInt(PREF_LAST_TAB_INDEX, 0))
    }

    // Composición de la UI
    Column {
        // Fila de pestañas
        TabRow(
            selectedTabIndex = selectedTabIndex, // Indica cuál pestaña está seleccionada
            backgroundColor = Color.Black, // Color del fondo de la barra
            contentColor = Color(0xFFFF9800) // Color para la pestaña activa
        ) {
            // Crea una pestaña por cada título
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index, // Si está seleccionada
                    onClick = {
                        selectedTabIndex = index // Cambia la pestaña activa
                        // Guarda la selección en SharedPreferences para recordar al volver
                        sharedPreferences.edit().putInt(PREF_LAST_TAB_INDEX, index).apply()
                    },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray // Color del texto según si está activa o no
                        )
                    }
                )
            }
        }

        // Contenido según la pestaña seleccionada
        when (selectedTabIndex) {
            0 -> FatigaMuscularView() // Pestaña 0: muestra vista de fatiga muscular
            1 -> HistoricoView() // Pestaña 1: muestra el historial de rutinas
        }
    }
}
