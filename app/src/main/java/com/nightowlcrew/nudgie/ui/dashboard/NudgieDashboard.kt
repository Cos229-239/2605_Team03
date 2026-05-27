package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.ui.theme.CardBlueBg
import com.nightowlcrew.nudgie.ui.theme.CardGreenBg
import com.nightowlcrew.nudgie.ui.theme.CardRedBg
import com.nightowlcrew.nudgie.ui.theme.CardYellowBg
import com.nightowlcrew.nudgie.ui.theme.DarkBackground
import com.nightowlcrew.nudgie.ui.theme.ElectricYellow
import com.nightowlcrew.nudgie.ui.theme.HeartRed
import com.nightowlcrew.nudgie.ui.theme.LevelUpBlue
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
import com.nightowlcrew.nudgie.ui.theme.PressStart2P
import com.nightowlcrew.nudgie.ui.theme.SuccessGreen
import com.nightowlcrew.nudgie.ui.theme.cpNeonGreen
import com.nightowlcrew.nudgie.ui.theme.nudgieCardShadow
import com.nightowlcrew.nudgie.ui.theme.spParchment

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Pet : Screen("pet", "Pet", Icons.Filled.AutoAwesome)
    object Learn : Screen("learn", "Learn", Icons.AutoMirrored.Filled.MenuBook)
    object Stats : Screen("stats", "Stats", Icons.AutoMirrored.Filled.ShowChart)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboard(viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val screens = listOf(
        Screen.Home,
        Screen.Pet,
        Screen.Learn,
        Screen.Stats,
        Screen.Settings,
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = {
                            Text(
                                text = screen.label.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )
                        },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                DashboardContent(
                    categorizedActivities = uiState.categorizedActivities,
                    currentTheme = uiState.currentTheme,
                    onToggleHabit = { viewModel.toggleHabitCompletion(it) },
                )
            }
            composable(Screen.Pet.route) {
                PetScreenContent(currentTheme = uiState.currentTheme)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
            // Placeholders for Learn and Stats
            composable(Screen.Learn.route) { ComingSoonScreen() }
            composable(Screen.Stats.route) { ComingSoonScreen() }
        }
    }
}

@Composable
fun ComingSoonScreen() {
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

@Composable
fun PetScreenContent(currentTheme: AppTheme = AppTheme.DEFAULT) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Cushioned horizontal padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MY PET",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 44.sp,
                lineHeight = 52.sp,
                fontFamily = PressStart2P
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Extra spacer for pixel font vertical breathing room
        Spacer(modifier = Modifier.height(32.dp))

        PetHeroContainer(currentTheme = currentTheme)

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
                    currentTheme = currentTheme,
                    modifier = Modifier.weight(1f)
                )
                NewStatCard(
                    label = "ENERGY",
                    value = "62%",
                    icon = Icons.Filled.FlashOn,
                    iconColor = Color(0xFFFFC107),
                    bgColor = CardYellowBg,
                    borderColor = ElectricYellow,
                    currentTheme = currentTheme,
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
                    currentTheme = currentTheme,
                    modifier = Modifier.weight(1f)
                )
                NewStatCard(
                    label = "AGE",
                    value = "5 Days",
                    icon = Icons.Filled.CalendarToday,
                    iconColor = Color.Green,
                    bgColor = CardGreenBg,
                    borderColor = SuccessGreen,
                    currentTheme = currentTheme,
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
fun PetHeroContainer(currentTheme: AppTheme = AppTheme.DEFAULT) {
    if (currentTheme == AppTheme.DEFAULT) {
        // ORIGINAL DEFAULT LOOK
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFFD1D5DB), RoundedCornerShape(24.dp))
                .border(4.dp, DarkBackground, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = """
|       _..._
|     .'     '.
|    /`\     /`\
|   (_*_|   |_*_)
|   (     "     )
|    \         /
|     \  \_/  /
|      '.___.'
                """.trimMargin(),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 12.sp,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = DarkBackground
            )
        }
    } else {
        // THEMED WINDOW (Cyberpunk / Steampunk / Goth / Alien)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            when (currentTheme) {
                AppTheme.GOTH -> AnimatedKitty(color = MaterialTheme.colorScheme.onSurface)
                AppTheme.CYBERPUNK -> AnimatedAlien(baseColor = LevelUpBlue)
                AppTheme.STEAMPUNK -> {
                    val steampunkAscii = """
|
|       _..._
|     .'     '.
|    /`\     /`\
|   (_*_|   |_*_)
|   (     "     ) |\ |\ /|          __+__
|    \         /   \\||//          /     \
|     \  \_/  /  |\|`  /        __/   O   \__
|      '.___.'   \____/       /    \__|__/   \
|       (___)    (___)        \______________/
|     /`     `\  / /                 /|\
|    |         \/ /                 / | \
|    | |     |\  /                 /  |  \
|    | |     | "`           
|    |_|_____|        
|   (___)_____) 
                    """.trimMargin()
                    Text(
                        text = steampunkAscii,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = spParchment
                    )
                }
            }
        }
    }
}



@Composable
fun DashboardContent(
    categorizedActivities: Map<CozyCategory, List<ActivityItem>>,
    currentTheme: AppTheme = AppTheme.DEFAULT,
    onToggleHabit: (ActivityItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp) // Cushioned horizontal padding
    ) {
        // HEADER
        Text(
            text = "NUDGIE",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 44.sp,
                lineHeight = 52.sp,
                fontFamily = PressStart2P
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        // High-density metadata
        Text(
            text = "Level 5 • Baby Stage",
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = PressStart2P,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // TOP HAPPINESS BAR
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium)
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
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = PressStart2P,
                                fontSize = 14.sp
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "85%",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = PressStart2P,
                            fontSize = 14.sp
                        ),
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
                    trackColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // BUDDY WINDOW WITH FLOATING STATS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            PetHeroContainer(currentTheme = currentTheme)

            // Top Left - Happiness
            PetCornerStatBadge(
                label = "Happiness",
                value = "85%",
                icon = Icons.Filled.Favorite,
                currentTheme = currentTheme,
                accentColor = Color(0xFFFF003C), // Corpo Red
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-8).dp, y = (-12).dp)
            )

            // Top Right - Energy
            PetCornerStatBadge(
                label = "Energy",
                value = "62%",
                icon = Icons.Filled.FlashOn,
                currentTheme = currentTheme,
                accentColor = Color(0xFFFCEE0A), // Cyber Yellow
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-12).dp)
            )

            // Bottom Left - Age
            PetCornerStatBadge(
                label = "Age",
                value = "5 Days",
                icon = Icons.Filled.HourglassBottom,
                currentTheme = currentTheme,
                accentColor = cpNeonGreen,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-8).dp, y = 12.dp)
            )

            // Bottom Right - Level
            PetCornerStatBadge(
                label = "Level",
                value = "5",
                icon = Icons.Filled.Star,
                currentTheme = currentTheme,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 12.dp)
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
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PressStart2P),
                fontSize = 18.sp, // Slightly forced override for width management
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CategorizedActivityLog(categorizedActivities, currentTheme, onToggleHabit)

        Spacer(modifier = Modifier.height(80.dp)) // Extra space for scroll
    }
}

/**
 * A categorized version of the activity log that uses expandable sections.
 */
@Composable
fun CategorizedActivityLog(
    categorizedActivities: Map<CozyCategory, List<ActivityItem>>,
    currentTheme: AppTheme,
    onToggleHabit: (ActivityItem) -> Unit
) {
    var expandedCategory by rememberSaveable { mutableStateOf<CozyCategory?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        categorizedActivities.forEach { (category, habits) ->
            ExpandableDashboardSection(
                categoryTitle = category.displayName,
                habits = habits,
                currentTheme = currentTheme,
                expanded = expandedCategory == category,
                onToggleExpand = {
                    expandedCategory = if (expandedCategory == category) null else category
                },
                onToggleHabit = onToggleHabit
            )
        }
    }
}

@Composable
private fun ExpandableDashboardSection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    currentTheme: AppTheme,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleHabit: (ActivityItem) -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .nudgieCardShadow(currentTheme, 4.dp, RoundedCornerShape(12.dp))
                .clickable { onToggleExpand() },
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 14.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = categoryTitle.uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PressStart2P), // Increased size/impact
                    fontSize = 20.sp, // Scaled down for width management
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
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
                    ActivityLogItem(activity, currentTheme, onToggleHabit)
                }
            }
        }
    }
}

@Composable
fun ActivityLogItem(
    activity: ActivityItem,
    currentTheme: AppTheme,
    onToggleHabit: (ActivityItem) -> Unit,
) {
    val itemBgColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val isCompleted = activity.currentCount >= activity.targetCount
    val contentAlpha = if (isCompleted) 0.8f else 1.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, RoundedCornerShape(16.dp))
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
                    // Multistep habits: use a simple bullet point/indicator or hide completely
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Multistep checkboxes (for habits like Water)
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
        ActivityItem(1, "BODY_VITALITY", "Fed Buddy", "08:30", true),
        ActivityItem(2, "BODY_VITALITY", "Played with ball", "10:15", true),
        ActivityItem(3, "MIND_SPACE", "Training session", "12:00", false),
        ActivityItem(4, "DAILY_RHYTHMS", "Walk outside", "15:00", false),
        ActivityItem(5, "DAILY_RHYTHMS", "Evening meal", "18:00", false)
    )
    val mockCategorized = mapOf(
        CozyCategory.BODY_VITALITY to mockActivities.filter { it.icon == "BODY_VITALITY" },
        CozyCategory.MIND_SPACE to mockActivities.filter { it.icon == "MIND_SPACE" },
        CozyCategory.DAILY_RHYTHMS to mockActivities.filter { it.icon == "DAILY_RHYTHMS" }
    )
    NudgieTheme {
        DashboardContent(
            categorizedActivities = mockCategorized,
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
    currentTheme: AppTheme,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium),
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
    icon: ImageVector,
    currentTheme: AppTheme,
    modifier: Modifier = Modifier,
    accentColor: Color = Color.Black,
) {

    val backgroundColor = MaterialTheme.colorScheme.surface

    Surface(
        modifier = modifier.nudgieCardShadow(currentTheme, 2.dp, CircleShape),
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(1.dp, accentColor),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
            Text(
                text = value.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = PressStart2P,
                    fontSize = 12.sp
                ),
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}
