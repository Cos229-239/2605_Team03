package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AnimatedKitty(
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    val frames = listOf(
        """
            
               \      /
              | \."./ |
             /         \
            /  {|} {|}  \   _
            \ ==  Y  == /  ( \
             ;--._^_.--;    ) )
            /    \_/    \  / /
            |    ( )    | / /
           /|   |   |   |\ /
          | |   |   |   | |
           \|   |___|   |/
            '""'     '""'
        
        """.trimIndent(),
        """
               \      /
              | \."./ |
             /         \
            /  {|} {|}  \    _
            \ ==  Y  == /   | |
             ;--._^_.--;    | |
            /    \_/    \  / /
            |    ( )    | / /
           /|   |   |   |\ /
          | |   |   |   | |
           \|   |___|   |/
            '""'     '""'
        """.trimIndent(),
        """
               \      /
              | \."./ |
             /         \
            /  {|} {|}  \     _
            \ ==  Y  == /   / /
             ;--._^_.--;    | |
            /    \_/    \  / /
            |    ( )    | / /
           /|   |   |   |\ /
          | |   |   |   | |
           \|   |___|   |/
            '""'     '""'
        """.trimIndent()

    )

    var currentFrame by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(600)
            currentFrame = (currentFrame + 1) % frames.size
        }
    }

    Text(
        text = frames[currentFrame],
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        color = color,
        softWrap = false
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun AnimatedKittyPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedKitty(color = Color.Magenta)
    }
}
