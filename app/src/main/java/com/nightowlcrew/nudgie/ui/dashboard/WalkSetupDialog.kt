package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.ui.theme.SpaceOutline
import com.nightowlcrew.nudgie.ui.theme.SpaceSurface
import com.nightowlcrew.nudgie.ui.theme.VT323

enum class WalkTrackingMode { DISTANCE, STEPS, BOTH }

@Composable
fun WalkSetupDialog(
    nudgieName: String,
    onDismissRequest: () -> Unit,
    onStartWalk: (mode: WalkTrackingMode, targetDistance: Int, targetSteps: Int) -> Unit
) {
    var selectedMode by remember { mutableStateOf<WalkTrackingMode?>(null) }
    var stepsInput by remember { mutableStateOf("1000") }
    var distanceInput by remember { mutableStateOf("500") }

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = colorResource(id = R.color.navy_background),
            border = BorderStroke(2.dp, SpaceOutline)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Walk with $nudgieName",
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                if (selectedMode == null) {
                    Text(
                        text = "How would you like to track your walk?",
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    WalkOptionButton("Track Distance (GPS)") { selectedMode = WalkTrackingMode.DISTANCE }
                    WalkOptionButton("Step Goal (Pedometer)") { selectedMode = WalkTrackingMode.STEPS }
                    WalkOptionButton("Both!") { selectedMode = WalkTrackingMode.BOTH }
                } else {
                    Text(
                        text = "Set your goals:",
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    if (selectedMode == WalkTrackingMode.STEPS || selectedMode == WalkTrackingMode.BOTH) {
                        OutlinedTextField(
                            value = stepsInput,
                            onValueChange = { stepsInput = it.filter { char -> char.isDigit() } },
                            label = { Text("Target Steps", color = Color.White.copy(alpha = 0.6f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.White, fontFamily = VT323, fontSize = 18.sp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpaceOutline,
                                unfocusedBorderColor = SpaceOutline.copy(alpha = 0.5f),
                                focusedLabelColor = SpaceOutline,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }

                    if (selectedMode == WalkTrackingMode.DISTANCE || selectedMode == WalkTrackingMode.BOTH) {
                        OutlinedTextField(
                            value = distanceInput,
                            onValueChange = { distanceInput = it.filter { char -> char.isDigit() } },
                            label = { Text("Target Meters", color = Color.White.copy(alpha = 0.6f)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(color = Color.White, fontFamily = VT323, fontSize = 18.sp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SpaceOutline,
                                unfocusedBorderColor = SpaceOutline.copy(alpha = 0.5f),
                                focusedLabelColor = SpaceOutline,
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }

                    WalkOptionButton("Confirm & Start Walk! 🐾") {
                        val steps = stepsInput.toIntOrNull() ?: 0
                        val distance = distanceInput.toIntOrNull() ?: 0
                        onStartWalk(selectedMode!!, distance, steps)
                    }

                    TextButton(onClick = { selectedMode = null }) {
                        Text("Back", color = Color.Gray)
                    }
                }

                if (selectedMode == null) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun WalkOptionButton(text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = SpaceSurface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SpaceOutline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().height(55.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text, 
                style = TextStyle(fontFamily = VT323, fontSize = 20.sp, color = Color.White)
            )
        }
    }
}
