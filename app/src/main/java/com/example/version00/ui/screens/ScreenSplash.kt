package com.example.version00.ui.screens

import com.example.version00.ui.viewmodel.AuthViewModel


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.navigation.AppDestinations
import kotlinx.coroutines.delay
@Composable
fun SplashScreen(
    navController: NavHostController,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel,
) {
    LaunchedEffect(Unit) {
        delay(1500) // Delay visual para el splash

        viewModel.verificarSesionActiva(
            onUsuarioActivo = {
                onLoginSuccess()
            },
            onNoSesion = {
                navController.navigate(AppDestinations.LOGIN_ROUTE) {
                    popUpTo(AppDestinations.SPLASH_ROUTE) { inclusive = true }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.syntra_icon),
                contentDescription = "Logo Syntra",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
