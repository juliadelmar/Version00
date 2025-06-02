package com.example.version00.ui.screens.actividades

import android.content.Context // Import necesario
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab // O androidx.compose.material3.Tab si migras a M3 completamente
import androidx.compose.material.TabRow // O androidx.compose.material3.TabRow si migras a M3 completamente
import androidx.compose.material.Text // O androidx.compose.material3.Text si migras a M3 completamente
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // Import necesario
import androidx.navigation.NavHostController

// Es buena práctica definir las claves de SharedPreferences como constantes
private const val PREFS_NAME = "app_prefs"
private const val PREF_LAST_TAB_INDEX = "last_activities_tab_index"

@Composable
fun ActividadesScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val tabTitles = listOf("Actividad", "Histórico")
    var selectedTabIndex by remember {
        // Leer el último índice guardado, o 0 por defecto
        mutableStateOf(sharedPreferences.getInt(PREF_LAST_TAB_INDEX, 0))
    }

    Column {
        TabRow( // Si usas Material 3, sería androidx.compose.material3.TabRow
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Black, // En M3, esto se configura diferente, ej. containerColor
            contentColor = Color(0xFFFF9800) // En M3, es selectedContentColor y unselectedContentColor
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab( // Si usas Material 3, sería androidx.compose.material3.Tab
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        // Guardar el índice seleccionado en SharedPreferences
                        sharedPreferences.edit().putInt(PREF_LAST_TAB_INDEX, index).apply()
                    },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) Color.White else Color.Gray
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> FatigaMuscularView()
            1 -> HistoricoView()
        }
    }
}