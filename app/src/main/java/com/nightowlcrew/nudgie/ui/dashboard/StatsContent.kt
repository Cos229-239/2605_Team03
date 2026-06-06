package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
import com.nightowlcrew.nudgie.ui.theme.SpaceAccent
import com.nightowlcrew.nudgie.ui.theme.SpaceOutline
import com.nightowlcrew.nudgie.ui.theme.SpaceSecondaryText
import com.nightowlcrew.nudgie.ui.theme.VT323

@Composable
fun StatsContent(uiState: DashboardUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Your Progress",
            style = TextStyle(
                fontFamily = VT323,
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Level Card
        LevelCard(petStats = uiState.petStats)

        Spacer(modifier = Modifier.height(24.dp))

        // Streak and Tasks Done Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatGridCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.FlashOn,
                iconTint = Color(0xFFFFB703),
                value = "12", // Placeholder for actual streak
                label = "Day Streak"
            )
            StatGridCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Check,
                iconTint = Color(0xFFE1BEE7),
                value = uiState.totalTasksDone.toString(),
                label = "Tasks Done"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Categories",
            style = TextStyle(
                fontFamily = VT323,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories List
        CategoryProgressList(uiState = uiState)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun LevelCard(petStats: PetStats) {
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, SpaceOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = petStats.level.toString(),
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 32.sp,
                        color = NavyBackground,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.offset(y = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Level",
                        style = TextStyle(
                            fontFamily = VT323,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = SpaceAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${petStats.xp} / 800 XP",
                        style = TextStyle(
                            fontFamily = VT323,
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { petStats.xp / 800f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = SpaceAccent,
                    trackColor = Color.White.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun StatGridCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String
) {
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(120.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = value,
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 36.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = label,
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 16.sp,
                        color = SpaceSecondaryText
                    )
                )
            }
        }
    }
}

@Composable
fun CategoryProgressList(uiState: DashboardUiState) {
    val categories = listOf(
        CategoryInfo("Learning", Color(0xFF916BFF), CozyCategory.MIND_SPACE),
        CategoryInfo("Health", Color(0xFF6BCB77), CozyCategory.BODY_VITALITY),
        CategoryInfo("Mindfulness", Color(0xFFFFB703), CozyCategory.SELF_CARE_RITUALS),
        CategoryInfo("Productivity", Color(0xFF4D96FF), CozyCategory.DAILY_RHYTHMS)
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categories.forEach { info ->
            val activitiesInCat = uiState.categorizedActivities[info.category] ?: emptyList()
            val total = activitiesInCat.sumOf { it.targetCount }
            val completed = activitiesInCat.sumOf { it.currentCount }
            val progress = if (total > 0) completed.toFloat() / total else 0.5f // Default to 50% for demo if empty

            CategoryProgressRow(
                name = info.name,
                progress = progress,
                color = info.color
            )
        }
    }
}

data class CategoryInfo(val name: String, val color: Color, val category: CozyCategory)

@Composable
fun CategoryProgressRow(name: String, progress: Float, color: Color) {
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontFamily = VT323,
                    fontSize = 20.sp,
                    color = Color.White
                ),
                modifier = Modifier.width(100.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = color,
                trackColor = Color.White.copy(alpha = 0.05f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    fontFamily = VT323,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f)
                ),
                modifier = Modifier.width(40.dp)
            )
        }
    }
}

@Preview
@Composable
fun StatsContentPreview() {
    NudgieTheme {
        StatsContent(
            uiState = DashboardUiState(
                petStats = PetStats(level = 5, xp = 450),
                totalTasksDone = 25
            )
        )
    }
}
