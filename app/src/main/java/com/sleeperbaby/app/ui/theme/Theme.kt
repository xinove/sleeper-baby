package com.sleeperbaby.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Scheme = darkColorScheme(
    primary = WarmGold,
    onPrimary = NightNavy,
    secondary = Lavender,
    background = NightNavy,
    surface = NightCard,
    onBackground = Mist,
    onSurface = Mist,
    onSurfaceVariant = MuteText,
    outline = Glass,
)

@Composable
fun SleeperBabyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Scheme,
        typography = Typography,
        content = content,
    )
}
