package com.example.version00.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun ActividadesScreen(navController: NavHostController) {
    val tabTitles = listOf("Actividad", "Histórico")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Black,
            contentColor = Color(0xFFFF9800)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
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
            0 -> FatigaMuscularView() // Tu composable de actividad
            1 -> HistoricoView()      // Tu composable del histórico
        }
    }
}
