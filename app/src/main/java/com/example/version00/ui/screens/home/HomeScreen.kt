package com.example.version00.ui.screens.home

import EjerciciosViewModel
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.version00.R
import com.example.version00.ui.components.BottomBarSyntra
import com.example.version00.ui.components.TopBarSyntra
import com.example.version00.ui.screens.actividades.ActividadesScreen
import com.example.version00.ui.screens.actividades.CalendarioEntrenamientoScreen
import com.example.version00.ui.screens.cuerpo.MedidasCorporalesScreen
import com.example.version00.ui.screens.explorar.*
import com.example.version00.ui.viewmodel.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(viewModel: AuthViewModel, navController: NavHostController) {
    val selectedIndex = remember { mutableStateOf(0) }

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    var avatarUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val localAvatar = sharedPrefs.getString("avatarPath", "") ?: ""
        avatarUrl = localAvatar
    }

    Scaffold(
        topBar = {
            TopBarSyntra(avatarUrl = avatarUrl) {
                navController.navigate("configuration")
            }
        },
        bottomBar = { BottomBarSyntra(selectedIndex, navController) },
        backgroundColor = MaterialTheme.colors.onBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.stars_with_transparency),
                contentDescription = "Fondo Estrellas",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
            )

            when (selectedIndex.value) {
                0 -> ContentBoxHome(
                    navController = navController,
                    onNavigateToTraining = {
                        navController.navigate("training/1")
                    }
                )

                1 -> ActividadesScreen(navController)

                2 -> {
                    val newsViewModel: NewsViewModel = viewModel()
                    val nutritionViewModel: NutritionViewModel = viewModel(factory = NutritionViewModelFactory(NutritionApiService.create()))
                    val supplementViewModel: SupplementViewModel = viewModel(
                        factory = SupplementViewModelFactory(
                            NutritionixRetrofitClient.api,
                            "694ca093",
                            "cdf3a7dba1923d02decfe5e39bdf3f"
                        )
                    )
                    ExploreScreen(
                        navController = navController,
                        newsViewModel = newsViewModel,
                        nutritionViewModel = nutritionViewModel,
                        supplementViewModel = supplementViewModel
                    )
                }

                3 -> {
                    val rutinaViewModel: RutinaFirebaseViewModel = viewModel()
                    val cicloViewModel: CalendarioMenstrualViewModel =
                        viewModel(factory = CalendarioMenstrualViewModel.CalendarioViewModelFactory(context))

                    var diasConEntrenamiento by remember { mutableStateOf(emptyList<LocalDate>()) }

                    LaunchedEffect(Unit) {
                        rutinaViewModel.obtenerHistorialFatiga { historial ->
                            diasConEntrenamiento = historial.mapNotNull {
                                try {
                                    LocalDate.parse(it.fecha.substringBefore(" "))
                                } catch (e: Exception) {
                                    null
                                }
                            }.distinct()
                        }
                    }

                    CalendarioEntrenamientoScreen(
                        navController = navController,
                        diasConEntrenamiento = diasConEntrenamiento,
                        cicloViewModel = cicloViewModel
                    )
                }

                4 -> MedidasCorporalesScreen(navController)
            }
        }
    }
}
