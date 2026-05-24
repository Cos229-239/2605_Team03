package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme

/**
 * A reusable pixel-perfect dialogue card for BUDDY's messages.
 * Mimics a classic sharp-cornered handheld game console screen.
 * 
 * @param text The message to display.
 */
@Composable
fun BuddyDialogueBox(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937) // Dark canvas backdrop
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(2.dp, Color.Gray) // Tactile line framework
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                lineHeight = 26.sp,
                color = Color.White
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BuddyDialogueBoxPreview() {
    NudgieTheme {
        BuddyDialogueBox(
            text = "Are you really going to eat that? My data suggests your health bar is low.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
