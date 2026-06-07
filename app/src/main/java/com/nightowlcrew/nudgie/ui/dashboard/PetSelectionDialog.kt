package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
import com.nightowlcrew.nudgie.ui.theme.SpaceOutline
import com.nightowlcrew.nudgie.ui.theme.SpaceSurface
import com.nightowlcrew.nudgie.ui.theme.VT323
import com.nightowlcrew.nudgie.utils.PetAssetManager
import com.nightowlcrew.nudgie.utils.PetType

@Composable
fun PetSelectionDialog(
    onDismissRequest: () -> Unit,
    onPetSelected: (PetType) -> Unit,
    currentPetType: PetType
) {
    Dialog(onDismissRequest = onDismissRequest) {
        PetSelectionContent(
            onDismissRequest = onDismissRequest,
            onPetSelected = onPetSelected,
            currentPetType = currentPetType
        )
    }
}

/**
 * Content of the dialog refactored for maximum responsiveness across screen sizes.
 */
@Composable
private fun PetSelectionContent(
    onDismissRequest: () -> Unit,
    onPetSelected: (PetType) -> Unit,
    currentPetType: PetType
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f) // Ensures dialog doesn't hit the very edges on small screens
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF0B0E14), // navy_background
        border = BorderStroke(2.dp, SpaceOutline)
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Choose Your Nudgie",
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Using Adaptive grid cells so it scales to 3+ columns on tablets/landscape
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false) // Allow grid to take only needed space, up to max
                ) {
                    items(PetType.entries) { petType ->
                        PetOptionItem(
                            petType = petType,
                            isSelected = petType == currentPetType,
                            onClick = {
                                onPetSelected(petType)
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
private fun PetOptionItem(
    petType: PetType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) SpaceSurface else NavySurface.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color.White else SpaceOutline.copy(alpha = 0.5f)
        ),
        modifier = Modifier.aspectRatio(1f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = PetAssetManager.getPetDrawable(petType)),
                contentDescription = petType.displayName,
                modifier = Modifier
                    .sizeIn(minWidth = 60.dp, minHeight = 60.dp, maxWidth = 100.dp, maxHeight = 100.dp)
                    .fillMaxSize(0.7f),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = petType.displayName,
                style = TextStyle(
                    fontFamily = VT323,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Preview(name = "Phone", device = Devices.PIXEL_7, showBackground = true, backgroundColor = 0xFF000000)
@Preview(name = "Tablet", device = Devices.PIXEL_TABLET, showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PetSelectionDialogPreview() {
    NudgieTheme {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            PetSelectionContent(
                onDismissRequest = {},
                onPetSelected = {},
                currentPetType = PetType.BLUE
            )
        }
    }
}
