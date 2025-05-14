package com.example.version00

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels // Para by viewModels()
import androidx.compose.material3.MaterialTheme // Asegúrate de tener un tema M3
import androidx.navigation.compose.rememberNavController
import com.example.version00.ui.navigation.AuthNavGraph
import com.example.version00.ui.theme.Version00Theme // Asumiendo que tienes un tema llamado así
import com.example.version00.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    // Inyección de ViewModel recomendada
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Version00Theme { // Envuelve tu app con tu tema de Material 3
                val navController = rememberNavController()
                AuthNavGraph(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}