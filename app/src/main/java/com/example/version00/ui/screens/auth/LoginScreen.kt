package com.example.version00.ui.screens.auth


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    navController: NavHostController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (viewModel.isLoggedIn) {
        onLoginSuccess()
    }
    LaunchedEffect(Unit) {
        viewModel.verificarSesionActiva {
            onLoginSuccess() // Navega a Home directamente
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Imagen de fondo de estrellas
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.stars_with_transparency),
            contentDescription = "Fondo estrellas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido con margen lateral
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.syntra_icon),
                contentDescription = "Logo Syntra",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 16.dp)
                    .align(androidx.compose.ui.Alignment.CenterHorizontally)
            )

            // Bienvenida
            Text(
                text = "Bienvenida a Syntra",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB39DDB),
                    unfocusedBorderColor = Color(0xFF9575CD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB39DDB),
                    unfocusedBorderColor = Color(0xFF9575CD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Registro
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color(0xFFB39DDB),
                modifier = Modifier
                    .clickable { navController.navigate("register") }
                    .padding(top = 8.dp)
            )

            viewModel.errorMessage?.let {
                Text(it, color = Color.Red)
            }
        }
    }
}
