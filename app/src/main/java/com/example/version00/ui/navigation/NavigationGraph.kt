package com.example.version00.ui.navigation

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.version00.ui.screens.auth.LoginScreen
import com.example.version00.ui.screens.auth.RegisterScreen
import com.example.version00.ui.screens.configuration.ConfigurationScreen
import com.example.version00.ui.screens.home.ContentTrainingScreen
import com.example.version00.ui.screens.home.HomeScreen
import com.example.version00.ui.screens.rutina.DetalleEjercicioScreen
// import com.example.version00.ui.screens.rutina.EjerciciosScreen
import com.example.version00.ui.screens.rutina.ElegirEjercicioScreen
import com.example.version00.ui.screens.rutina.ListaEjerciciosScreen
import com.example.version00.ui.screens.rutina.RutinaDetailScreen
import com.example.version00.ui.viewmodel.AuthViewModel
import com.example.version00.ui.viewmodel.EjerciciosViewModel
// Asegúrate de importar EditEjercicioRutinaScreen
import com.example.version00.ui.screens.rutina.EditEjercicioRutinaScreen

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val HOME_ROUTE = "home"
    const val CONFIGURATION_ROUTE = "configuration"
    const val RUTINA_DETAIL_ROUTE = "rutinaDetail"
    const val TRAINING_ROUTE = "training"
    const val LISTA_EJERCICIOS_ROUTE = "ejercicios"
    const val ELEGIR_EJERCICIO_ROUTE = "elegirEjercicio"
    const val EDIT_EJERCICIO_RUTINA_ROUTE = "edit_ejercicio_rutina" // Ya lo tienes definido, ¡bien!

    // const val EJERCICIO_DETAIL_ROUTE = "detalle"
}

@RequiresApi(35) // Considera si esta anotación es realmente necesaria para todo el NavGraph
@Composable
fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = AppDestinations.LOGIN_ROUTE) {

        // ... (tus rutas existentes de LOGIN_ROUTE, REGISTER_ROUTE, HOME_ROUTE, CONFIGURATION_ROUTE) ...
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                authViewModel,
                onLoginSuccess = {
                    navController.navigate(AppDestinations.HOME_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                authViewModel,
                onRegisterSuccess = {
                    navController.navigate(AppDestinations.HOME_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(authViewModel, navController = navController)
        }

        composable(AppDestinations.CONFIGURATION_ROUTE) {
            ConfigurationScreen(authViewModel, navController = navController)
        }


        composable(
            route = "${AppDestinations.RUTINA_DETAIL_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            RutinaDetailScreen(rutinaId = rutinaId, navController = navController)
        }

        composable(
            route = "${AppDestinations.TRAINING_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            ContentTrainingScreen(rutinaId = rutinaId, navController = navController)
        }

        composable(
            route = "${AppDestinations.LISTA_EJERCICIOS_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejerciciosViewModel: EjerciciosViewModel = viewModel()
            LaunchedEffect(Unit) {
                ejerciciosViewModel.cargarEjercicios()
            }
            ListaEjerciciosScreen(
                viewModel = ejerciciosViewModel,
                rutinaIdContext = rutinaId,
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "detalleEjercicio/{rutinaId}/{ejercicioId}", // Esta es para añadir un NUEVO ejercicio
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("ejercicioId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejercicioId = backStackEntry.arguments?.getInt("ejercicioId") ?: -1
            DetalleEjercicioScreen(rutinaId, ejercicioId, navController)
        }


        composable(AppDestinations.ELEGIR_EJERCICIO_ROUTE) {
            ElegirEjercicioScreen(
                onMusculoSeleccionado = { musculo ->
                    // Lógica de navegación
                },
                navController = navController
            )
        }

        // ===== ¡AQUÍ ES DONDE DEBES AÑADIR LA NUEVA RUTA! =====
        composable(
            route = "${AppDestinations.EDIT_EJERCICIO_RUTINA_ROUTE}/{rutinaId}/{ejercicioId}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("ejercicioId") { type = NavType.IntType } // Este es el EjercicioGuardado.id
            )
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejercicioId = backStackEntry.arguments?.getInt("ejercicioId") ?: -1

            // Asegúrate de que los IDs son válidos antes de llamar a la pantalla
            if (rutinaId != -1 && ejercicioId != -1) {
                EditEjercicioRutinaScreen(
                    rutinaId = rutinaId,
                    ejercicioId = ejercicioId,
                    navController = navController
                    // El RutinaFirebaseViewModel se instanciará dentro de EditEjercicioRutinaScreen
                    // usando androidx.lifecycle.viewmodel.compose.viewModel() por defecto.
                )
            } else {
                // Manejar el caso de IDs inválidos, quizás mostrando un error o volviendo atrás.
                // Por ahora, podría ser un Log y no hacer nada, o popBackStack.
                Log.e("AuthNavGraph", "IDs inválidos para EDIT_EJERCICIO_RUTINA_ROUTE: rutinaId=$rutinaId, ejercicioId=$ejercicioId")
                // navController.popBackStack() // Opcional: volver si los IDs son malos
            }
        }
        // ========================================================

    }
}