package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CopilotCyan,
    onPrimary = Color(0xFF00363F),
    primaryContainer = CopilotCyanContainer,
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = CreativeViolet,
    onSecondary = Color(0xFF2E004F),
    secondaryContainer = CreativeVioletContainer,
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = StudioRose,
    onTertiary = Color(0xFF4C0015),
    tertiaryContainer = StudioRoseContainer,
    onTertiaryContainer = Color(0xFFFFD9DF),
    background = StudioMidnight,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder
)

private val LightColorScheme = lightColorScheme(
    primary = CopilotCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7F4FF),
    onPrimaryContainer = Color(0xFF001F25),
    secondary = CreativeVioletDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDDCFF),
    onSecondaryContainer = Color(0xFF2E004F),
    tertiary = StudioRose,
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek studio dark aesthetic
    dynamicColor: Boolean = false, // Keep high-fidelity custom branding
    content: @Composable () -> Unit,
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
