package com.example.version00.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.components.BottomBarSyntra
import com.example.version00.ui.components.TopBarSyntra
import com.example.version00.ui.viewmodel.AuthViewModel

/**
 * Pantalla principal de inicio (HomeScreen) que gestiona la navegación inferior y superior,
 * y muestra contenido según la pestaña seleccionada.
 *
 * @param viewModel ViewModel que maneja la autenticación y carga del usuario.
 * @param navController Controlador de navegación para mover entre pantallas.
 */
@Composable
fun HomeScreen(viewModel: AuthViewModel, navController: NavHostController) {
    // Índice actual seleccionado en la barra inferior
    val selectedIndex = remember { mutableStateOf(0) }

    // URL del avatar del usuario
    var avatarUrl by remember { mutableStateOf("") }

    // Carga los datos del usuario al entrar en pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario { _, _, url ->
            avatarUrl = url
        }
    }

    // Scaffold con barra superior e inferior
    Scaffold(
        topBar = {
            TopBarSyntra(avatarUrl = avatarUrl) {
                navController.navigate("configuration")
            }
        },
        bottomBar = { BottomBarSyntra(selectedIndex, navController) },
        backgroundColor = MaterialTheme.colors.onBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            // Fondo de estrellas
            Image(
                painter = painterResource(id = R.drawable.stars_with_transparency),
                contentDescription = "Fondo Estrellas",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
            )

            // Contenido según la pestaña seleccionada
            when (selectedIndex.value) {
                0 -> ContentBoxHome(
                    navController = navController,
                    onNavigateToTraining = {
                        navController.navigate("training/1") // Puedes adaptar este ID dinámicamente
                    }
                )
            }
        }
    }
}
