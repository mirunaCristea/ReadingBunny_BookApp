package com.example.readingbunny.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ReadingBunnyColorScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,

    secondary = SoftSage,
    onSecondary = DarkBrown,

    background = WarmCream,
    onBackground = DarkBrown,

    surface = CardCream,
    onSurface = DarkBrown,

    surfaceVariant = SoftCream,
    onSurfaceVariant = MutedBrown,

    outlineVariant = ProgressTrack
)

@Composable
fun ReadingBunnyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ReadingBunnyColorScheme,
        typography = Typography,
        content = content
    )
}