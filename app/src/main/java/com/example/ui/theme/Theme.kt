package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = GrudexYellow,
    onPrimary = GrudexBlack,
    primaryContainer = GrudexYellowDark,
    onPrimaryContainer = GrudexBlack,
    secondary = GrudexYellowLight,
    onSecondary = GrudexBlack,
    background = GrudexBlack,
    onBackground = Color.White,
    surface = GrudexDark,
    onSurface = Color.White,
    surfaceVariant = GrudexSurfaceDark,
    onSurfaceVariant = Color.LightGray,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GrudexYellow,
    onPrimary = GrudexBlack,
    primaryContainer = GrudexYellowContainer,
    onPrimaryContainer = GrudexBlack,
    secondary = GrudexBlack,
    onSecondary = Color.White,
    background = Color(0xFFF9F9FB),
    onBackground = GrudexBlack,
    surface = Color.White,
    onSurface = GrudexBlack,
    surfaceVariant = GrudexLightGrey,
    onSurfaceVariant = GrudexDark,
    error = GrudexRed,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

