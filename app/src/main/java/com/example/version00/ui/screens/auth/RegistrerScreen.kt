package com.example.version00.ui.screens.auth

// Imports necesarios para UI, estado, navegación y recursos
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
import com.example.version00.R
import com.example.version00.ui.viewmodel.AuthViewModel

// Pantalla de registro de usuario
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,              // ViewModel para manejar registro
    onRegisterSuccess: () -> Unit,         // Callback cuando el registro es exitoso
    navController: NavHostController       // Controlador de navegación
) {
    // Estados para capturar el input del usuario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Si ya está logueado, redirige automáticamente
    if (viewModel.isLoggedIn) {
        onRegisterSuccess()
    }

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo negro
    ) {
        // Imagen de fondo (estética espacial)
        Image(
            painter = painterResource(id = R.drawable.stars_with_transparency),
            contentDescription = "Fondo estrellas",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido centrado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                "Crear cuenta",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Nombre completo
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB39DDB),  // Lila claro
                    unfocusedBorderColor = Color(0xFF9575CD), // Lila oscuro
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo: Correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico", color = Color.White) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // Teclado con @
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

            // Campo: Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(), // Oculta el texto
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
                    viewModel.register(email, password, nombre) {
                        onRegisterSuccess() // Llama a callback cuando el registro es exitoso
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            // Mostrar errores si existen
            viewModel.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red)
            }
        }
    }
}
