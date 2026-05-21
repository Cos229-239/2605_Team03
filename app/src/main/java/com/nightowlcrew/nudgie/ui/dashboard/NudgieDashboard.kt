package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboard(viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Pet", "Learn", "Stats", "Settings")
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.AutoAwesome, // Pet equivalent
        Icons.AutoMirrored.Filled.MenuBook, // Learn
        Icons.AutoMirrored.Filled.ShowChart, // Stats
        Icons.Filled.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F4F6),
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { 
                            Text(
                                text = item.uppercase(), 
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp // Slightly smaller for nav bars
                            ) 
                        },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = DarkBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedItem) {
                0 -> {
                    DashboardContent(
                        activities = uiState.activities,
                        onToggleHabit = { viewModel.toggleHabitCompletion(it) }
                    )
                }
                1 -> {
                    PetScreenContent()
                }
                4 -> {
                    SettingsScreen(viewModel = viewModel)
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Screen Coming Soon".uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PetScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Cushioned horizontal padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MY PET".uppercase(),
            style = MaterialTheme.typography.displayLarge,
            color = Color.Black
        )
        
        // Extra spacer for pixel font vertical breathing room
        Spacer(modifier = Modifier.height(32.dp))
        
        PetHeroContainer()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Stats & Customization coming soon!".uppercase(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun PetHeroContainer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color(0xFFD1D5DB), RoundedCornerShape(24.dp))
            .border(4.dp, DarkBackground, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for Pixel Art Pet
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("(◕‿◕)", fontSize = 60.sp, color = DarkBackground)
        }

        // Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(DarkBackground, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Numeric badge - using clean sans-serif for readability
                Text(
                    text = "1", 
                    color = Color.White, 
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    activities: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp) // Cushioned horizontal padding
    ) {
        // HEADER
        Text(
            text = "BUDDY".uppercase(),
            style = MaterialTheme.typography.displayLarge,
            color = Color.Black
        )
        // High-density metadata - pinned to clean sans-serif
        Text(
            text = "Level 5 • Baby Stage",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TOP HAPPINESS BAR
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HAPPINESS".uppercase(),
                            color = Color.Gray,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = "85%",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 0.85f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = Color.Gray,
                    trackColor = Color(0xFF374151)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BUDDY WINDOW
        PetHeroContainer()

        Spacer(modifier = Modifier.height(32.dp))

        // DASHBOARD SECTION
        Text(
            text = "DASHBOARD".uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // STATS GRID
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NewStatCard(
                    label = "HAPPINESS",
                    value = "85%",
                    icon = Icons.Filled.Favorite,
                    iconColor = Color.Red,
                    bgColor = CardRedBg,
                    borderColor = HeartRed,
                    modifier = Modifier.weight(1f)
                )
                NewStatCard(
                    label = "ENERGY",
                    value = "62%",
                    icon = Icons.Filled.FlashOn,
                    iconColor = Color(0xFFFFC107),
                    bgColor = CardYellowBg,
                    borderColor = ElectricYellow,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NewStatCard(
                    label = "LEVEL",
                    value = "5",
                    icon = Icons.Filled.Star,
                    iconColor = Color.Blue,
                    bgColor = CardBlueBg,
                    borderColor = LevelUpBlue,
                    modifier = Modifier.weight(1f)
                )
                NewStatCard(
                    label = "AGE",
                    value = "5 Days",
                    icon = Icons.Filled.CalendarToday,
                    iconColor = Color.Green,
                    bgColor = CardGreenBg,
                    borderColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TODAY'S ACTIVITY
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TODAY'S ACTIVITY".uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 14.sp, // Slightly forced override for width management
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DailyActivityLog(activities, onToggleHabit)

        Spacer(modifier = Modifier.height(80.dp)) // Extra space for scroll
    }
}

// Main log container
@Composable
fun DailyActivityLog(
    activities: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            activities.forEachIndexed { index, activity ->
                ActivityLogItem(activity, onToggleHabit)
                if (index < activities.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Individual log item
@Composable
fun ActivityLogItem(
    activity: ActivityItem,
    onToggleHabit: (ActivityItem) -> Unit
) {
    val itemBgColor = if (activity.isCompleted) Color(0xFF1F2937).copy(alpha = 0.4f) else Color(0xFF374151)
    val contentAlpha = if (activity.isCompleted) 0.5f else 1.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleHabit(activity) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = itemBgColor)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Box (Acts as Checkbox)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF1F2937), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF4B5563), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (activity.isCompleted) {
                    Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Emoji/Icon
            Text(text = activity.icon, fontSize = 20.sp)

            Spacer(modifier = Modifier.width(12.dp))

            // Description - Pinned to clean sans-serif for accessibility
            Text(
                text = activity.description,
                color = Color.White.copy(alpha = contentAlpha),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                textDecoration = if (activity.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )

            // Time - Pinned to clean sans-serif for accessibility
            Text(
                text = activity.time,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FIGMA_DASHBOARD_PREVIEW() {
    val mockActivities = listOf(
        ActivityItem(1, "🍖", "Fed Buddy", "08:30", true),
        ActivityItem(2, "⚽", "Played with ball", "10:15", true),
        ActivityItem(3, "🎯", "Training session", "12:00", false),
        ActivityItem(4, "🌳", "Walk outside", "15:00", false),
        ActivityItem(5, "🍽️", "Evening meal", "18:00", false)
    )
    NudgieTheme {
        DashboardContent(
            activities = mockActivities,
            onToggleHabit = {}
        )
    }
}

@Composable
fun NewStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
        }
    }
}
