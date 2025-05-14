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

@Composable
fun TopBarSyntra(avatarUrl: String,  onAvatarClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Indices,
        contentColor = Color.Black,
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
            Text(
                text = "Selenya",
                fontSize = 24.sp
            )
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Mensajes",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
