package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
import com.nightowlcrew.nudgie.ui.theme.SpaceOutline
import com.nightowlcrew.nudgie.ui.theme.SpaceSurface
import com.nightowlcrew.nudgie.ui.theme.VT323

/**
 * Type-safe enum for play options available in the menu.
 */
enum class PlayOption(val displayName: String) {
    WALK("Going on a walk"),
    MINIGAME("Minigame"),
    FETCH("Fetch"),
    HIDE_AND_SEEK("Hide n Seek")
}

@Composable
fun PlayMenuModal(
    onDismissRequest: () -> Unit,
    onOptionSelected: (PlayOption) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = colorResource(id = R.color.navy_background),
            border = BorderStroke(2.dp, SpaceOutline)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                // Close button in the top-right corner
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp), // Space for the close button
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Play Time!",
                        style = TextStyle(
                            fontFamily = VT323,
                            fontSize = 32.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Primary button options stacked vertically
                    PlayOption.entries.forEach { option ->
                        PlayMenuButton(
                            text = option.displayName,
                            onClick = {
                                onOptionSelected(option)
                                onDismissRequest()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayMenuButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = SpaceSurface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SpaceOutline.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = VT323,
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayMenuModalPreview() {
    NudgieTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            PlayMenuModal(
                onDismissRequest = {},
                onOptionSelected = {}
            )
        }
    }
}
