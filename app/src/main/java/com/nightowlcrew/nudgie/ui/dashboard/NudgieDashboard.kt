package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Extra spacer for pixel font vertical breathing room
        Spacer(modifier = Modifier.height(32.dp))
        
        PetHeroContainer()
        
        Spacer(modifier = Modifier.height(32.dp))

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
        
        Text(
            text = "Customization coming soon!".uppercase(),
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
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.large)
            .border(4.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large),
        contentAlignment = Alignment.Center
    ) {
        // Placeholder for Pixel Art Pet
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("(◕‿◕)", fontSize = 60.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Numeric badge - using clean sans-serif for readability
                Text(
                    text = "1", 
                    color = MaterialTheme.colorScheme.onPrimary, 
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
            text = "Nudgie".uppercase(),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        // High-density metadata - pinned to clean sans-serif
        Text(
            text = "Level 5 • Baby Stage",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TOP HAPPINESS BAR
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.medium,
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
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HAPPINESS".uppercase(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "85%",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 0.85f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BUDDY WINDOW WITH FLOATING STATS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            PetHeroContainer()

            // Top Left - Happiness
            PetCornerStatBadge(
                label = "Happiness",
                value = "85%",
                accentColor = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )

            // Top Right - Energy
            PetCornerStatBadge(
                label = "Energy",
                value = "62%",
                accentColor = Color(0xFFFFC107),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )

            // Bottom Left - Age
            PetCornerStatBadge(
                label = "Age",
                value = "5 Days",
                accentColor = Color.Green,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )

            // Bottom Right - Level
            PetCornerStatBadge(
                label = "Level",
                value = "5",
                accentColor = Color.Blue,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TODAY'S ACTIVITY
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TODAY'S Habits".uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 14.sp, // Slightly forced override for width management
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CategorizedActivityLog(activities, onToggleHabit)

        Spacer(modifier = Modifier.height(80.dp)) // Extra space for scroll
    }
}

/**
 * A categorized version of the activity log that uses expandable sections.
 */
@Composable
fun CategorizedActivityLog(
    activities: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        CozyCategory.entries.forEach { category ->
            val filteredActivities = activities.filter { it.icon == category.name }
            if (filteredActivities.isNotEmpty()) {
                ExpandableDashboardSection(
                    categoryTitle = category.displayName,
                    habits = filteredActivities,
                    onToggleHabit = onToggleHabit
                )
            }
        }
    }
}

@Composable
private fun ExpandableDashboardSection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 14.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = categoryTitle.uppercase(),
                    style = MaterialTheme.typography.headlineMedium, // Increased size/impact
                    fontSize = 16.sp, // Scaled down for width management
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationState),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                habits.forEach { activity ->
                    ActivityLogItem(activity, onToggleHabit)
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(
    activity: ActivityItem,
    onToggleHabit: (ActivityItem) -> Unit
) {
    val itemBgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = MaterialTheme.colorScheme.onSurface
    val isCompleted = activity.currentCount >= activity.targetCount
    val contentAlpha = if (isCompleted) 0.5f else 1.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                if (activity.targetCount <= 1) {
                    onToggleHabit(activity) 
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = itemBgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon Box (Acts as Checkbox for single-step habits)
                if (activity.targetCount <= 1) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), MaterialTheme.shapes.small)
                            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small),
                        contentAlignment = Alignment.Center
                    ) {
                        if (activity.isCompleted) {
                            Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    // Multi-step habits: use a simple bullet point/indicator or hide completely 
                    // since we have the row of checkboxes below.
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("•", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Emoji/Icon
                if (activity.icon.length <= 2) {
                    Text(text = activity.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Description
                Text(
                    text = activity.description,
                    color = contentColor.copy(alpha = contentAlpha),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )

                // Time
                Text(
                    text = activity.time,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Multi-step checkboxes (for habits like Water)
            if (activity.targetCount > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    repeat(activity.targetCount) { index ->
                        val isChecked = index < activity.currentCount
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(
                                    if (isChecked) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), 
                                    MaterialTheme.shapes.small
                                )
                                .border(
                                    1.dp, 
                                    if (isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, 
                                    MaterialTheme.shapes.small
                                )
                                .clickable {
                                    // Clicking an unchecked slot adds a log
                                    if (index == activity.currentCount) {
                                        onToggleHabit(activity)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                            }
                        }
                    }
                }
            }
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
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, if (MaterialTheme.colorScheme.outline != Color.Unspecified) MaterialTheme.colorScheme.outline else borderColor),
        colors = CardDefaults.cardColors(containerColor = if (MaterialTheme.colorScheme.surfaceVariant != Color.Unspecified) MaterialTheme.colorScheme.surfaceVariant else bgColor)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = value.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PetCornerStatBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.Black
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 8.sp
            )
            Text(
                text = value.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}
