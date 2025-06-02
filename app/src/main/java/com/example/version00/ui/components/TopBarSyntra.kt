package com.example.version00.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.version00.ui.theme.Indices

/**
 * Barra superior personalizada (TopAppBar) para la app.
 * Incluye avatar, título y un icono de correo.
 *
 * @param avatarUrl URL de la imagen del usuario.
 * @param onAvatarClick Acción al hacer clic sobre el avatar.
 */
@Composable
fun TopBarSyntra(avatarUrl: String, onAvatarClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Indices, // Color definido en el tema
        contentColor = Color.Black,
        elevation = 0.dp // Sin sombra
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Avatar dentro de un Card redondo
            Card(
                shape = CircleShape,
                elevation = 8.dp,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onAvatarClick() }
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(100.dp)
                )
            }

            // Nombre de usuario
            Text(
                text = "Selenya", // Se puede hacer dinámico
                fontSize = 24.sp
            )

            // Icono de mensajes (decorativo)
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Mensajes",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
