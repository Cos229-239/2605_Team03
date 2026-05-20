package com.example.nudgie.ui.dashboard // Link to the project structure

import androidx.compose.foundation.* // Handles scrolling and borders
import androidx.compose.foundation.layout.* // Manages 8-bit dashboard spacing
import androidx.compose.foundation.shape.RoundedCornerShape // Sharp edges for console feel
import androidx.compose.material3.* // Provides tactile "Game Boy" depth
import androidx.compose.runtime.Composable // Core UI function annotation
import androidx.compose.ui.Alignment // Centers the pixel-art mascot
import androidx.compose.ui.Modifier // Tool for borders and padding
import androidx.compose.ui.graphics.Color // Used for Nudgie palette colors
import androidx.compose.ui.text.font.FontWeight // Mimics chunky 8-bit fonts
import androidx.compose.ui.unit.dp // Standard layout spacing unit
import androidx.compose.ui.unit.sp // Scalable unit for dialogue and branding
import com.example.nudgie.ui.theme.* // Import the Toy Palette (BrandGold, LogicPurple, etc.)

@Composable
fun NudgieDashboard() {
    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Ensures scrollability
        ) {
            // BRANDING
            Text(
                text = "NUDGIE",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold, // Mimics "Press Start 2P" style
                color = Color.Black
            )
            Text("Level 5 • Baby Stage", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // PET TOY SCREEN
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
                border = BorderStroke(4.dp, Color.Black), // Visual depth for toy feel
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(0.dp) // Retro sharp edges
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("✧ (◕‿◕) ✧", fontSize = 40.sp) // Mascot Placeholder
                }
            }

            // GENTLE AI NUDGE
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = LogicPurple),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Text(
                    text = "I saw you coded today! My logic circuits are tingling.",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    color = Color.Black // "VT323" dialogue style placeholder
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // STATS GRID
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NudgieStatCard("HAPPINESS", "85%", HeartRed, Modifier.weight(1f))
                    NudgieStatCard("ENERGY", "62%", ElectricYellow, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NudgieStatCard("LEVEL", "5", BrandGold, Modifier.weight(1f))
                    NudgieStatCard("AGE", "5 Days", LevelUpBlue, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun NudgieStatCard(label: String, value: String, accentColor: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(3.dp, Color.Black), // Tactile visuals
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            // Toy palette indicator
            Box(modifier = Modifier.size(14.dp).background(accentColor).border(1.dp, Color.Black))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}