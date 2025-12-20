package com.jose.pantrypal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PantryPrimaryDark,
    onPrimary = PantryOnPrimaryDark,
    primaryContainer = PantryPrimaryContainerDark,
    onPrimaryContainer = PantryOnPrimaryContainerDark,

    secondary = PantrySecondaryDark,
    onSecondary = PantryOnSecondaryDark,
    secondaryContainer = PantrySecondaryContainerDark,
    onSecondaryContainer = PantryOnSecondaryContainerDark,

    tertiary = PantryTertiaryDark,
    onTertiary = PantryOnTertiaryDark,
    tertiaryContainer = PantryTertiaryContainerDark,
    onTertiaryContainer = PantryOnTertiaryContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = PantryPrimary,
    onPrimary = PantryOnPrimary,
    primaryContainer = PantryPrimaryContainer,
    onPrimaryContainer = PantryOnPrimaryContainer,

    secondary = PantrySecondary,
    onSecondary = PantryOnSecondary,
    secondaryContainer = PantrySecondaryContainer,
    onSecondaryContainer = PantryOnSecondaryContainer,

    tertiary = PantryTertiary,
    onTertiary = PantryOnTertiary,
    tertiaryContainer = PantryTertiaryContainer,
    onTertiaryContainer = PantryOnTertiaryContainer
)

@Composable
fun PantryPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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