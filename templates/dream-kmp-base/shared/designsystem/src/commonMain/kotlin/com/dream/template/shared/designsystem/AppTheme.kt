package com.dream.template.shared.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = DreamPrimary,
    secondary = DreamSecondary,
    surface = DreamSurfaceLight
)

private val DarkColors = darkColorScheme(
    primary = DreamPrimary,
    secondary = DreamSecondary,
    surface = DreamSurfaceDark
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = DreamTypography,
        content = content
    )
}
