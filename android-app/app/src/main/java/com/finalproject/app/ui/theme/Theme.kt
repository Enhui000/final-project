package com.finalproject.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    tertiary = AccentOrange,
    onTertiary = Color.Black,
    background = Color.White,
    onBackground = Color(0xFF1C1C1C),
    surface = SurfaceGray,
    onSurface = Color(0xFF1C1C1C)
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    tertiary = AccentOrange,
    onTertiary = Color.Black
)

@Composable
fun FinalProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
