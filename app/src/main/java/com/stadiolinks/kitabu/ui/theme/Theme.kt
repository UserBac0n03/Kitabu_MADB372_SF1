package com.stadiolinks.kitabu.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Which STADIO colour plays which part in the app
private val DarkColorScheme = darkColorScheme(
    primary = SchoolAccountancy,
    secondary = SchoolCommerce,
    tertiary = SchoolFashion,
    background = KitabuBackground,
    surface = KitabuSurface,
    // Cards take their colour from this slot
    surfaceContainerHighest = KitabuSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SchoolAccountancy,
    secondary = SchoolCommerce,
    tertiary = SchoolFashion
)

/**
 * Wraps the whole app so every screen shares the same colours and text styles.
 * Kitabu is dark by default and ignores the phone's wallpaper colours,
 * so it looks the same on every device.
 */
@Composable
fun KitabuTheme(
    darkTheme: Boolean = true,
    // Wallpaper-based colours only exist on Android 12 and newer
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
