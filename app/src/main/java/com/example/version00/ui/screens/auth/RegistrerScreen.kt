package com.example.version00.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(viewModel: AuthViewModel, onRegisterSuccess: () -> Unit,  navController: NavHostController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    if (viewModel.isLoggedIn) {
        onRegisterSuccess()
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        // Fondo de estrellas
        Image(
            painter = painterResource(id = R.drawable.stars_with_transparency),
            contentDescription = "Fondo estrellas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB39DDB),
                    unfocusedBorderColor = Color(0xFF9575CD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB39DDB),
                    unfocusedBorderColor = Color(0xFF9575CD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de registro
            Button(
                onClick = {
                    viewModel.register(email, password, nombre){
                        onRegisterSuccess()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            viewModel.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
        }
    }
}

