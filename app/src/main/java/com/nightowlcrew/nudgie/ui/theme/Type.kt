package com.nightowlcrew.nudgie.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.nightowlcrew.nudgie.R

// Google Font Provider Setup
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms.fonts",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Header Font: Press Start 2P
val PressStart2P = FontFamily(
    Font(googleFont = GoogleFont("Press Start 2P"), fontProvider = provider)
)

// Dialogue Font: VT323
val VT323 = FontFamily(
    Font(googleFont = GoogleFont("VT323"), fontProvider = provider)
)

// The Nudgie Typography Style Set
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = PressStart2P,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = VT323,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    )
)
