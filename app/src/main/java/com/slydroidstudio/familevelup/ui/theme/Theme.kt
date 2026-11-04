package com.slydroidstudio.familevelup.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

fun getGalaxyLightColorScheme(theme: GalaxyTheme): ColorScheme {
    val primary = theme.primaryColor
    val (primaryContainer, secondary) = when (theme) {
        GalaxyTheme.OCEAN -> Pair(Color(0xFFDBEAFE), Color(0xFF2563EB))
        GalaxyTheme.NATURE -> Pair(Color(0xFFD1FAE5), Color(0xFF059669))
        GalaxyTheme.ZEN -> Pair(Color(0xFFFEF3C7), Color(0xFFEA580C))
        GalaxyTheme.CREATIVE -> Pair(Color(0xFFEDE9FE), Color(0xFF7C3AED))
        GalaxyTheme.SAKURA -> Pair(Color(0xFFFFE4E6), Color(0xFFE11D48))
    }

    return lightColorScheme(
        primary = primary,
        onPrimary = Color.White,
        primaryContainer = primaryContainer,
        onPrimaryContainer = Color(0xFF0F172A),
        secondary = secondary,
        onSecondary = Color.White,
        secondaryContainer = primaryContainer.copy(alpha = 0.5f),
        onSecondaryContainer = Color(0xFF0F172A),
        background = Color(0xFFF1F5F9),
        onBackground = Color(0xFF0F172A),
        surface = Color.White,
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFE2E8F0),
        onSurfaceVariant = Color(0xFF334155),
        outline = Color(0xFF64748B),
        error = Color(0xFFDC2626),
        errorContainer = Color(0xFFFEE2E2),
        onErrorContainer = Color(0xFF991B1B)
    )
}

fun getGalaxyDarkColorScheme(theme: GalaxyTheme): ColorScheme {
    val (primary, primaryContainer) = when (theme) {
        GalaxyTheme.OCEAN -> Pair(Color(0xFF60A5FA), Color(0xFF1E3A8A))
        GalaxyTheme.NATURE -> Pair(Color(0xFF34D399), Color(0xFF065F46))
        GalaxyTheme.ZEN -> Pair(Color(0xFFFBBF24), Color(0xFF78350F))
        GalaxyTheme.CREATIVE -> Pair(Color(0xFFA78BFA), Color(0xFF5B21B6))
        GalaxyTheme.SAKURA -> Pair(Color(0xFFFB7185), Color(0xFF9F1239))
    }

    return darkColorScheme(
        primary = primary,
        onPrimary = Color(0xFF0F172A),
        primaryContainer = primaryContainer,
        onPrimaryContainer = Color(0xFFDBEAFE),
        secondary = primary.copy(alpha = 0.85f),
        onSecondary = Color(0xFF0F172A),
        secondaryContainer = primaryContainer,
        onSecondaryContainer = Color.White,
        background = Color(0xFF0B0F19),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF111827),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF1F2937),
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = Color(0xFF94A3B8),
        error = Color(0xFFF87171),
        errorContainer = Color(0xFF7F1D1D),
        onErrorContainer = Color(0xFFFEE2E2)
    )
}

@Composable
fun FamiLevelUpTheme(
    galaxyTheme: GalaxyTheme = GalaxyTheme.OCEAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        getGalaxyDarkColorScheme(galaxyTheme)
    } else {
        getGalaxyLightColorScheme(galaxyTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
