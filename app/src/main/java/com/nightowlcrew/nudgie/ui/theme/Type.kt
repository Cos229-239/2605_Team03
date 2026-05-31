package com.nightowlcrew.nudgie.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.R

// Statically bundle the local font definitions to prevent system fallbacks
val PressStart2P = FontFamily(
    Font(R.font.press_start_2p_regular, FontWeight.Normal)
)

val VT323 = FontFamily(
    Font(R.font.vt323_regular, FontWeight.Normal)
)

val FiraSans = FontFamily(
    Font(R.font.fira_sans_regular, FontWeight.Normal)
)

// Keep your existing typography configurations intact
// src/main/java/com/nightowlcrew/nudgie/ui/theme/Type.kt

val Typography = Typography(
    // Perfect for huge game titles (like NUDGIE)
    displayLarge = TextStyle(
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 38.sp,
        lineHeight = 46.sp,
        letterSpacing = (-1).sp
    ),
    // Perfect for sub-headings or active stats
    headlineMedium = TextStyle(
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // Tighter for inline pixel badges
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    // Crisp, clean game-boy style reading text
    bodyLarge = TextStyle(
        fontFamily = VT323,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = VT323,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),
    // Clean, readable structural labels
    labelLarge = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)