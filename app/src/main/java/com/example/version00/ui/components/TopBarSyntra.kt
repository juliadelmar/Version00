package com.example.version00.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.version00.ui.theme.Indices
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.version00.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSyntra(
    avatarUrl: String,
    onAvatarClick: () -> Unit
) {
    val isFilePath = avatarUrl.startsWith("/data") || avatarUrl.startsWith("file")

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Selenya",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onAvatarClick() }
            ) {
                AsyncImage(
                    model = if (isFilePath) File(avatarUrl).toURI().toString() else avatarUrl,
                    contentDescription = "Avatar del usuario",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(id = R.drawable.ic_cuerpo),
                    error = painterResource(id = R.drawable.ic_cuerpo)
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Acción futura */ }) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Indices,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.background(Indices)
    )
}

