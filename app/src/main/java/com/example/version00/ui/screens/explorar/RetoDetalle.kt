package com.example.version00.ui.screens.explorar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.version00.ui.model.Reto
import com.example.version00.ui.model.RetoPrefs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RetoDetalleScreen(retoId: Int, navController: NavController) {
    val context = LocalContext.current

    val reto = remember {
        listOf(
            Reto(1, "Sentadillas", "Haz tantas sentadillas como puedas", 100, gifUrl = ""),
            Reto(2, "Flexiones", "50 flexiones", 50, gifUrl = ""),
            Reto(3, "Plancha", "5 minutos", 300, gifUrl = ""),
            Reto(4, "Burpees", "Completa 30", 30, gifUrl = "")
        ).find { it.id == retoId }
    } ?: return

    var progreso by remember { mutableStateOf(RetoPrefs.getProgreso(context, retoId)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(reto.nombre) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(reto.descripcion, style = MaterialTheme.typography.bodyLarge)

            if (reto.gifUrl.isNotBlank()) {
                AsyncImage(
                    model = reto.gifUrl,
                    contentDescription = reto.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("GIF no disponible")
                }
            }

            LinearProgressIndicator(
                progress = (progreso.toFloat() / reto.objetivo).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )

            Text("$progreso / ${reto.objetivo}")

            Button(
                onClick = {
                    if (progreso < reto.objetivo) {
                        progreso++
                        RetoPrefs.setProgreso(context, retoId, progreso)
                    } else {
                        Toast.makeText(context, "¡Reto completado!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = progreso < reto.objetivo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sumar +1")
            }

            if (progreso >= reto.objetivo) {
                Text("✅ ¡Reto completado!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
