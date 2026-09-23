package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1F1500),
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldLight,
    secondary = NeonGreen,
    onSecondary = Color(0xFF00220B),
    secondaryContainer = NeonGreenContainer,
    onSecondaryContainer = Color(0xFFB9F6CA),
    tertiary = CyberCyan,
    onTertiary = Color(0xFF001F25),
    tertiaryContainer = CyberCyanContainer,
    onTertiaryContainer = Color(0xFF97F0FF),
    background = ArenaBlack,
    onBackground = TextPrimary,
    surface = ArenaSurface,
    onSurface = TextPrimary,
    surfaceVariant = ArenaSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ArenaBorder,
    error = RubyRed,
    errorContainer = RubyRedContainer
)

private val LightColorScheme = DarkColorScheme // Default to esports dark theme for performance & contrast

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

