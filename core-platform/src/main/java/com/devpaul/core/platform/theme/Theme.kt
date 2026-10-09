package com.devpaul.core.platform.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CvBlueDark,
    onPrimary = CvDarkBackground,
    primaryContainer = ColorBlue900,
    onPrimaryContainer = CvDarkText,
    secondary = CvDarkMuted,
    onSecondary = CvDarkBackground,
    tertiary = CvTerracottaDark,
    onTertiary = CvDarkBackground,
    background = CvDarkBackground,
    onBackground = CvDarkText,
    surface = CvDarkSurface,
    onSurface = CvDarkText,
    surfaceVariant = CvDarkSurfaceVariant,
    onSurfaceVariant = CvDarkMuted,
    outline = CvDarkOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = CvBlue,
    onPrimary = White,
    primaryContainer = ColorBlue100,
    onPrimaryContainer = ColorBlue950,
    secondary = CvInk,
    onSecondary = White,
    tertiary = CvTerracotta,
    onTertiary = White,
    background = CvLightBackground,
    onBackground = ColorBlue950,
    surface = CvLightSurface,
    onSurface = ColorBlue950,
    surfaceVariant = ColorBlue50,
    onSurfaceVariant = CvLightMuted,
    outline = CvLightOutline,
)

@Composable
fun CvFreeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Dynamic colors are intentionally ignored so the CV Free palette stays stable.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CvFreeTypography,
        content = content,
    )
}
