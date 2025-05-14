package com.example.version00.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.example.version00.ui.screens.home.ContentTrainingScreen
@Composable
fun HomeScreen(viewModel: AuthViewModel, navController: NavHostController) {
    val selectedIndex = remember { mutableStateOf(0) }
    val currentSubsection = remember { mutableStateOf("home") }
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
        bottomBar = { BottomBarSyntra(selectedIndex) },
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

            when (currentSubsection.value) {
                "home" -> ContentBoxHome(
                    onNavigateToTraining = {
                        currentSubsection.value = "training"
                    },
                    onRutinaClick = { rutina ->
                        navController.navigate("rutinaDetail/${rutina.id}")
                    }
                )
            }
        }
    }
}
