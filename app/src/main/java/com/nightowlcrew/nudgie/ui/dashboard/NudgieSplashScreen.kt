package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.VT323
import kotlinx.coroutines.delay

@Composable
fun NudgieSplashScreen(
    onSplashFinished: () -> Unit
) {
    val characters = listOf(
        R.drawable.blue_trashpanda,
        R.drawable.red_fox,
        R.drawable.oxylotyl,
        R.drawable.orange_blue_dragon_bgno
    )
    
    // Select a random character once per composition
    val randomCharacter = remember { characters.random() }

    // Entrance Animation States
    val logoAlpha = remember { Animatable(0f) }
    val mascotAlpha = remember { Animatable(0f) }
    val mascotScale = remember { Animatable(0.3f) } // Start small

    // Floating Animation
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingMascot")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatingOffset"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    LaunchedEffect(Unit) {
        // Sequenced entrance
        logoAlpha.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        delay(300)
        
        // Mascot "Pop" animation: Scale from small -> really large -> final size
        mascotAlpha.animateTo(1f, tween(1200))
        
        // Overshoot to "Really Large" (1.5x)
        mascotScale.animateTo(1.5f, tween(1000, easing = FastOutSlowInEasing))
        // Settle into final size with a gentle spring
        mascotScale.animateTo(
            targetValue = 1.0f, 
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NavySurface,
                        NavyBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing Background Glow behind the mascot
        Box(
            modifier = Modifier
                .size(450.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF6200EE).copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Logo at the top - Animated entrance
            Image(
                painter = painterResource(id = R.drawable.nudgie_name_transparent),
                contentDescription = "Nudgie Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .alpha(logoAlpha.value),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(1.dp))

            // Random Character - Animated floating and entrance
            Image(
                painter = painterResource(id = randomCharacter),
                contentDescription = "Random Character",
                modifier = Modifier
                    .size(280.dp)
                    .offset { IntOffset(0, floatingOffset.dp.roundToPx()) }
                    .alpha(mascotAlpha.value)
                    .scale(mascotScale.value),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // Slogan at the bottom
            Text(
                text = "Time to nudge your goals...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = VT323,
                    color = LavenderText.copy(alpha = mascotAlpha.value)
                )
            )
        }
    }
}

@Preview
@Composable
fun NudgieSplashScreenPreview() {
    NudgieSplashScreen(onSplashFinished = {})
}
