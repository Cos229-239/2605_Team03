package com.nightowlcrew.nudgie.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import com.nightowlcrew.nudgie.ui.dashboard.AppTheme

/**
 * Custom shadow modifier to support themed shadow colors (e.g. Neon Pink for Cyberpunk).
 */
@Composable
fun Modifier.nudgieCardShadow(
    theme: AppTheme,
    elevation: Dp = 4.dp,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium
): Modifier {
    return if (theme == AppTheme.CYBERPUNK) {
        this.shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = cpNeonPink,
            spotColor = cpNeonPink
        )
    } else {
        // Default shadow for other themes
        this.graphicsLayer(shadowElevation = elevation.value, shape = shape, clip = false)
    }
}

val CyberpunkShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(24.dp)
)

val SteampunkShapes = Shapes(
    small = RoundedCornerShape(percent = 50),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp)
)

val GothShapes = Shapes(
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp)
)

private val CyberpunkColorScheme = darkColorScheme(
    primary = cpNeonCyan,
    secondary = cpNeonPink,
    tertiary = cpNeonCyan, // Progress bars and key accents
    background = cpVoid,
    surface = cpMainframe,
    error = cpCorpoRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = cpNeonCyan,    // High visibility headers
    onSurface = cpNeonCyan,       // High visibility interactive text
    surfaceVariant = Color(0xFF1A1A2E), // Darker cards for contrast
    onSurfaceVariant = cpNeonCyan,
    outline = cpNeonCyan.copy(alpha = 0.5f)
)

private val SteampunkColorScheme = darkColorScheme(
    primary = spBrass,
    secondary = spCopper,
    tertiary = spParchment,
    background = spLeather,
    surface = spIron,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = spLeather,
    onBackground = spParchment,
    onSurface = spParchment,
    surfaceVariant = spBrass.copy(alpha = 0.1f),
    onSurfaceVariant = spBrass,
    outline = spCopper
)

private val GothColorScheme = darkColorScheme(
    primary = gothBloodRed,
    secondary = gothNeonPink,
    tertiary = gothNeonPink,
    background = gothAbyss,
    surface = gothObsidian,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = gothTombstone,
    onSurface = gothTombstone,
    surfaceVariant = gothBloodRed.copy(alpha = 0.2f),
    onSurfaceVariant = gothNeonPink,
    outline = gothBloodRed
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
        appTheme == AppTheme.STEAMPUNK -> SteampunkColorScheme
        appTheme == AppTheme.GOTH -> GothColorScheme
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val shapes = when (appTheme) {
        AppTheme.CYBERPUNK -> CyberpunkShapes
        AppTheme.STEAMPUNK -> SteampunkShapes
        AppTheme.GOTH -> GothShapes
        else -> MaterialTheme.shapes
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
