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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.ui.theme.SpaceOutline
import com.nightowlcrew.nudgie.ui.theme.SpaceSurface
import com.nightowlcrew.nudgie.ui.theme.VT323

@Composable
fun EarlyTerminationDialog(
    onDismiss: () -> Unit,
    onSaveProgress: () -> Unit,
    onClearProgress: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    text = "End walk early?",
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                TerminationOptionButton("Continue Later") { onSaveProgress() }
                TerminationOptionButton("End & Clear") { onClearProgress() }

                TextButton(onClick = onDismiss) {
                    Text("Go Back", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun TerminationOptionButton(text: String, onClick: () -> Unit) {
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
