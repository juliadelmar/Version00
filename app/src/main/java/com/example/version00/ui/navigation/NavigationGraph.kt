// Archivo de navegación principal de la app
package com.example.version00.ui.navigation

// Importaciones necesarias para Jetpack Compose Navigation y ViewModel
import EjerciciosViewModel
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.version00.ui.components.RutinaTemporalHolder
import com.example.version00.ui.model.EjercicioGuardado
import com.example.version00.ui.screens.SplashScreen

// Importaciones de pantallas
import com.example.version00.ui.screens.actividades.*
import com.example.version00.ui.screens.auth.*
import com.example.version00.ui.screens.configuration.*
import com.example.version00.ui.screens.cuerpo.*
import com.example.version00.ui.screens.explorar.ExploreScreen
import com.example.version00.ui.screens.explorar.NutritionixRetrofitClient
import com.example.version00.ui.screens.explorar.RetoDetalleScreen
import com.example.version00.ui.screens.explorar.SupplementViewModel
import com.example.version00.ui.screens.explorar.SupplementViewModelFactory
import com.example.version00.ui.screens.home.*
import com.example.version00.ui.screens.rutina.*
import com.example.version00.ui.viewmodel.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import java.time.LocalDate
import java.time.YearMonth

// Objeto que contiene las rutas utilizadas en la navegación
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
    const val PREDEFINED_TRAINING_ROUTE = "predefined_routine"
    const val PREDEFINED_TRAINING_FULL_ROUTE = "$PREDEFINED_TRAINING_ROUTE/{faseIndex}/{semanaIndex}/{diaIndex}"

}

// Función composable que define el gráfico de navegación principal de la app
@RequiresApi(35)
@Composable
fun AuthNavGraph(navController: NavHostController, authViewModel: AuthViewModel) {
    val nutritionApiService = remember { NutritionApiService.create() }
    val nutritionViewModelFactory = remember { NutritionViewModelFactory(nutritionApiService) }
    val newsViewModel: NewsViewModel = viewModel()
    val apiService = NutritionixRetrofitClient.api
    val appId = "694ca093"  // x-app-id real
    val apiKey = "cdf3a7dba1923d02decfe5e39bdf3f"  // x-app-key real

    val supplementViewModel: SupplementViewModel = viewModel(
        factory = SupplementViewModelFactory(apiService, appId, apiKey)
    )

    val nutritionViewModel: NutritionViewModel = viewModel(factory = nutritionViewModelFactory)
    NavHost(navController = navController, startDestination = AppDestinations.SPLASH_ROUTE) {

        // Splash inicial, decide a dónde navegar
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

        // Pantalla de login
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(authViewModel, onLoginSuccess = {
                navController.navigate(AppDestinations.HOME_ROUTE) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
            }, navController)
        }

        // Registro
        composable(AppDestinations.REGISTER_ROUTE) {
            RegisterScreen(authViewModel, onRegisterSuccess = {
                navController.navigate(AppDestinations.HOME_ROUTE) {
                    popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true }
                }
            }, navController)
        }

        // Pantalla principal tras logueo
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(authViewModel, navController)
        }

        // Configuración
        composable(AppDestinations.CONFIGURATION_ROUTE) {
            ConfigurationScreen(authViewModel, navController)
        }

        // Detalle de rutina con argumentos
        composable("rutinaDetail/{rutinaId}/{rutinaNombre}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("rutinaNombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val rutinaNombre = backStackEntry.arguments?.getString("rutinaNombre") ?: "Sin nombre"
            RutinaDetailScreen(rutinaId, rutinaNombre, navController)
        }

        // Actividades (fatiga e histórico)
        composable(AppDestinations.ACTIVIDADES_ROUTE) {
            ActividadesScreen(navController)
        }

        // Medidas corporales
        composable(AppDestinations.CUERPO_ROUTE) {
            MedidasCorporalesScreen(navController)
        }

        // Calendario menstrual con fatiga integrada
        composable(AppDestinations.REGLA_ROUTE) {
            val rutinaViewModel: RutinaFirebaseViewModel = viewModel()
            val cicloViewModel: CalendarioMenstrualViewModel =
                viewModel(factory = CalendarioMenstrualViewModel.CalendarioViewModelFactory(
                    LocalContext.current.applicationContext
                )
                )

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
                cicloViewModel = cicloViewModel
            )

        }
        composable("reto_detalle/{retoId}") { backStackEntry ->
            val retoId = backStackEntry.arguments?.getString("retoId")?.toIntOrNull() ?: return@composable
            RetoDetalleScreen(retoId, navController = navController)
        }

        // Buscar amigos
        composable(AppDestinations.AMIGOS_ROUTE) {
            ExploreScreen(
                navController = navController,
                newsViewModel = newsViewModel,
                nutritionViewModel = nutritionViewModel,
                supplementViewModel = supplementViewModel
            )
        }
        composable(
            route = AppDestinations.PREDEFINED_TRAINING_FULL_ROUTE,
            arguments = listOf(
                navArgument("faseIndex") { type = NavType.IntType },
                navArgument("semanaIndex") { type = NavType.IntType },
                navArgument("diaIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val faseIndex = backStackEntry.arguments?.getInt("faseIndex") ?: 0
            val semanaIndex = backStackEntry.arguments?.getInt("semanaIndex") ?: 0
            val diaIndex = backStackEntry.arguments?.getInt("diaIndex") ?: 0

            RutinaPredefinidaScreen(
                rutinaId = RutinaTemporalHolder.rutinaId,
                rutinaNombre = RutinaTemporalHolder.rutinaNombre,
                ejercicios = RutinaTemporalHolder.ejercicios,
                navController = navController,
                rutinaViewModel = viewModel(),
                faseIndex = faseIndex,
                semanaIndex = semanaIndex,
                diaIndex = diaIndex
            )
        }








        // Ejecución de rutina
        composable("${AppDestinations.TRAINING_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val rutinaViewModel: RutinaFirebaseViewModel = viewModel()
            val cicloViewModel: CalendarioMenstrualViewModel = viewModel(factory = CalendarioMenstrualViewModel.CalendarioViewModelFactory(
                LocalContext.current
            )
            )

            var ejercicios by remember { mutableStateOf(emptyList<com.example.version00.ui.model.EjercicioGuardado>()) }
            var rutinaNombre by remember { mutableStateOf("Rutina $rutinaId") }

            LaunchedEffect(rutinaId) {
                rutinaViewModel.obtenerEjerciciosDeRutina(rutinaId) { ejercicios = it }
                rutinaViewModel.obtenerRutinaPorId(rutinaId) { rutina -> rutina?.let { rutinaNombre = it.nombre } }
            }

            RutinaScreen(rutinaId, ejercicios, navController, rutinaNombre, cicloViewModel, rutinaViewModel)
        }

        composable("rutinaPredefinida") {
            ContentTrainingScreen(rutinaId = 0, navController = navController)
        }

        // Lista de ejercicios disponibles para agregar
        composable("${AppDestinations.LISTA_EJERCICIOS_ROUTE}/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejerciciosViewModel: EjerciciosViewModel = viewModel()

            LaunchedEffect(Unit) { ejerciciosViewModel.cargarEjercicios() }

            ListaEjerciciosScreen(ejerciciosViewModel, rutinaId, navController) {
                navController.popBackStack()
            }
        }

        // Detalle individual de ejercicio
        composable("detalleEjercicio/{rutinaId}/{ejercicioId}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("ejercicioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejercicioId = backStackEntry.arguments?.getInt("ejercicioId") ?: -1
            DetalleEjercicioScreen(rutinaId, ejercicioId, navController)
        }

        // Elegir ejercicio por grupo muscular (no usa argumentos)
        composable(AppDestinations.ELEGIR_EJERCICIO_ROUTE) {
            ElegirEjercicioScreen(onMusculoSeleccionado = {}, navController)
        }

        // Editar ejercicio ya agregado a una rutina
        composable("${AppDestinations.EDIT_EJERCICIO_RUTINA_ROUTE}/{rutinaId}/{ejercicioId}",
            arguments = listOf(
                navArgument("rutinaId") { type = NavType.IntType },
                navArgument("ejercicioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: -1
            val ejercicioId = backStackEntry.arguments?.getInt("ejercicioId") ?: -1

            if (rutinaId != -1 && ejercicioId != -1) {
                EditEjercicioRutinaScreen(rutinaId, ejercicioId, navController)
            } else {
                Log.e("AuthNavGraph", "IDs inválidos para editar ejercicio: rutinaId=$rutinaId, ejercicioId=$ejercicioId")
            }
        }
    }
}