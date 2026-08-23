package com.urielhuerta.criterio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CriterioDarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = CriterioDarkSurfaceVariant,
    onPrimaryContainer = CriterioDarkTextPrimary,
    secondary = CriterioDarkSecondary,
    onSecondary = Color.Black,
    background = CriterioDarkBackground,
    onBackground = CriterioDarkTextPrimary,
    surface = CriterioDarkSurface,
    onSurface = CriterioDarkTextPrimary,
    surfaceVariant = CriterioDarkSurfaceVariant,
    onSurfaceVariant = CriterioDarkTextSecondary,
    outline = CriterioDarkBorder,
    error = RiskRed
)

private val LightColorScheme = lightColorScheme(
    primary = CriterioLightPrimary,
    onPrimary = Color.White,
    primaryContainer = CriterioLightSurfaceVariant,
    onPrimaryContainer = CriterioLightTextPrimary,
    secondary = CriterioLightSecondary,
    onSecondary = Color.White,
    background = CriterioLightBackground,
    onBackground = CriterioLightTextPrimary,
    surface = CriterioLightSurface,
    onSurface = CriterioLightTextPrimary,
    surfaceVariant = CriterioLightSurfaceVariant,
    onSurfaceVariant = CriterioLightTextSecondary,
    outline = CriterioLightBorder,
    error = RiskRed
)

@Composable
fun CriterioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
