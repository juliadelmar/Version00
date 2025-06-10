package com.example.version00.ui.screens.configuration

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.ui.components.ButtomSalir
import com.example.version00.ui.viewmodel.AuthViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ConfigurationScreen(
    viewModel: AuthViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val activity = LocalContext.current as? Activity
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var avatarPath by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("Usuario") }
    var email by remember { mutableStateOf("usuario@email.com") }
    var themeMode by remember { mutableStateOf(sharedPref.getString("app_theme", "system") ?: "system") }

    LaunchedEffect(Unit) {
        avatarPath = sharedPref.getString("avatarPath", "") ?: ""
        viewModel.cargarDatosUsuario { nombreFetched, emailFetched, _ ->
            nombre = nombreFetched
            email = emailFetched
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val filePath = saveImageToInternalStorage(context, it)
            filePath?.let { path ->
                sharedPref.edit().putString("avatarPath", path).apply()
                avatarPath = path
                Toast.makeText(context, "Imagen actualizada ✅", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 🔙 Back + título
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(end = 8.dp)
            )
            Text("Mi Cuenta", fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 👤 Avatar editable
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .clickable { imagePickerLauncher.launch("image/*") }
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (avatarPath.isNotEmpty() && File(avatarPath).exists()) {
                AsyncImage(
                    model = File(avatarPath).toURI().toString(),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("👤", fontSize = 42.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Datos de usuario
        Text(nombre, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
        Text(email, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))

        // ✏️ Botón cambiar nombre
        ElevatedButton(
            onClick = {
                Toast.makeText(context, "Función para cambiar nombre (próximamente)", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.elevatedButtonColors()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cambiar nombre")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ✏️ Botón cambiar contraseña
        ElevatedButton(
            onClick = {
                Toast.makeText(context, "Función para cambiar contraseña (próximamente)", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.elevatedButtonColors()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cambiar contraseña")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Tema de la aplicación", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)

        // 🌞🌚 Switch de tema
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Claro", color = MaterialTheme.colorScheme.onBackground)
            Switch(
                checked = themeMode == "dark",
                onCheckedChange = {
                    themeMode = if (it) "dark" else "light"
                    sharedPref.edit().putString("app_theme", themeMode).apply()
                    activity?.recreate()
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary
                )
            )
            Text("Oscuro", color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 🔓 Botón cerrar sesión
        ButtomSalir(navController)
    }
}

// 🔐 Función para guardar imagen localmente
fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_image.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
