package com.example.version00.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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

@Composable
fun HomeScreen(viewModel: AuthViewModel, navController: NavHostController) {
    val selectedIndex = remember { mutableStateOf(0) }
    var avatarUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario { _, _, url ->
            avatarUrl = url
        }
    }

    Scaffold(
        topBar = {
            TopBarSyntra(avatarUrl = avatarUrl) {
                navController.navigate("configuration")
            }
        },
        bottomBar = { BottomBarSyntra(selectedIndex, navController) }, // 👈 Aquí
        backgroundColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.stars_with_transparency),
                contentDescription = "Fondo Estrellas",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
            )

            // Solo muestra "home" si el índice es 0
            if (selectedIndex.value == 0) {
                ContentBoxHome(
                    onNavigateToTraining = {
                        navController.navigate("training/1") // ejemplo, usar ID real
                    },
                    onRutinaClick = { rutina ->
                        navController.navigate("rutinaDetail/${rutina.id}")
                    }
                )
            }
        }
    }
}
