package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.ui.theme.LevelUpBlue
import com.nightowlcrew.nudgie.ui.theme.SuccessGreen

@Composable
fun AnimatedAlien(
    modifier: Modifier = Modifier,
    baseColor: Color = LevelUpBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PetAnimation")
    val frameIndex by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FrameIndex"
    )

    val frame1 = """
|
|       _..._
|     .'     '.
|    /`\     /`\
|   (_*_|   |_*_)
|   (     "     ) |\ |\ /|          __+__
|    \         /   \\||//          /     \
|     \  \_/  /  |\|`  /        __/   O   \__
|      '.___.'   \____/       /    \__|__/   \
|       (___)    (___)        \______________/
|     /`     `\  / /                 /|\
|    |         \/ /                 / | \
|    | |     |\  /                 /  |  \
|    | |     | "`         
|    | |     |
|    | |     |    
|    |_|_____|        
|   (___)_____) 
    """.trimMargin()
    
    val frames = listOf(frame1, frame1, frame1)
    val currentFrame = frames[frameIndex % 3]

    val annotatedText = buildAnnotatedString {
        append(currentFrame)

        fun addHighlight(part: String, color: Color) {
            var start = currentFrame.indexOf(part)
            while (start != -1) {
                addStyle(SpanStyle(color = color), start, start + part.length)
                start = currentFrame.indexOf(part, start + 1)
            }
        }

        // 1. Head - Green
        listOf(
            "_..._", ".'     '.", "/`\\     /`\\", "(_", "_|   |_", "_)", "(     \"     )",
            "\\         /", "\\  \\_/  /", "'.___.'"
        ).forEach { addHighlight(it, SuccessGreen) }

        // 2. UFO Guy - Green
        addHighlight(" O ", SuccessGreen)

        // 3. Eyes (*) - Yellow
        addHighlight("*", Color.Yellow)

        // 4. UFO Triangle - Yellow
        listOf("/|\\", "/ | \\", "/  |  \\").forEach { addHighlight(it, Color.Yellow) }
    }

    Text(
        text = annotatedText,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall.copy(
            fontFamily = FontFamily.Monospace,
            lineHeight = 12.sp,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        ),
        color = baseColor
    )
}
