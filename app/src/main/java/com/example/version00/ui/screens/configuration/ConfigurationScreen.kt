package com.example.version00.ui.screens.configuration

import android.content.Context
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.components.ButtomSalir
import com.example.version00.ui.theme.Indices
import com.example.version00.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ConfigurationScreen(viewModel: AuthViewModel, navController: NavHostController) {
    var avatarUrl by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val savedTheme = sharedPref.getString("app_theme", "system") ?: "system"
    var currentTheme by remember { mutableStateOf(savedTheme) }
    val scope = rememberCoroutineScope()

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
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 8.dp)
            )
            Text("Mi Cuenta", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }

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
                Text(nombre, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp)
                Text(email, color = Color.Gray, fontSize = 14.sp)
            }
        }

        Text("Tema de la aplicación", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))

        listOf("light", "dark").forEach { themeOption ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        currentTheme = themeOption
                        scope.launch {
                            sharedPref.edit().putString("app_theme", themeOption).apply()
                            (context as? ComponentActivity)?.recreate() // Recarga la actividad para aplicar el tema
                        }
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Indices)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentTheme == themeOption,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (themeOption == "light") "Claro" else "Oscuro",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        ButtomSalir(navController = navController)
    }
}
