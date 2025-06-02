package com.example.version00.ui.navigation

import EjerciciosViewModel
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.version00.ui.screens.actividades.ActividadesScreen
import com.example.version00.ui.screens.SplashScreen
import com.example.version00.ui.screens.actividades.CalendarioEntrenamientoScreen
import com.example.version00.ui.screens.actividades.EntradaHistorial
import com.example.version00.ui.screens.auth.LoginScreen
import com.example.version00.ui.screens.auth.RegisterScreen
import com.example.version00.ui.screens.configuration.ConfigurationScreen
import com.example.version00.ui.screens.cuerpo.MedidasCorporalesScreen
import com.example.version00.ui.screens.home.HomeScreen
import com.example.version00.ui.screens.rutina.BuscarUsuariosScreen
import com.example.version00.ui.screens.rutina.DetalleEjercicioScreen
import com.example.version00.ui.screens.rutina.EditEjercicioRutinaScreen
import com.example.version00.ui.screens.rutina.ElegirEjercicioScreen
import com.example.version00.ui.screens.rutina.ListaEjerciciosScreen
import com.example.version00.ui.screens.rutina.RutinaDetailScreen
import com.example.version00.ui.screens.rutina.RutinaScreen
import com.example.version00.ui.viewmodel.AuthViewModel
import com.example.version00.ui.viewmodel.CalendarioMenstrualViewModel
import com.example.version00.ui.viewmodel.CalendarioViewModelFactory
import com.example.version00.ui.viewmodel.RutinaFirebaseViewModel
import java.time.LocalDate
import java.time.YearMonth

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
    const val SPLASH_ROUTE = "splash"
    const val CUERPO_ROUTE = "cuerpo"
    const val REGLA_ROUTE = "regla"
    const val AMIGOS_ROUTE = "amigos"



}

@RequiresApi(35)
@Composable
fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController = navController, startDestination = AppDestinations.SPLASH_ROUTE) {
        composable(AppDestinations.SPLASH_ROUTE) {
            SplashScreen(
                navController = navController,

                onLoginSuccess = {
                    navController.navigate(AppDestinations.HOME_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                },
                authViewModel,

            )
        }
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
            route = "rutinaDetail/{rutinaId}/{rutinaNombre}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("rutinaNombre") { type = NavType.StringType }
            )
        )
{ backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val rutinaNombre = backStackEntry.arguments?.getString("rutinaNombre") ?: "Sin nombre"

            RutinaDetailScreen(
                rutinaId = rutinaId,
                rutinaNombre = rutinaNombre,
                navController = navController
            )
        }



        composable(AppDestinations.ACTIVIDADES_ROUTE) {
            ActividadesScreen(navController = navController)
        }
        composable(AppDestinations.CUERPO_ROUTE) {
            MedidasCorporalesScreen(navController = navController)
        }
        composable(AppDestinations.REGLA_ROUTE) {
            val rutinaViewModel: RutinaFirebaseViewModel = viewModel()
            val cicloViewModel: CalendarioMenstrualViewModel = viewModel(factory = CalendarioViewModelFactory(LocalContext.current.applicationContext))

            var historial by remember { mutableStateOf(emptyList<EntradaHistorial>()) }
            var diasConEntrenamiento by remember { mutableStateOf(emptyList<LocalDate>()) }

            LaunchedEffect(Unit) {
                rutinaViewModel.obtenerHistorialFatiga { entradas ->
                    historial = entradas
                    diasConEntrenamiento = entradas.mapNotNull {
                        try {
                            LocalDate.parse(it.fecha.substringBefore(" "))
                        } catch (e: Exception) { null }
                    }.distinct()
                }
            }

            CalendarioEntrenamientoScreen(
                navController = navController,
                diasConEntrenamiento = diasConEntrenamiento,
                obtenerHistorialParaFecha = { fecha ->
                    historial.filter { it.fecha.startsWith(fecha.toString()) }
                        .map { it.rutinaNombre ?: "Sin nombre" } // Puedes cambiarlo según tu modelo
                },
                cicloViewModel = cicloViewModel
            )
        }



        composable(AppDestinations.AMIGOS_ROUTE) {
            BuscarUsuariosScreen(navController = navController)
        }






        // Pantalla para ejecutar la rutina
        composable(
            route = "${AppDestinations.TRAINING_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val rutinaViewModel: RutinaFirebaseViewModel = viewModel()
            val cicloViewModel: CalendarioMenstrualViewModel = viewModel(factory = CalendarioViewModelFactory(LocalContext.current))

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
                rutinaNombre = rutinaNombre,
                cicloViewModel = cicloViewModel,
                rutinaViewModel = rutinaViewModel
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
