package com.example.version00.ui.navigation

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.version00.ui.screens.ActividadesScreen
import com.example.version00.ui.screens.auth.LoginScreen
import com.example.version00.ui.screens.auth.RegisterScreen
import com.example.version00.ui.screens.configuration.ConfigurationScreen
import com.example.version00.ui.screens.home.HomeScreen
import com.example.version00.ui.screens.rutina.DetalleEjercicioScreen
import com.example.version00.ui.screens.rutina.EditEjercicioRutinaScreen
import com.example.version00.ui.screens.rutina.ElegirEjercicioScreen
import com.example.version00.ui.screens.rutina.ListaEjerciciosScreen
import com.example.version00.ui.screens.rutina.RutinaDetailScreen
import com.example.version00.ui.screens.rutina.RutinaScreen
import com.example.version00.ui.viewmodel.AuthViewModel
import com.example.version00.ui.viewmodel.EjerciciosViewModel
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel

object AppDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val HOME_ROUTE = "home"
    const val CONFIGURATION_ROUTE = "configuration"
    const val RUTINA_DETAIL_ROUTE = "rutinaDetail"
    const val TRAINING_ROUTE = "training"
    const val LISTA_EJERCICIOS_ROUTE = "ejercicios"
    const val ELEGIR_EJERCICIO_ROUTE = "elegirEjercicio"
    const val EDIT_EJERCICIO_RUTINA_ROUTE = "edit_ejercicio_rutina"
    const val ACTIVIDADES_ROUTE = "actividades"

}

@RequiresApi(35)
@Composable
fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = AppDestinations.LOGIN_ROUTE) {

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
        composable(AppDestinations.ACTIVIDADES_ROUTE) {
            ActividadesScreen(navController = navController)
        }


        // Pantalla para ejecutar la rutina
        composable(
            route = "${AppDestinations.TRAINING_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val rutinaViewModel: RutinaFirebaseViewModel = viewModel()

            var ejercicios by remember { mutableStateOf(emptyList<com.example.version00.ui.model.EjercicioGuardado>()) }
            var rutinaNombre by remember { mutableStateOf("Rutina $rutinaId") }

            LaunchedEffect(rutinaId) {
                rutinaViewModel.obtenerEjerciciosDeRutina(rutinaId) { lista ->
                    ejercicios = lista
                }

                rutinaViewModel.obtenerRutinaPorId(rutinaId) { rutina ->
                    rutina?.let { rutinaNombre = it.nombre }
                }
            }

            RutinaScreen(
                rutinaId = rutinaId,
                ejercicios = ejercicios,
                navController = navController,
                rutinaNombre = rutinaNombre
            )
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
            route = "detalleEjercicio/{rutinaId}/{ejercicioId}",
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
                    // Implementa la lógica si es necesario
                },
                navController = navController
            )
        }

        composable(
            route = "${AppDestinations.EDIT_EJERCICIO_RUTINA_ROUTE}/{rutinaId}/{ejercicioId}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("ejercicioId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejercicioId = backStackEntry.arguments?.getInt("ejercicioId") ?: -1

            if (rutinaId != -1 && ejercicioId != -1) {
                EditEjercicioRutinaScreen(
                    rutinaId = rutinaId,
                    ejercicioId = ejercicioId,
                    navController = navController
                )
            } else {
                Log.e("AuthNavGraph", "IDs inválidos para editar ejercicio: rutinaId=$rutinaId, ejercicioId=$ejercicioId")
            }
        }
    }
}
