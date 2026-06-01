package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.ui.theme.BrandGold
import com.nightowlcrew.nudgie.ui.theme.ElectricYellow
import com.nightowlcrew.nudgie.ui.theme.HeartRed
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.LevelUpBlue
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
import com.nightowlcrew.nudgie.ui.theme.SpaceAccent
import com.nightowlcrew.nudgie.ui.theme.SpaceEnergy
import com.nightowlcrew.nudgie.ui.theme.SpaceHappiness
import com.nightowlcrew.nudgie.ui.theme.SpaceLevel
import com.nightowlcrew.nudgie.ui.theme.SpaceOutline
import com.nightowlcrew.nudgie.ui.theme.SpaceSecondaryText
import com.nightowlcrew.nudgie.ui.theme.SpaceSuccess
import com.nightowlcrew.nudgie.ui.theme.SpaceSurface
import com.nightowlcrew.nudgie.ui.theme.SuccessGreen
import com.nightowlcrew.nudgie.ui.theme.VT323
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
    val success: Color,
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
    object Pet : Screen("pet", "Pet", Icons.Filled.Favorite) // New Pet tab
    object Tasks : Screen("tasks", "Tasks", Icons.Filled.CheckCircle)
    object Stats : Screen("stats", "Stats", Icons.Filled.Star)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboard(viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val archivedHabits by viewModel.archivedHabits.collectAsStateWithLifecycle()
    
    NudgieDashboardContent(
        uiState = uiState,
        archivedHabits = archivedHabits,
        onToggleHabit = { viewModel.toggleHabitCompletion(it) },
        onAddHabit = { title, category, frequency, isStock -> viewModel.addNewHabit(title, category, frequency, isStock) },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onUpdateScreenTimeGoal = { hours -> viewModel.updateScreenTimeGoal(hours) },
        onUpdateTheme = { theme -> viewModel.updateTheme(theme) },
        onUpdatePetName = { viewModel.updatePetName(it) },
        onArchiveHabit = { viewModel.archiveHabit(it) },
        onRestoreHabit = { viewModel.restoreHabit(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboardContent(
    uiState: DashboardUiState,
    archivedHabits: List<HabitEntity>,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdatePetName: (String) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val screens = listOf(
        Screen.Home,
        Screen.Pet,
        Screen.Profile,
        Screen.Stats,
        Screen.Tasks,
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
                    currentScreenTimeMillis = uiState.currentScreenTimeMillis,
                    screenTimeGoalMillis = uiState.screenTimeGoalMillis,
                    onToggleHabit = onToggleHabit,
                    onUpdatePetName = onUpdatePetName,
                    onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                    streak = 12, // For demo, can be linked to viewModel later
                    currency = 250 // For demo, can be linked to viewModel later
                )
            }
            composable(Screen.Pet.route) {
                NudgiePetScreen(
                    petStats = uiState.petStats,
                    onUpdatePetName = onUpdatePetName
                )
            }
            composable(Screen.Tasks.route) {
                TasksContent(
                    activities = uiState.activities,
                    archivedHabits = archivedHabits,
                    onToggleHabit = onToggleHabit,
                    onAddHabit = onAddHabit,
                    onArchiveHabit = onArchiveHabit,
                    onRestoreHabit = onRestoreHabit
                )
            }
            composable(Screen.Stats.route) { ComingSoonScreen("Stats") }
            composable(Screen.Profile.route) { ComingSoonScreen("Profile") }
            composable(Screen.Settings.route) {
                SettingsContent(
                    activities = uiState.activities,
                    archivedHabits = archivedHabits,
                    screenTimeGoalMillis = uiState.screenTimeGoalMillis,
                    currentTheme = uiState.currentTheme,
                    onAddHabit = { title, category, freq, isStock -> onAddHabit(title, category, freq, isStock) },
                    onDeleteHabit = onDeleteHabit,
                    onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                    onUpdateTheme = onUpdateTheme,
                    onArchiveHabit = onArchiveHabit,
                    onRestoreHabit = onRestoreHabit
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
fun HeartsRow(happiness: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(3) { index ->
            val isFilled = index < (happiness / 33.4).toInt().coerceAtMost(3)
            Icon(
                imageVector = if (isFilled) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = if (isFilled) HeartRed else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
            if (index < 2) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun PetActionButton(
    label: String,
    iconRes: Int,
    onClick: () -> Unit = {}
) {
    Surface(
        color = SpaceSurface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SpaceOutline.copy(alpha = 0.5f)),
        modifier = Modifier
            .size(width = 85.dp, height = 100.dp)
            .clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontFamily = VT323,
                    fontSize = 14.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun PetActionButtons(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PetActionButton("Customize", R.drawable.zustomize)
        PetActionButton("Food", R.drawable.feed)
        PetActionButton("Play", R.drawable.play)
        PetActionButton("Bath", R.drawable.bath)
    }
}

@Composable
fun NudgiePetScreen(
    petStats: PetStats,
    onUpdatePetName: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pet_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 32.dp)
        ) {
            // Header


            Spacer(modifier = Modifier.height(8.dp))

            // Pet Info Card
            NudgieTopCard(
                petStats = petStats,
                maxXp = 800,
                onUpdateName = onUpdatePetName
            )

            Spacer(modifier = Modifier.weight(0.8f))

            // Character
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.blue_trashpanda),
                    contentDescription = "Nudgie Character",
                    modifier = Modifier
                        .size(280.dp)
                        .offset(x = (-70).dp, y = 15.dp)
                        .graphicsLayer { scaleX = -1f }, // Flip horizontally
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Action Buttons
            PetActionButtons()
        }
    }
}

@Composable
fun NudgieTopCard(
    petStats: PetStats,
    maxXp: Int,
    onUpdateName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(petStats.name) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Surface(
        color = NavySurface.copy(alpha = 0.9f),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingName) {
                    BasicTextField(
                        value = nameInput,
                        onValueChange = { if (it.length <= 13) nameInput = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = VT323,
                            fontSize = 32.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(Color.White),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                onUpdateName(nameInput)
                                isEditingName = false
                                focusManager.clearFocus()
                            }
                        ),
                        modifier = Modifier
                            .width(IntrinsicSize.Min)
                            .focusRequester(focusRequester)
                    )
                } else {
                    Text(
                        text = petStats.name,
                        style = TextStyle(
                            fontFamily = VT323,
                            fontSize = 32.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Name",
                    tint = SpaceSecondaryText,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            if (isEditingName) {
                                onUpdateName(nameInput)
                            } else {
                                nameInput = petStats.name
                            }
                            isEditingName = !isEditingName
                        }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Nudgie",
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 32.sp,
                        color = Color.White
                    )
                )
                HeartsRow(happiness = petStats.happiness)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Level ${petStats.level}",
                        style = TextStyle(
                            fontFamily = VT323,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = SpaceAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "${petStats.xp} / $maxXp XP",
                    style = TextStyle(
                        fontFamily = VT323,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { petStats.xp.toFloat() / maxXp.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = SuccessGreen,
                trackColor = Color.White.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NudgiePetScreenPreview() {
    NudgieTheme {
        NudgiePetScreen(
            petStats = PetStats(name = "Zorg", level = 5, xp = 450, happiness = 80, energy = 65),
            onUpdatePetName = {}
        )
    }
}

@Composable
fun DashboardContent(
    categorizedActivities: Map<CozyCategory, List<ActivityItem>>,
    currentTheme: AppTheme,
    petStats: PetStats,
    currentScreenTimeMillis: Long,
    screenTimeGoalMillis: Long,
    onToggleHabit: (ActivityItem) -> Unit,
    onUpdatePetName: (String) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    streak: Int,
    currency: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground) // Fix white background issue
            .verticalScroll(rememberScrollState())
    ) {
        PetFrame(
            petStats = petStats,
            currentTheme = currentTheme,
            currentScreenTimeMillis = currentScreenTimeMillis,
            screenTimeGoalMillis = screenTimeGoalMillis,
            onUpdatePetName = onUpdatePetName,
            onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
            streak = streak,
            currency = currency
        )
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
fun PetFrame(
    petStats: PetStats,
    currentTheme: AppTheme,
    currentScreenTimeMillis: Long,
    screenTimeGoalMillis: Long,
    onUpdatePetName: (String) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    streak: Int,
    currency: Int
) {
    val statColors = getThemeStatColors(currentTheme)
    var isEditingName by remember { mutableStateOf(value = false) }
    var nameInput by remember { mutableStateOf(petStats.name) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    androidx.compose.runtime.LaunchedEffect(isEditingName) {
        if (isEditingName) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(510.dp) // Slightly increased to accommodate Digital Balance
    ) {
        // ... (rest of the Box content remains largely the same until Level & XP Box)
        // Edge-to-edge Background Image
        Image(
            painter = painterResource(id = R.drawable.pethero_dashboard),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Feathering mask to dissolve the bottom edge into NavyBackground
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, NavyBackground)
                    )
                )
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
                        .height(180.dp)
                        .clip(RoundedCornerShape(50.dp)), // Mask corner artifacts from background asset
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.clock_date_alarm),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,

                    )
                    Column(
                        modifier = Modifier.padding(bottom = 1.dp),
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
                    color = SpaceSurface.copy(alpha = 1f), // Dark Purple
                    shape = RoundedCornerShape(8.dp), // Rounded corners
                    border = BorderStroke(2.dp, NavyOutline),
                    modifier = Modifier.offset(y = 10.dp) // Overlap effect
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), // WORD + 16 padding as requested
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEditingName) {
                            BasicTextField(
                                value = nameInput,
                                onValueChange = { if (it.length <= 13) nameInput = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = BrandGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(BrandGold),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        onUpdatePetName(nameInput)
                                        isEditingName = false
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier
                                    .width(IntrinsicSize.Min) // Expand to fit word
                                    .focusRequester(focusRequester)
                            )
                        } else {
                            Text(
                                petStats.name,
                                color = BrandGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    if (isEditingName) {
                                        onUpdatePetName(nameInput)
                                    } else {
                                        nameInput = petStats.name
                                    }
                                    isEditingName = !isEditingName
                                },
                            tint = BrandGold
                        )
                    }
                }
            }

            // Bars Box (XP + Digital Balance)
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // XP Bar Section
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Level ${petStats.level}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${petStats.xp} / 800 XP", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(4.dp))
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

                // Digital Balance Bar Section
                val goalHours = screenTimeGoalMillis / 3600000f
                val currentHours = currentScreenTimeMillis / 3600000f
                
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Digital Balance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${"%.1f".format(currentHours)}h / ${"%.1f".format(goalHours)}h", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentHours / goalHours.coerceAtLeast(0.1f)).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (currentHours > goalHours) Color.Red else statColors.success,
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
                    modifier = Modifier.size(225.dp), // Enlarged to 225.dp
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.weight(3f))
            }
            Spacer(Modifier.height(32.dp)) // Cushion for overlaid card
        }

        // Stats Box with solid background: 1/3 inside the frame, 2/3 outside
        Surface(
            color = NavySurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, NavyOutline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 55.dp) // Narrow the card
                .align(Alignment.BottomCenter)
                .offset(y = 55.dp) // Adjusted offset for more "overlap" look
                .height(70.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 2.dp), // Minimal vertical padding
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem("Happiness", "${petStats.happiness}%", statColors.happiness, Icons.Default.Favorite)
                StatItem("Energy", "${petStats.energy}%", statColors.energy, Icons.Default.FlashOn)
                StatItem("Level", petStats.level.toString(), statColors.level, Icons.Default.Star)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp)) // Restored icon size
            Spacer(Modifier.width(5.dp))
            Text(label, color = Color.White, fontSize = 16.sp) // White labels as requested
        }
        Spacer(Modifier.height((-8).dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp, modifier = Modifier.offset(y = (-4).dp)) // Prominent value, now White and larger
        Spacer(Modifier.height(1.dp))
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
        Spacer(Modifier.height(5.dp))
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
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp // Increased font size (Orange Line)
            )
            Text(
                "View All",
                color = LavenderText, // Match mockup
                fontSize = 18.sp, // Increased font size (Orange Line)
                fontWeight = FontWeight.Normal
                
            )
        }

        Spacer(Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            activeCategories.forEach { category ->
                val isSelected = selectedCategory == category
                val icon = when (category) {
                    CozyCategory.BODY_VITALITY -> "💪"
                    CozyCategory.MIND_SPACE -> "🧠"
                    CozyCategory.DAILY_RHYTHMS -> "📅"
                    CozyCategory.SELF_CARE_RITUALS -> "✨"
                    CozyCategory.CONNECTIONS -> "🤝"
                }
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(if (isSelected) NavySurface else Color.Transparent)
                        .clickable { selectedCategory = category }
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val color = NavyOutline
                            val cornerRadius = 12.dp.toPx()

                            // Full outline for all tabs (including active)
                            drawRoundRect(
                                color = color,
                                cornerRadius = CornerRadius(cornerRadius),
                                style = Stroke(strokeWidth)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 20.sp)
                }
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
        ActivityItem(id = 1, icon = "</>", description = "Coding Lesson", category = CozyCategory.MIND_SPACE.name, time = "10:00 AM", isCompleted = true, targetCount = 1, currentCount = 1),
        ActivityItem(id = 2, icon = "📖", description = "Read 20 Pages", category = CozyCategory.MIND_SPACE.name, time = "1:00 PM", isCompleted = true, targetCount = 1, currentCount = 1),
        ActivityItem(id = 3, icon = "💧", description = "Drink 8 Glasses of Water", category = CozyCategory.BODY_VITALITY.name, time = "All Day", isCompleted = false, targetCount = 8, currentCount = 0),
        ActivityItem(id = 4, icon = "🧘", description = "Meditate 10 Minutes", category = CozyCategory.MIND_SPACE.name, time = "8:00 PM", isCompleted = false, targetCount = 1, currentCount = 0)
    )

    // Group them into a mock category
    val categorizedActivities = mapOf(
        CozyCategory.MIND_SPACE to mockActivities
    )

    val sampleUiState = DashboardUiState(
        activities = mockActivities,
        categorizedActivities = categorizedActivities,
        currentScreenTimeMillis = 3600000L,
        screenTimeGoalMillis = 14400000L,
        currentTheme = AppTheme.RETRO_SPACE,
        petStats = PetStats(name = "Zorg", level = 5, xp = 450, happiness = 80, energy = 65),
        isLoading = false
    )

    // Wrap the preview in the NudgieTheme targeting the RETRO_SPACE look
    NudgieTheme(appTheme = AppTheme.RETRO_SPACE) {
        NudgieDashboardContent(
            uiState = sampleUiState,
            archivedHabits = emptyList(),
            onToggleHabit = {},
            onAddHabit = { _, _, _, _ -> },
            onDeleteHabit = {},
            onUpdateScreenTimeGoal = {},
            onUpdateTheme = {},
            onUpdatePetName = {},
            onArchiveHabit = {},
            onRestoreHabit = {}
        )
    }
}
