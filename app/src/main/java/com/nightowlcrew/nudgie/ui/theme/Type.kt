package com.nightowlcrew.nudgie.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.nightowlcrew.nudgie.R

/**
 * Nudgie Typography Design System
 * 
 * Uses 'Press Start 2P' for primary UI headers and pixel-perfect impact.
 * Uses 'VT323' for dialogue and primary body text for a retro-CRT feel.
 * Uses 'Fira Sans' for functional labels and micro-information.
 */

// Google Font Provider Setup
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms.fonts",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Primary Retro Headers
val PressStart2P = FontFamily(
    Font(googleFont = GoogleFont("Press Start 2P"), fontProvider = provider)
)

// Retro Terminal/Dialogue Body
val VT323 = FontFamily(
    Font(googleFont = GoogleFont("VT323"), fontProvider = provider)
)

// Functional Clean Sans-Serif
val FiraSans = FontFamily(
    Font(googleFont = GoogleFont("Fira Sans"), fontProvider = provider)
)

// The Nudgie Typography Configuration
val Typography = Typography(
    // Large pixel-style headers (Grid multiples of 8)
    displayLarge = TextStyle(
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // Readable retro body text (Minimum threshold 18.sp for VT323 legibility)
    bodyLarge = TextStyle(
        fontFamily = VT323,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = VT323,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),

    // Functional labels for navigation and small data points
    labelMedium = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    )
)
