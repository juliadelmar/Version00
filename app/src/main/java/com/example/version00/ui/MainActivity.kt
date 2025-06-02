package com.example.version00

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.example.version00.ui.navigation.AuthNavGraph
import com.example.version00.ui.theme.Version00Theme
import com.example.version00.ui.viewmodel.AuthViewModel
import com.example.version00.ui.viewmodel.FriendViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedTheme = sharedPref.getString("app_theme", "light") ?: "light" // por defecto claro
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        val viewModel = FriendViewModel()

        viewModel.obtenerSolicitudesRecibidas { solicitudes ->
            if (solicitudes.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Tienes ${solicitudes.size} solicitud(es) de amistad",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        setContent {
            val darkTheme = savedTheme == "dark"

            Version00Theme(darkTheme = darkTheme) {
                val navController = rememberNavController()
                AuthNavGraph(navController = navController, authViewModel = authViewModel)
            }
        }

    }
}
