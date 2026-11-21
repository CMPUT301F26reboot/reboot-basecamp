/**
 * The app's Material 3 theme. Wrap every screen (and every Preview) in [CampsiteReservationTheme]
 * so it gets the app's colors, text styles and shapes. The app only has a light theme.
 *
 */
package com.example.campsitereservationapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** The app's colors. */
private val AppColorScheme = lightColorScheme(
    primary = Forest30,
    onPrimary = Gray0,
    primaryContainer = Forest90,
    onPrimaryContainer = Forest10,
    inversePrimary = Forest80,

    secondary = Emerald40,
    onSecondary = Gray0,
    secondaryContainer = Emerald90,
    onSecondaryContainer = Emerald10,

    tertiary = Amber40,
    onTertiary = Gray0,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,

    error = Red40,
    onError = Gray0,
    errorContainer = Red90,
    onErrorContainer = Red10,

    background = Gray0,
    onBackground = Gray900,
    surface = Gray0,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray500,
    surfaceTint = Forest30,
    inverseSurface = Gray800,
    inverseOnSurface = Gray50,

    outline = Gray600,
    outlineVariant = Gray200,

    surfaceBright = Gray0,
    surfaceDim = Gray200,
    surfaceContainerLowest = Gray0,
    surfaceContainerLow = Gray50,
    surfaceContainer = Gray100,
    surfaceContainerHigh = Gray150,
    surfaceContainerHighest = Gray200
)

/**
 * Applies the app's colors, text styles and shapes to [content].
 *
 * @param content the UI to theme.
 */
@Composable
fun CampsiteReservationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}