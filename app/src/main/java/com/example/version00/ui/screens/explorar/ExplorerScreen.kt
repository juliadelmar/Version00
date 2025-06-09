package com.example.version00.ui.screens.explorar

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.version00.ui.model.*
import com.example.version00.ui.viewmodel.*

@Composable
fun ExploreScreen(
    navController: NavController,
    newsViewModel: NewsViewModel,
    nutritionViewModel: NutritionViewModel,
    supplementViewModel: SupplementViewModel
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val articles by newsViewModel.articles.collectAsState(initial = emptyList())
    val meals by nutritionViewModel.meals.collectAsState()
    val supplements by supplementViewModel.supplements.collectAsState()
    val isLoadingSupplements by supplementViewModel.isLoading.collectAsState()
    val errorSupplements by supplementViewModel.error.collectAsState()

    // Inicializa ViewModels
    LaunchedEffect(Unit) {
        newsViewModel.fetchNews()
        nutritionViewModel.loadMeals()
        supplementViewModel.loadSupplements()
    }

    val retos = remember {
        listOf(
            Reto(1, "Sentadillas", "Haz 50 sentadillas hoy", 50, ""),
            Reto(2, "Planchas", "5 minutos acumulados", 300, ""),
            Reto(3, "Burpees", "Completa 30 burpees", 30, ""),
            Reto(4, "Correr", "Corre 3km esta semana", 3000, "")
        )
    }

    val progresos = remember {
        retos.associate { reto ->
            reto.id to mutableStateOf(RetoPrefs.getProgreso(context, reto.id))
        }
    }

    // FONDO oscuro o claro
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 🔹 Noticias
            Text("Noticias", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(articles.take(5)) { article ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .clickable { uriHandler.openUri(article.url) },
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            article.image?.let {
                                Image(
                                    painter = rememberAsyncImagePainter(it),
                                    contentDescription = article.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                            Text(article.title, style = MaterialTheme.typography.titleMedium, maxLines = 3)
                            article.description?.let {
                                Spacer(Modifier.height(4.dp))
                                Text(it, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Nutrición
            Text("Nutrición", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(meals.take(5)) { meal ->
                    Card(
                        modifier = Modifier.width(260.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Row(Modifier.padding(12.dp)) {
                            Image(
                                painter = rememberAsyncImagePainter(meal.thumbnail),
                                contentDescription = meal.name,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(MaterialTheme.shapes.medium),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(meal.name, style = MaterialTheme.typography.titleMedium, maxLines = 2)
                                meal.category?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Suplementación
            Text("Suplementación", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            when {
                isLoadingSupplements -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                errorSupplements != null -> Text("Error: $errorSupplements", color = MaterialTheme.colorScheme.error)
                else -> LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(supplements) { item ->
                        when (item) {
                            is SupplementCommon -> SupplementCard(
                                item.food_name,
                                "${item.serving_qty} ${item.serving_unit}",
                                item.nf_calories,
                                item.photo?.thumb ?: ""
                            )
                            is SupplementBranded -> SupplementCard(
                                "${item.brand_name} - ${item.food_name}",
                                "${item.serving_qty} ${item.serving_unit}",
                                item.nf_calories,
                                item.photo?.thumb ?: ""
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Retos
            Text("Retos", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(retos) { reto ->
                    val progreso = progresos[reto.id] ?: remember { mutableStateOf(0) }

                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .height(140.dp)
                            .clickable {
                                navController.navigate("reto_detalle/${reto.id}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(reto.nombre, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(reto.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                            Spacer(Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = (progreso.value.toFloat() / reto.objetivo).coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("${progreso.value} / ${reto.objetivo}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupplementCard(
    name: String,
    serving: String,
    calories: Double,
    imageUrl: String
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(140.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = name,
                modifier = Modifier
                    .size(96.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Porción: $serving",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Calorías: ${calories.toInt()} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}