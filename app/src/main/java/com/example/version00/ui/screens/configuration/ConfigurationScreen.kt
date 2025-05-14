package com.example.version00.ui.screens.configuration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.components.ButtomSalir
import com.example.version00.ui.theme.Indices
import com.example.version00.ui.viewmodel.AuthViewModel

@Composable
fun ConfigurationScreen(viewModel: AuthViewModel, navController: NavHostController) {
    var avatarUrl by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario { nombreFetched, emailFetched, urlFetched ->
            nombre = nombreFetched
            email = emailFetched
            avatarUrl = urlFetched
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // TopBar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.White,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 8.dp)
            )
            Text("Mi Cuenta", fontSize = 20.sp, color = Color.White)
        }

        // Perfil
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(nombre, color = Color.White, fontSize = 18.sp)
                Text(email, color = Color.Gray, fontSize = 14.sp)
            }
        }

        // Tarjetas moradas

         Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = Indices, shape = RoundedCornerShape(20.dp))
                    .padding(8.dp)
                    .padding(bottom = 16.dp)
            )



        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = Indices, shape = RoundedCornerShape(20.dp))
                .padding(8.dp)
                .padding(bottom = 16.dp)
        )



        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color = Indices, shape = RoundedCornerShape(20.dp))
                .padding(8.dp)
                .padding(bottom = 16.dp)
        )



        Spacer(modifier = Modifier.height(30.dp))
        Spacer(modifier = Modifier.height(30.dp))
        ButtomSalir(navController = navController)
    }
}
