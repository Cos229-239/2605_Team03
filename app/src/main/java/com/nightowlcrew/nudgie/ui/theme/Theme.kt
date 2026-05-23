package com.nightowlcrew.nudgie.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nightowlcrew.nudgie.ui.dashboard.AppTheme

val CyberpunkShapes = Shapes(
    small = CutCornerShape(4.dp),
    medium = CutCornerShape(8.dp),
    large = CutCornerShape(12.dp)
)

private val CyberpunkColorScheme = darkColorScheme(
    primary = cpNeonCyan,
    secondary = cpNeonPink,
    tertiary = cpCyberYellow,
    background = cpVoid,
    surface = cpMainframe,
    error = cpCorpoRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = cpDimCyan,     // Slightly dimmed for better reading
    onSurface = cpDimCyan,        // Slightly dimmed for better reading
    surfaceVariant = cpGlitchBlue.copy(alpha = 0.2f),
    onSurfaceVariant = cpNeonCyan,
    outline = cpNeonCyan.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGold,
    secondary = LevelUpBlue,
    tertiary = LogicPurple,
    background = Color(0xFF121212),
    surface = DarkGreyScale
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGold,
    secondary = LevelUpBlue,
    tertiary = LogicPurple,
    background = Color.White,
    surface = Color.White
)

@Composable
fun NudgieTheme(
    appTheme: AppTheme = AppTheme.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        appTheme == AppTheme.CYBERPUNK -> CyberpunkColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val shapes = if (appTheme == AppTheme.CYBERPUNK) CyberpunkShapes else MaterialTheme.shapes

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
