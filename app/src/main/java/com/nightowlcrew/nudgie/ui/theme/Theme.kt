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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.Dp
import com.nightowlcrew.nudgie.ui.dashboard.AppTheme

/**
 * Custom shadow modifier to support hard-edged retro game drop shadows
 * that cast explicitly to the lower right.
 */
@Composable
fun Modifier.nudgieCardShadow(
    theme: AppTheme,
    elevation: Dp = 4.dp,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.medium
): Modifier {
    // 1. Determine the hard block shadow tint color based on the selected theme style
    val shadowColor = when (theme) {
        AppTheme.CYBERPUNK -> cpNeonPink
        AppTheme.GOTH -> Color.Black
        AppTheme.STEAMPUNK -> Color(0xFF4A3525) // Solid deep brass/leather tone
        else -> Color.Black.copy(alpha = 0.15f)   // Clean subtle vintage block for default look
    }

    return this.drawBehind {
        val sizePx = size
        // Punchy, prominent 6.dp style offset multiplier for retro layouts
        val shadowOffsetPx = elevation.toPx() * 1.5f

        // 2. Map explicit pixel values directly from the active theme structure
        // This guarantees that canvas drawings exactly mirror the roundness of the main layout container
        val radiusPx = when (theme) {
            AppTheme.GOTH -> 0f
            AppTheme.CYBERPUNK -> 12.dp.toPx() // Clean 12.dp match for Cyberpunk cards
            AppTheme.STEAMPUNK -> {
                // If the height is small, it's a circular capsule stat badge
                if (sizePx.height < 40.dp.toPx()) sizePx.height / 2f else 24.dp.toPx()
            }
            else -> 12.dp.toPx() // Perfect match for default rounded corners
        }

        // 3. Positive X shifts right, positive Y shifts down to create a crisp lower-right cast
        drawRoundRect(
            color = shadowColor,
            topLeft = androidx.compose.ui.geometry.Offset(shadowOffsetPx, shadowOffsetPx),
            size = sizePx,
            cornerRadius = CornerRadius(radiusPx, radiusPx)
        )
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
    tertiaryContainer = cpNeonCyan.copy(alpha = 0.2f),
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
    tertiaryContainer = spIron,
    background = spLeather,
    surface = spIron,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = spLeather,
    onBackground = spParchment,
    onSurface = spParchment,
    surfaceVariant = spBrass.copy(alpha = 0.7f),
    onSurfaceVariant = spParchmentDark,
    outline = spCopper
)

private val GothColorScheme = darkColorScheme(
    primary = gothBloodRed,
    secondary = gothNeonPink,
    tertiary = gothNeonPink,
    tertiaryContainer = gothBloodRed.copy(alpha = 0.2f),
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
    tertiaryContainer = LogicPurple.copy(alpha = 0.2f),
    background = Color(0xFF121212),
    surface = DarkGreyScale,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGold,
    secondary = LevelUpBlue,
    tertiary = LogicPurple,
    tertiaryContainer = LogicPurple.copy(alpha = 0.2f),
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color.Black
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