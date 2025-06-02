package com.example.version00.ui.screens.rutina

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.version00.R
import com.example.version00.ui.viewmodel.FriendViewModel
import com.example.version00.ui.viewmodel.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun BuscarUsuariosScreen(
    navController: NavHostController,
    viewModel: FriendViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var query by remember { mutableStateOf("") }
    var todosLosUsuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var resultados by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var usuarioActual by remember { mutableStateOf<Usuario?>(null) }

    // ✅ Obtener datos del usuario actual desde Firebase Realtime
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("usuarios").child(uid)
            .get()
            .addOnSuccessListener { snap ->
                val nombre = snap.child("nombre").getValue(String::class.java) ?: ""
                val email = snap.child("email").getValue(String::class.java) ?: ""
                val avatarUrl = snap.child("avatarUrl").getValue(String::class.java) ?: ""
                usuarioActual = Usuario(uid, nombre, email, avatarUrl)
            }

        viewModel.obtenerTodosLosUsuarios { lista ->
            val filtrados = lista.filter { it.uid != uid }
            todosLosUsuarios = filtrados
            resultados = filtrados
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                resultados = if (query.isBlank()) {
                    todosLosUsuarios
                } else {
                    todosLosUsuarios.filter {
                        it.nombre.contains(query, ignoreCase = true)
                    }
                }
            },
            label = { Text("Buscar usuario por nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(resultados) { usuario ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = usuario.avatarUrl,
                            contentDescription = usuario.nombre,
                            placeholder = painterResource(R.drawable.ic_mancuerna_oblicua),
                            error = painterResource(R.drawable.ic_mancuerna_oblicua),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(50))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(usuario.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(usuario.email, style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                usuarioActual?.let { actual ->
                                    viewModel.enviarSolicitudAmistad(
                                        uidDestino = usuario.uid,
                                        usuarioEmisor = actual
                                    ) { success ->
                                        Toast.makeText(
                                            context,
                                            if (success) "Solicitud enviada" else "Error al enviar",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .height(36.dp)
                        ) {
                            Text("Añadir amigo")
                        }
                    }
                }
            }
        }
    }
}
