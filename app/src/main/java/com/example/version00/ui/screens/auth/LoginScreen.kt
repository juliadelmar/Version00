package com.example.version00.ui.screens.auth

// Imports necesarios para la interfaz, manejo de estado, navegación, estilos y recursos
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.viewmodel.AuthViewModel

// Composable principal para la pantalla de Login
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,       // Acción al iniciar sesión con éxito
    navController: NavHostController
) {
    // Estados locales para capturar el input del usuario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Si el usuario ya está logueado, redirigimos automáticamente
    if (viewModel.isLoggedIn) {
        onLoginSuccess()
    }

    // Contenedor general
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo negro para estética espacial
    ) {
        // Imagen de fondo con estrellas
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.stars_with_transparency),
            contentDescription = "Fondo estrellas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido central vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Logo de la app en el centro superior
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.syntra_icon),
                contentDescription = "Logo Syntra",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 16.dp)
                    .align(androidx.compose.ui.Alignment.CenterHorizontally)
            )

            // Mensaje de bienvenida
            Text(
                text = "Bienvenida a Syntra",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de entrada para el email
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

            // Campo de entrada para la contraseña, con ocultación de caracteres
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

            // Botón para iniciar sesión
            Button(
                onClick = { viewModel.login(email, password) }, // Llama al método del ViewModel
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto clickeable que navega a la pantalla de registro
            Text(
                text = "¿No tienes cuenta? Regístrate",
                color = Color(0xFFB39DDB),
                modifier = Modifier
                    .clickable { navController.navigate("register") }
                    .padding(top = 8.dp)
            )

            // Si hay error en el login, mostrar el mensaje
            viewModel.errorMessage?.let {
                Text(it, color = Color.Red)
            }
        }
    }
}
