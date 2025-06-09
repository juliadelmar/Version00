package com.example.version00.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Tu color personalizado
val PurpleCustom = Color(0xFFB58EDC)
val purleDark = Color(0xFFB58EDC)
// Esquema de colores para tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = purleDark,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    onPrimary = Color.White
)

// Esquema de colores para tema claro
private val LightColorScheme = lightColorScheme(
    primary = PurpleCustom,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    onPrimary = Color.White


)

@Composable
fun Version00Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
