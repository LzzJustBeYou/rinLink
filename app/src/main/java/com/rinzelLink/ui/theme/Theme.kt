package com.rinzelLink.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AqaraGreen,
    onPrimary = Color.White,
    primaryContainer = AqaraGreenDark,
    onPrimaryContainer = Color.White,
    
    secondary = AqaraBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1565C0),
    onSecondaryContainer = Color.White,
    
    tertiary = AqaraOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE65100),
    onTertiaryContainer = Color.White,
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = TextSecondaryDark,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color.White,
    
    outline = AqaraGray600,
    outlineVariant = AqaraGray700,
    scrim = Color.Black.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = AqaraGreen,
    onPrimary = Color.White,
    primaryContainer = AqaraGreenLight,
    onPrimaryContainer = AqaraGreenDark,
    
    secondary = AqaraBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3F2FD),
    onSecondaryContainer = Color(0xFF1565C0),
    
    tertiary = AqaraOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFF3E0),
    onTertiaryContainer = Color(0xFFE65100),
    
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = AqaraGray100,
    onSurfaceVariant = TextSecondaryLight,
    
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    
    outline = AqaraGray300,
    outlineVariant = AqaraGray200,
    scrim = Color.Black.copy(alpha = 0.5f)
)

@Composable
fun RinLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
