package com.example.version00.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable que muestra un botón "Salir" y un diálogo de confirmación para cerrar sesión.
 * Al confirmar, cierra sesión en Firebase y navega al login.
 */
@Composable
fun ButtomSalir(navController: NavHostController) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Si se activa, se muestra el diálogo de confirmación
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = {
                    FirebaseAuth.getInstance().signOut() // Cierra sesión en Firebase
                    navController.navigate("login") {   // Navega al login eliminando home del backstack
                        popUpTo("home") { inclusive = true }
                    }
                    mostrarDialogo = false
                }) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            title = { Text("Cerrar sesión", color = Color.White) },
            text = { Text("¿Estás segura de que quieres cerrar sesión?", color = Color.LightGray) },
            backgroundColor = Color(0xFF222222) // Fondo oscuro del diálogo
        )
    }

    // Botón visible en la pantalla que activa el diálogo al pulsarlo
    Box(modifier = Modifier.fillMaxWidth()) {
        TextButton(
            onClick = { mostrarDialogo = true },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Salir", color = Color.White, fontSize = 18.sp)
        }
    }
}
