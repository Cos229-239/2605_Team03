package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.ui.theme.BrandGold
import com.nightowlcrew.nudgie.ui.theme.ElectricYellow
import com.nightowlcrew.nudgie.ui.theme.HeartRed
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.LevelUpBlue
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
import com.nightowlcrew.nudgie.ui.theme.SpaceEnergy
import com.nightowlcrew.nudgie.ui.theme.SpaceHappiness
import com.nightowlcrew.nudgie.ui.theme.SpaceLevel
import com.nightowlcrew.nudgie.ui.theme.SpaceSuccess
import com.nightowlcrew.nudgie.ui.theme.SpaceSurface
import com.nightowlcrew.nudgie.ui.theme.SuccessGreen
import com.nightowlcrew.nudgie.ui.theme.cpStatEnergy
import com.nightowlcrew.nudgie.ui.theme.cpStatHappiness
import com.nightowlcrew.nudgie.ui.theme.cpStatLevel
import com.nightowlcrew.nudgie.ui.theme.cpStatSuccess
import com.nightowlcrew.nudgie.ui.theme.gothStatEnergy
import com.nightowlcrew.nudgie.ui.theme.gothStatHappiness
import com.nightowlcrew.nudgie.ui.theme.gothStatLevel
import com.nightowlcrew.nudgie.ui.theme.gothStatSuccess
import com.nightowlcrew.nudgie.ui.theme.spStatEnergy
import com.nightowlcrew.nudgie.ui.theme.spStatHappiness
import com.nightowlcrew.nudgie.ui.theme.spStatLevel
import com.nightowlcrew.nudgie.ui.theme.spStatSuccess

// Helper class to hold dynamic stat colors based on the current theme
data class StatColors(
    val happiness: Color,
    val energy: Color,
    val level: Color,
    val success: Color
)

// Maps the active app theme to its distinct semantic stat colors
fun getThemeStatColors(theme: AppTheme): StatColors {
    return when (theme) {
        AppTheme.CYBERPUNK -> StatColors(cpStatHappiness, cpStatEnergy, cpStatLevel, cpStatSuccess)
        AppTheme.STEAMPUNK -> StatColors(spStatHappiness, spStatEnergy, spStatLevel, spStatSuccess)
        AppTheme.GOTH -> StatColors(gothStatHappiness, gothStatEnergy, gothStatLevel, gothStatSuccess)
        AppTheme.RETRO_SPACE -> StatColors(SpaceHappiness, SpaceEnergy, SpaceLevel, SpaceSuccess)
        else -> StatColors(HeartRed, ElectricYellow, LevelUpBlue, SuccessGreen) // Default
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Tasks : Screen("tasks", "Tasks", Icons.Filled.CheckCircle)
    object Stats : Screen("stats", "Stats", Icons.Filled.Star)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboard(viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    NudgieDashboardContent(
        uiState = uiState,
        onToggleHabit = { viewModel.toggleHabitCompletion(it) },
        onAddHabit = { title, category, frequency -> viewModel.addNewHabit(title, category.name, frequency) },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onUpdateScreenTimeGoal = { hours -> viewModel.updateScreenTimeGoal(hours) },
        onUpdateTheme = { theme -> viewModel.updateTheme(theme) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboardContent(
    uiState: DashboardUiState,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, CozyCategory, Int) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val screens = listOf(
        Screen.Home,
        Screen.Tasks,
        Screen.Stats,
        Screen.Profile,
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NavySurface,
                tonalElevation = 8.dp
            ) {
                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = LavenderText,
                            unselectedTextColor = LavenderText,
                            indicatorColor = Color(0xFF6200EE).copy(alpha = 0.5f) // Glowing purple highlight
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()) // Only apply bottom padding
        ) {
            composable(Screen.Home.route) {
                DashboardContent(
                    categorizedActivities = uiState.categorizedActivities,
                    currentTheme = uiState.currentTheme,
                    petStats = uiState.petStats,
                    onToggleHabit = onToggleHabit,
                    streak = 12, // For demo, can be linked to viewModel later
                    currency = 250 // For demo, can be linked to viewModel later
                )
            }
            composable(Screen.Tasks.route) { ComingSoonScreen("Tasks") }
            composable(Screen.Stats.route) { ComingSoonScreen("Stats") }
            composable(Screen.Profile.route) {
                SettingsContent(
                    activities = uiState.activities,
                    screenTimeGoalMillis = uiState.screenTimeGoalMillis,
                    currentTheme = uiState.currentTheme,
                    onAddHabit = onAddHabit,
                    onDeleteHabit = onDeleteHabit,
                    onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                    onUpdateTheme = onUpdateTheme
                )
            }
            composable(Screen.Settings.route) {
                SettingsContent(
                    activities = uiState.activities,
                    screenTimeGoalMillis = uiState.screenTimeGoalMillis,
                    currentTheme = uiState.currentTheme,
                    onAddHabit = onAddHabit,
                    onDeleteHabit = onDeleteHabit,
                    onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                    onUpdateTheme = onUpdateTheme
                )
            }
        }
    }
}

@Composable
fun ComingSoonScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title.uppercase(), style = MaterialTheme.typography.headlineMedium, color = Color.Gray)
    }
}

@Composable
fun DashboardContent(
    categorizedActivities: Map<CozyCategory, List<ActivityItem>>,
    currentTheme: AppTheme,
    petStats: PetStats,
    onToggleHabit: (ActivityItem) -> Unit,
    streak: Int,
    currency: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground) // Fix white background issue
            .verticalScroll(rememberScrollState())
    ) {
        PetFrame(petStats = petStats, currentTheme = currentTheme, streak = streak, currency = currency)
        Spacer(modifier = Modifier.height(48.dp)) // Move headers down more
        TasksSection(
            categorizedActivities = categorizedActivities,
            currentTheme = currentTheme,
            onToggleHabit = onToggleHabit
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun PetFrame(petStats: PetStats, currentTheme: AppTheme, streak: Int, currency: Int) {
    val statColors = getThemeStatColors(currentTheme)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp) // Elongated height
    ) {
        // Edge-to-edge Background Image
        Image(
            painter = painterResource(id = R.drawable.pethero_dashboard),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Overlaid Streak and Currency Row
            Row(
                modifier = Modifier
                    .statusBarsPadding() // Add padding to avoid overlapping system icons
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak Logic
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val streakIcon = when {
                        streak >= 10 -> "🔥"
                        streak >= 5 -> "⭐"
                        else -> "•"
                    }
                    val fontSize = if (streak >= 25) 22.sp else 20.sp // Increased font sizes
                    Text(text = streakIcon, fontSize = 24.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "$streak Day Streak!",
                        color = Color.White,
                        fontSize = fontSize,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Currency
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💎", fontSize = 22.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = currency.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp // Increased currency font size
                    )
                }
            }

            // Digital Clock Frame and Pet Name wrapper
            Box(contentAlignment = Alignment.BottomCenter) {
                // Digital Clock Frame with custom background asset
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock_date_alarm),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    Column(
                        modifier = Modifier.padding(bottom = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("08:30", fontSize = 72.sp, fontWeight = FontWeight.ExtraBold, color = Color.White) // Prominent Time
                            Text("AM", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp))
                        }
                        Text("Thursday, May 7", fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f)) // Increased font size
                    }
                }

                // Pet Name Box centered and overlapping the bottom
                Surface(
                    color = SpaceSurface.copy(alpha = 0.9f), // Dark Purple
                    shape = RoundedCornerShape(0.dp), // 8-bit block
                    border = BorderStroke(2.dp, NavyOutline),
                    modifier = Modifier.offset(y = 12.dp) // Overlap effect
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Your Pet", color = BrandGold, fontSize = 14.sp, fontWeight = FontWeight.Bold) // Yellow font
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp), tint = BrandGold)
                    }
                }
            }

            // Level & XP Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 8.dp) // Reduced vertical padding
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Level ${petStats.level}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) // Larger font
                        Text("${petStats.xp} / 800 XP", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp) // Larger font
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { petStats.xp / 800f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Interactive Bottom Strip - Enlarged Pet
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Spacer(Modifier.weight(1f))
                // Pixel Art Pet
                Image(
                    painter = painterResource(id = R.drawable.blue_trashpanda),
                    contentDescription = "Your Pet",
                    modifier = Modifier.size(200.dp), // Enlarged to 225.dp
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(32.dp)) // Cushion for overlaid card
        }

        // Stats Box with custom background asset: 1/3 inside the frame, 2/3 outside
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 55.dp) // Narrow the card
                .align(Alignment.BottomCenter)
                .offset(y = 50.dp) // Moved UP to match green arrows (less overlap outside)
                .height(80.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.petstat_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp), // Minimal vertical padding
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("Happiness", "${petStats.happiness}%", statColors.happiness, Icons.Default.Favorite)
                StatItem("Energy", "${petStats.energy}%", statColors.energy, Icons.Default.FlashOn)
                StatItem("Level", "${petStats.level}", statColors.level, Icons.Default.Star)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp)) // Restored icon size
            Spacer(Modifier.width(4.dp))
            Text(label, color = Color.White, fontSize = 14.sp) // White labels as requested
        }
        Text(value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 24.sp) // Prominent value, now White and larger
        LinearProgressIndicator(
            progress = { value.replace("%", "").toFloatOrNull()?.div(100f) ?: 1f },
            modifier = Modifier.width(32.dp).height(2.dp).clip(RoundedCornerShape(1.dp)), // Very slim and short bars
            color = color,
            trackColor = MaterialTheme.colorScheme.background,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun TasksSection(
    categorizedActivities: Map<CozyCategory, List<ActivityItem>>,
    currentTheme: AppTheme,
    onToggleHabit: (ActivityItem) -> Unit
) {
    // Filter categories that have tasks
    val activeCategories = CozyCategory.entries.filter { categorizedActivities[it]?.isNotEmpty() == true }
    
    if (activeCategories.isEmpty()) return

    var selectedCategory by remember { mutableStateOf(activeCategories.first()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Today's Tasks",
                color = LavenderText, // Match mockup
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp // Increased font size (Orange Line)
            )
            Text(
                "View All",
                color = LavenderText, // Match mockup
                fontSize = 16.sp, // Increased font size (Orange Line)
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(16.dp))

        SecondaryTabRow(
            selectedTabIndex = activeCategories.indexOf(selectedCategory),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {},
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(activeCategories.indexOf(selectedCategory)),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            activeCategories.forEach { category ->
                val icon = when (category) {
                    CozyCategory.BODY_VITALITY -> "💪"
                    CozyCategory.MIND_SPACE -> "🧠"
                    CozyCategory.DAILY_RHYTHMS -> "📅"
                    CozyCategory.SELF_CARE_RITUALS -> "✨"
                    CozyCategory.CONNECTIONS -> "🤝"
                }
                Tab(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = icon,
                            fontSize = 20.sp
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val tasksInCategory = categorizedActivities[selectedCategory] ?: emptyList()

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            tasksInCategory.forEach { task ->
                TaskItem(task, currentTheme, onToggleHabit)
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TaskItem(task: ActivityItem, currentTheme: AppTheme, onToggleHabit: (ActivityItem) -> Unit) {
    val isCompleted = task.isCompleted
    val statColors = getThemeStatColors(currentTheme)
    val successColor = statColors.success 

    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if(isCompleted) successColor.copy(alpha=0.5f) else NavyOutline),
        modifier = Modifier.clickable { onToggleHabit(task) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Checkbox Container
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isCompleted) successColor else NavyBackground.copy(alpha = 0.5f))
                    .border(1.dp, if (isCompleted) successColor else NavyOutline, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.width(16.dp))

            // Icon backdrop box
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NavyBackground.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (task.icon.length <= 2) task.icon else "📌", fontSize = 20.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.description, 
                    color = Color.White, 
                    fontSize = 20.sp, // Increased font size (Orange Line)
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(Modifier.height(4.dp))
                
                // Task Progress Bar
                val progress = if (task.targetCount > 0) task.currentCount.toFloat() / task.targetCount else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = if (isCompleted) successColor else MaterialTheme.colorScheme.primary,
                    trackColor = NavyOutline.copy(alpha = 0.3f),
                    strokeCap = StrokeCap.Round
                )
            }

            // Reward
            Text("15 XP", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NudgieDashboardPreview() {
    // Generate some mock activities to match your Figma mockup
    val mockActivities = listOf(
        ActivityItem(id = 1, icon = "</>", description = "Coding Lesson", time = "10:00 AM", isCompleted = true, targetCount = 1, currentCount = 1),
        ActivityItem(id = 2, icon = "📖", description = "Read 20 Pages", time = "1:00 PM", isCompleted = true, targetCount = 1, currentCount = 1),
        ActivityItem(id = 3, icon = "💧", description = "Drink 8 Glasses of Water", time = "All Day", isCompleted = false, targetCount = 8, currentCount = 0),
        ActivityItem(id = 4, icon = "🧘", description = "Meditate 10 Minutes", time = "8:00 PM", isCompleted = false, targetCount = 1, currentCount = 0)
    )

    // Group them into a mock category
    val categorizedActivities = mapOf(
        CozyCategory.MIND_SPACE to mockActivities
    )

    val sampleUiState = DashboardUiState(
        activities = mockActivities,
        categorizedActivities = categorizedActivities,
        currentTheme = AppTheme.RETRO_SPACE,
        petStats = PetStats(level = 5, xp = 450, happiness = 80, energy = 65),
        isLoading = false
    )

    // Wrap the preview in the NudgieTheme targeting the RETRO_SPACE look
    NudgieTheme(appTheme = AppTheme.RETRO_SPACE) {
        NudgieDashboardContent(
            uiState = sampleUiState,
            onToggleHabit = {},
            onAddHabit = { _, _, _ -> },
            onDeleteHabit = {},
            onUpdateScreenTimeGoal = {},
            onUpdateTheme = {}
        )
    }
}
