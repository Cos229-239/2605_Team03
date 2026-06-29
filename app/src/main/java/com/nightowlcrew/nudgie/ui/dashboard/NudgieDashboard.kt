package com.nightowlcrew.nudgie.ui.dashboard

import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.data.AccessoryCategory
import com.nightowlcrew.nudgie.data.AccessoryItem
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.services.WalkTrackingService
import com.nightowlcrew.nudgie.ui.theme.BrandGold
import com.nightowlcrew.nudgie.ui.theme.ElectricYellow
import com.nightowlcrew.nudgie.ui.theme.HeartRed
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.LevelUpBlue
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.NudgiePurple
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
import com.nightowlcrew.nudgie.utils.PetAssetManager
import com.nightowlcrew.nudgie.utils.PetType
import com.nightowlcrew.nudgie.utils.showReminderTimePicker
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.roundToInt
import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import com.nightowlcrew.nudgie.data.PersonalityType

data class StatColors(val happiness: Color, val energy: Color, val level: Color, val success: Color)

fun getThemeStatColors(theme: AppTheme): StatColors {
    return when (theme) {
        AppTheme.CYBERPUNK -> StatColors(cpStatHappiness, cpStatEnergy, cpStatLevel, cpStatSuccess)
        AppTheme.STEAMPUNK -> StatColors(spStatHappiness, spStatEnergy, spStatLevel, spStatSuccess)
        AppTheme.GOTH -> StatColors(gothStatHappiness, gothStatEnergy, gothStatLevel, gothStatSuccess)
        AppTheme.RETRO_SPACE -> StatColors(SpaceHappiness, SpaceEnergy, SpaceLevel, SpaceSuccess)
        else -> StatColors(HeartRed, ElectricYellow, LevelUpBlue, SuccessGreen)
    }
}

@Composable
fun NudgieProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    trackColor: Color = color.copy(alpha = 0.2f)
) {
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50)),
        color = color,
        trackColor = trackColor,
        strokeCap = StrokeCap.Round
    )
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Default.FlashOn)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Filled.Pets)
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Pet : Screen("pet", "Pet", Icons.Filled.Favorite)
    object Tasks : Screen("tasks", "Tasks", Icons.Filled.CheckCircle)
    object Stats : Screen("stats", "Stats", Icons.Filled.Star)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboard(viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val statsUiState by viewModel.statsUiState.collectAsStateWithLifecycle()
    val archivedHabits by viewModel.archivedHabits.collectAsStateWithLifecycle()
    val isOnboardingComplete by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()
    Column {
        PersonalityTestSwitcher(viewModel = viewModel)

        NudgieDashboardContent(
            uiState = uiState,
            statsUiState = statsUiState,
            archivedHabits = archivedHabits,
            isOnboardingComplete = isOnboardingComplete,
            onToggleHabit = { viewModel.toggleHabitCompletion(it) },
            onAddHabit = { title, category, frequency, isStock ->
                viewModel.addNewHabit(
                    title,
                    category,
                    frequency,
                    isStock
                )
            },
            onDeleteHabit = { id -> viewModel.deleteHabit(id) },
            onUpdateScreenTimeGoal = { hours -> viewModel.updateScreenTimeGoal(hours) },
            onUpdateTheme = { theme -> viewModel.updateTheme(theme) },
            onUpdateOverlayEnabled = { viewModel.updateOverlayEnabled(it) },
            onUpdatePetName = { viewModel.updatePetName(it) },
            onUpdatePetType = { viewModel.updatePetType(it) },
            onArchiveHabit = { viewModel.archiveHabit(it) },
            onRestoreHabit = { viewModel.restoreHabit(it) },
            onBuyAccessory = { viewModel.buyAccessory(it) },
            onEquipAccessory = { viewModel.equipAccessory(it) },
            onPetTheNudgie = { viewModel.petTheNudgie() },
            onReplyToPet = { viewModel.replyToPet(it) },
            onSignOut = { viewModel.signOut() },
            onLinkWithEmail = { email, password -> viewModel.linkWithEmail(email, password) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboardContent(
    uiState: DashboardUiState,
    statsUiState: StatsUiState,
    archivedHabits: List<HabitEntity>,
    isOnboardingComplete: Boolean,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onDeleteHabit: (String) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdateOverlayEnabled: (Boolean) -> Unit,
    onUpdatePetName: (String) -> Unit,
    onUpdatePetType: (PetType) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    onBuyAccessory: (AccessoryItem) -> Unit,
    onEquipAccessory: (AccessoryItem) -> Unit,
    onPetTheNudgie: () -> Unit,
    onReplyToPet: (String) -> Unit,
    onSignOut: () -> Unit,
    onLinkWithEmail: (String, String) -> Unit,
    startDestination: String = Screen.Splash.route
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var showPetSelection by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }

    if (showPetSelection) {
        PetSelectionDialog(
            onDismissRequest = { showPetSelection = false },
            onPetSelected = onUpdatePetType,
            currentPetType = uiState.currentPetType,
            isNudgieUnlocked = uiState.isNudgieUnlocked
        )
    }

    if (showShopDialog) {
        ShopDialog(
            currency = uiState.petStats.currency,
            accessories = uiState.petStats.accessories,
            onBuy = onBuyAccessory,
            onEquip = onEquipAccessory,
            onDismiss = { showShopDialog = false }
        )
    }

    val screens = listOf(Screen.Home, Screen.Pet, Screen.Profile, Screen.Stats, Screen.Tasks)

    Scaffold(
        bottomBar = {
            val showBottomBar = currentDestination?.route != Screen.Splash.route && 
                               currentDestination?.route != Screen.Onboarding.route
            if (showBottomBar) {
                NavigationBar(containerColor = NavySurface, tonalElevation = 8.dp) {
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
                                selectedIconColor = MaterialTheme.colorScheme.onSurface,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedIconColor = LavenderText,
                                unselectedTextColor = LavenderText,
                                indicatorColor = NudgiePurple.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Splash.route) {
                NudgieSplashScreen(onSplashFinished = {
                    val targetRoute = if (isOnboardingComplete) Screen.Home.route else Screen.Onboarding.route
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Onboarding.route) {
                com.nightowlcrew.nudgie.ui.onboarding.OnboardingScreen(
                    viewModel = viewModel(factory = NudgieViewModel.Factory), // Reusing the same ViewModel
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                DashboardContent(
                    categorizedActivities = uiState.categorizedActivities,
                    currentTheme = uiState.currentTheme,
                    petStats = uiState.petStats,
                    currentPetType = uiState.currentPetType,
                    currentScreenTimeMillis = uiState.currentScreenTimeMillis,
                    screenTimeGoalMillis = uiState.screenTimeGoalMillis,
                    onToggleHabit = onToggleHabit,
                    onUpdatePetName = onUpdatePetName,
                    onDigitalBalanceDoubleTap = { navController.navigate("tasks?openSlider=true") },
                    streak = 12,
                    currency = uiState.petStats.currency,
                    onPetTheNudgie = onPetTheNudgie
                )
            }
            composable(Screen.Pet.route) {
                NudgiePetScreen(
                    petStats = uiState.petStats,
                    currentPetType = uiState.currentPetType,
                    currentTheme = uiState.currentTheme,
                    onUpdatePetName = onUpdatePetName,
                    onCustomizeClick = { showPetSelection = true },
                    onShopClick = { showShopDialog = true },
                    onPetTheNudgie = onPetTheNudgie,
                    onReplyToPet = onReplyToPet
                )
            }
            composable(
                route = "tasks?openSlider={openSlider}",
                arguments = listOf(navArgument("openSlider") { type = NavType.BoolType; defaultValue = false })
            ) { backStackEntry ->
                val openSlider = backStackEntry.arguments?.getBoolean("openSlider") ?: false
                TasksContent(
                    activities = uiState.activities,
                    archivedHabits = archivedHabits,
                    screenTimeGoalMillis = uiState.screenTimeGoalMillis,
                    currentTheme = uiState.currentTheme,
                    onToggleHabit = onToggleHabit,
                    onAddHabit = onAddHabit,
                    onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                    onArchiveHabit = onArchiveHabit,
                    onRestoreHabit = onRestoreHabit,
                    onDeleteHabit = onDeleteHabit,
                    initialOpenSlider = openSlider
                )
            }
            composable(Screen.Stats.route) { StatsContent(statsUiState) }
            composable(Screen.Profile.route) {
                ProfileContent(
                    uiState = uiState,
                    statsUiState = statsUiState,
                    onUpdatePetName = onUpdatePetName,
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsContent(
                    currentTheme = uiState.currentTheme,
                    overlayEnabled = uiState.overlayEnabled,
                    isAnonymous = uiState.isAnonymous,
                    onUpdateTheme = onUpdateTheme,
                    onUpdateOverlayEnabled = onUpdateOverlayEnabled,
                    onSignOut = onSignOut,
                    onLinkWithEmail = onLinkWithEmail
                )
            }
        }
    }
}

@Composable
fun ComingSoonScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title.uppercase(), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
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
                tint = if (isFilled) HeartRed else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
            if (index < 2) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun PetActionButton(label: String, iconRes: Int, onClick: () -> Unit = {}) {
    Surface(
        color = SpaceSurface.copy(alpha = 0.8f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SpaceOutline.copy(alpha = 0.5f)),
        modifier = Modifier.size(width = 85.dp, height = 100.dp).clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(8.dp)) {
            Image(painter = painterResource(id = iconRes), contentDescription = label, modifier = Modifier.size(45.dp))
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
fun PetActionButtons(
    onCustomizeClick: () -> Unit,
    onShopClick: () -> Unit,
    onPlayClick: () -> Unit, // <--- ADDED THIS PARAMETER
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PetActionButton("Customize", R.drawable.zustomize, onClick = onCustomizeClick)
        PetActionButton("Shop", android.R.drawable.ic_menu_manage, onClick = onShopClick)
        PetActionButton("Food", R.drawable.feed)
        PetActionButton("Play", R.drawable.play, onClick = onPlayClick) // <--- WIRED THE CLICK HERE
    }
}

@Composable
fun NudgiePetScreen(
    petStats: PetStats,
    currentPetType: PetType,
    currentTheme: AppTheme,
    onUpdatePetName: (String) -> Unit,
    onCustomizeClick: () -> Unit,
    onShopClick: () -> Unit,
    onPetTheNudgie: () -> Unit,
    onReplyToPet: (String) -> Unit
) {
    val context = LocalContext.current
    val isWalkActive by WalkTrackingService.isServiceRunning.collectAsStateWithLifecycle()
    val progressManager = remember { com.nightowlcrew.nudgie.utils.WalkProgressManager(context) }

    // --- STATE MANAGEMENT ---
    var showPlayMenu by remember { mutableStateOf(false) }
    var showWalkSetup by remember { mutableStateOf(false) }
    var showEarlyTerminationDialog by remember { mutableStateOf(false) }
    var pendingWalkMode by remember { mutableStateOf<WalkTrackingMode?>(null) }
    var pendingTargetSteps by remember { mutableStateOf(0) }
    var pendingTargetDistance by remember { mutableStateOf(0) }

    // Helper to start the service
    fun startWalkService(ctx: android.content.Context, mode: WalkTrackingMode, distance: Int, steps: Int) {
        val intent = Intent(ctx, WalkTrackingService::class.java).apply {
            action = WalkTrackingService.ACTION_START
            putExtra(WalkTrackingService.EXTRA_TRACKING_MODE, mode.name)
            putExtra(WalkTrackingService.EXTRA_TARGET_STEPS, steps)
            putExtra(WalkTrackingService.EXTRA_TARGET_DISTANCE, distance.toFloat())
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ctx.startForegroundService(intent)
        } else {
            ctx.startService(intent)
        }
    }

    // --- PERMISSION LAUNCHER & SERVICE TRIGGER ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        val stepsGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true
        } else true

        val notificationsGranted = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] == true
        } else true

        val mode = pendingWalkMode
        if (mode != null && notificationsGranted) {
            val canStartGps = (mode == WalkTrackingMode.DISTANCE || mode == WalkTrackingMode.BOTH) && locationGranted
            val canStartSteps = (mode == WalkTrackingMode.STEPS || mode == WalkTrackingMode.BOTH) && stepsGranted

            if (canStartGps || canStartSteps) {
                Toast.makeText(context, "Starting walk!🐾", Toast.LENGTH_SHORT).show()
                startWalkService(context, mode, pendingTargetDistance, pendingTargetSteps)
            } else {
                Toast.makeText(context, "Permissions required to track walk.", Toast.LENGTH_LONG).show()
            }
        } else if (!notificationsGranted) {
            Toast.makeText(context, "Notifications are required to track walks.", Toast.LENGTH_LONG).show()
        }
        pendingWalkMode = null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pet_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(bottom = 32.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            NudgieTopCard(petStats = petStats, maxXp = 800, onUpdateName = onUpdatePetName)

            Spacer(modifier = Modifier.weight(0.8f))

            // THE NEW INTERACTIVE PLAYPEN
            Box(modifier = Modifier.fillMaxWidth().weight(2f), contentAlignment = Alignment.Center) {
                val isEvolved = petStats.level >= 10
                val currentPetImage = if (isEvolved) R.drawable.zustomize else PetAssetManager.getPetDrawable(currentPetType)
                val equippedClothes = petStats.accessories.filter { it.isEquipped && it.category == AccessoryCategory.CLOTHES }
                val activeToy = petStats.accessories.firstOrNull { it.isEquipped && it.category == AccessoryCategory.TOYS }

                InteractivePetPlaypen(
                    basePetRes = currentPetImage,
                    equippedClothes = equippedClothes,
                    activeToy = activeToy,
                    petMessage = petStats.message,
                    currentTheme = currentTheme,
                    onPetTheNudgie = onPetTheNudgie
                )
            }

            if (petStats.isAwaitingReply) {
                Spacer(modifier = Modifier.height(16.dp))
                ChatInputBox(onSend = { replyText -> onReplyToPet(replyText) })
            }

            Spacer(modifier = Modifier.weight(0.5f))

            if (isWalkActive) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.Center) {
                    PetActionButton(
                        label = "Stop Walk",
                        iconRes = R.drawable.play, // Swap with a stop icon if you have one
                        onClick = { showEarlyTerminationDialog = true }
                    )
                }
            } else {
                PetActionButtons(
                    onCustomizeClick = onCustomizeClick,
                    onShopClick = onShopClick,
                    onPlayClick = {
                        val savedProgress = progressManager.loadProgressIfValid()
                        if (savedProgress != null) {
                            startWalkService(context, savedProgress.mode, savedProgress.targetDistance.toInt(), savedProgress.targetSteps)
                        } else {
                            showPlayMenu = true
                        }
                    }
                )
            }
        }

        // --- OVERLAY DIALOGS ---

        if (showEarlyTerminationDialog) {
            EarlyTerminationDialog(
                onDismiss = { showEarlyTerminationDialog = false },
                onSaveProgress = {
                    showEarlyTerminationDialog = false
                    context.startService(Intent(context, WalkTrackingService::class.java).apply { action = WalkTrackingService.ACTION_STOP })
                },
                onClearProgress = {
                    showEarlyTerminationDialog = false
                    progressManager.clearProgress()
                    context.startService(Intent(context, WalkTrackingService::class.java).apply { action = WalkTrackingService.ACTION_STOP })
                }
            )
        }

        // 1. The Play Menu
        if (showPlayMenu) {
            PlayMenuModal(
                onDismissRequest = { showPlayMenu = false },
                onOptionSelected = { option ->
                    showPlayMenu = false
                    if (option == PlayOption.WALK) {
                        showWalkSetup = true // Triggers the Setup Dialog
                    } else {
                        Toast.makeText(context, "${option.displayName} coming soon!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // 2. The Tracking Mode Setup
        if (showWalkSetup) {
            WalkSetupDialog(
                nudgieName = petStats.name,
                onDismissRequest = { showWalkSetup = false },
                onStartWalk = { trackingMode, targetDistance, targetSteps ->
                    showWalkSetup = false
                    pendingWalkMode = trackingMode
                    pendingTargetDistance = targetDistance
                    pendingTargetSteps = targetSteps

                    val permissionsToRequest = mutableListOf<String>()

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (trackingMode == WalkTrackingMode.DISTANCE || trackingMode == WalkTrackingMode.BOTH) {
                        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
                        permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                    if (trackingMode == WalkTrackingMode.STEPS || trackingMode == WalkTrackingMode.BOTH) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
                        }
                    }

                    // Fire the permission launcher!
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                }
            )
        }
    }
}

@Composable
fun InteractivePetPlaypen(
    basePetRes: Int,
    equippedClothes: List<AccessoryItem>,
    activeToy: AccessoryItem?,
    petMessage: String?,
    currentTheme: AppTheme,
    onPetTheNudgie: () -> Unit
) {
    var toyOffsetX by remember { mutableStateOf(150f) }
    var toyOffsetY by remember { mutableStateOf(0f) }

    val petOffsetX by animateFloatAsState(
        targetValue = if (activeToy != null) toyOffsetX - 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "petChaseX"
    )
    val petOffsetY by animateFloatAsState(
        targetValue = if (activeToy != null) toyOffsetY else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "petChaseY"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // The Chasing Pet
        Box(
            modifier = Modifier.offset { IntOffset(petOffsetX.roundToInt(), petOffsetY.roundToInt()) },
            contentAlignment = Alignment.Center
        ) {
            DressedUpPet(
                basePetRes = basePetRes,
                equippedAccessories = equippedClothes,
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer { scaleX = -1f }, // Flips the pet horizontally
                onClick = onPetTheNudgie
            )

            // The Speech Bubble
            if (petMessage != null) {
                SpeechBubble(
                    message = petMessage,
                    currentTheme = currentTheme,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = 90.dp, y = (-40).dp)
                        .graphicsLayer { scaleX = -1f } // Unflip the text inside the flipped parent
                )
            }
        }

        // The Draggable Toy
        if (activeToy != null) {
            Image(
                painter = painterResource(id = activeToy.assetResId),
                contentDescription = activeToy.name,
                modifier = Modifier
                    .size(80.dp)
                    .offset { IntOffset(toyOffsetX.roundToInt(), toyOffsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            toyOffsetX += dragAmount.x
                            toyOffsetY += dragAmount.y
                        }
                    }
            )
        }
    }
}

@Composable
fun NudgieTopCard(petStats: PetStats, maxXp: Int, onUpdateName: (String) -> Unit, modifier: Modifier = Modifier) {
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(petStats.name) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Surface(color = NavySurface.copy(alpha = 0.9f), shape = RoundedCornerShape(20.dp), modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isEditingName) {
                    BasicTextField(
                        value = nameInput, onValueChange = { if (it.length <= 13) nameInput = it },
                        singleLine = true, textStyle = TextStyle(fontFamily = VT323, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold),
                        cursorBrush = SolidColor(Color.White), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onUpdateName(nameInput); isEditingName = false; focusManager.clearFocus() }),
                        modifier = Modifier.width(IntrinsicSize.Min).focusRequester(focusRequester)
                    )
                } else {
                    Text(text = petStats.name, style = TextStyle(fontFamily = VT323, fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Bold))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = SpaceSecondaryText, modifier = Modifier.size(24.dp).clickable {
                    if (isEditingName) onUpdateName(nameInput) else nameInput = petStats.name
                    isEditingName = !isEditingName
                })
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Nudgie",
                    style = TextStyle(fontFamily = VT323, fontSize = 32.sp, color = Color.White)
                )
                HeartsRow(happiness = petStats.happiness)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Level ${petStats.level}", style = TextStyle(fontFamily = VT323, fontSize = 20.sp, color = Color.White))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.Pets, contentDescription = null, tint = SpaceAccent, modifier = Modifier.size(18.dp))
                }
                Text(text = "${petStats.xp} / $maxXp XP", style = TextStyle(fontFamily = VT323, fontSize = 18.sp, color = Color.White))
            }
            Spacer(modifier = Modifier.height(12.dp))
            NudgieProgressBar(
                progress = petStats.xp.toFloat() / maxXp.toFloat(),
                color = SuccessGreen,
                modifier = Modifier.height(12.dp),
                trackColor = Color.White.copy(alpha = 0.2f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NudgiePetScreenPreview() {
    NudgieTheme {
        NudgiePetScreen(
            petStats = PetStats(name = "Zorg", level = 1, xp = 0, happiness = 80, energy = 65),
            currentPetType = PetType.BLUE,
            currentTheme = AppTheme.DEFAULT,
            onUpdatePetName = {},
            onCustomizeClick = {},
            onShopClick = {},
            onPetTheNudgie = {},
            onReplyToPet = {}
        )
    }
}

@Composable
fun DashboardContent(
    categorizedActivities: Map<CozyCategory, List<ActivityItem>>,
    currentTheme: AppTheme,
    petStats: PetStats,
    currentPetType: PetType,
    currentScreenTimeMillis: Long,
    screenTimeGoalMillis: Long,
    onToggleHabit: (ActivityItem) -> Unit,
    onUpdatePetName: (String) -> Unit,
    onDigitalBalanceDoubleTap: () -> Unit,
    streak: Int,
    currency: Int,
    onPetTheNudgie: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
    ) {
        PetFrame(
            petStats = petStats,
            currentTheme = currentTheme,
            currentPetType = currentPetType,
            currentScreenTimeMillis = currentScreenTimeMillis,
            screenTimeGoalMillis = screenTimeGoalMillis,
            onUpdatePetName = onUpdatePetName,
            onDigitalBalanceDoubleTap = onDigitalBalanceDoubleTap,
            streak = streak,
            currency = currency,
            onPetTheNudgie = onPetTheNudgie
        )
        Spacer(modifier = Modifier.height(48.dp))
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
    petStats: PetStats, currentTheme: AppTheme, currentPetType: PetType, currentScreenTimeMillis: Long, screenTimeGoalMillis: Long,
    onUpdatePetName: (String) -> Unit, onDigitalBalanceDoubleTap: () -> Unit, streak: Int, currency: Int, onPetTheNudgie: () -> Unit
) {
    val statColors = getThemeStatColors(currentTheme)
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(petStats.name) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(isEditingName) { if (isEditingName) focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .background(NavyBackground)
    ) {
        // Edge-to-edge Background Image
        Image(
            painter = painterResource(id = R.drawable.pethero_dashboard),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Feathering mask
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
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val streakIcon = when {
                        streak >= 10 -> "🔥"
                        streak >= 5 -> "⭐"
                        else -> "•"
                    }
                    val fontSize = if (streak >= 25) 22.sp else 20.sp
                    Text(text = streakIcon, fontSize = 24.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(text = "$streak Day Streak!", color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💎", fontSize = 22.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(text = currency.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }

            Box(contentAlignment = Alignment.BottomCenter) {
                Box(
                    modifier = Modifier
                        .width(320.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(50.dp)),
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
                            Text("08:30", fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
                            Text("AM", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                        }
                        Text("Thursday, May 7", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                    }
                }

                Surface(
                    color = SpaceSurface.copy(alpha = 1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(2.dp, NavyOutline),
                    modifier = Modifier.offset(y = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isEditingName) {
                            BasicTextField(
                                value = nameInput, onValueChange = { if (it.length <= 13) nameInput = it }, singleLine = true,
                                textStyle = TextStyle(color = BrandGold, fontSize = 14.sp, fontWeight = FontWeight.Bold),
                                cursorBrush = SolidColor(BrandGold), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { onUpdatePetName(nameInput); isEditingName = false; focusManager.clearFocus() }),
                                modifier = Modifier.width(IntrinsicSize.Min).focusRequester(focusRequester)
                            )
                        } else {
                            Text(petStats.name, color = BrandGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp).clickable {
                            if (isEditingName) onUpdatePetName(nameInput) else nameInput = petStats.name; isEditingName = !isEditingName
                        }, tint = BrandGold)
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth(0.9f).padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Level ${petStats.level}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${petStats.xp} / 800 XP", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    NudgieProgressBar(
                        progress = petStats.xp / 800f,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.height(8.dp)
                    )
                }

                val goalHours = screenTimeGoalMillis / 3600000f
                val currentHours = currentScreenTimeMillis / 3600000f
                Column(
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = { onDigitalBalanceDoubleTap() })
                    }
                ) {
                    NudgieProgressBar(
                        progress = (currentHours / goalHours.coerceAtLeast(0.1f)).coerceIn(0f, 1f),
                        color = if (currentHours > goalHours) Color.Red else statColors.success,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.height(8.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Digital Balance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${"%.1f".format(currentHours)}h / ${"%.1f".format(goalHours)}h", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = ( 70).dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(Modifier.weight(1f))
            Box(contentAlignment = Alignment.BottomCenter) {
                val isEvolved = petStats.level >= 10
                val currentPetImage = if (isEvolved) R.drawable.zustomize else PetAssetManager.getPetDrawable(currentPetType)
                DressedUpPet(
                    basePetRes = currentPetImage,
                    equippedAccessories = petStats.accessories.filter { it.isEquipped },
                    modifier = Modifier.size(if (isEvolved) 275.dp else 225.dp),
                    onClick = onPetTheNudgie
                )
                if (petStats.message != null) {
                    SpeechBubble(
                        message = petStats.message,
                        currentTheme = currentTheme,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 40.dp, y = (-20).dp)
                    )
                }
            }
            Spacer(Modifier.weight(3f))
        }
    }
}

@Composable
fun ActivityLogItem(
    activity: ActivityItem,
    currentTheme: AppTheme,
    onToggleHabit: (ActivityItem) -> Unit,
) {
    val context = LocalContext.current
    val itemBgColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val isCompleted = activity.currentCount >= activity.targetCount
    val contentAlpha = if (isCompleted) 0.8f else 1.0f
    val displayTime = remember { mutableStateOf(activity.time) }

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
                if (activity.targetCount <= 1) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f), CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (activity.isCompleted) {
                            Box(modifier = Modifier.size(12.dp).background(MaterialTheme.colorScheme.primary, CircleShape))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                if (activity.icon.length <= 2) {
                    Text(text = activity.icon, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Text(
                    text = activity.description,
                    color = contentColor.copy(alpha = contentAlpha),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = displayTime.value,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        showReminderTimePicker(context, activity.description) { formattedTime ->
                            displayTime.value = formattedTime
                        }
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Set Reminder",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(5.dp))
            Text(label, color = Color.White, fontSize = 16.sp)
        }
        Spacer(Modifier.height((-8).dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp, modifier = Modifier.offset(y = (-4).dp))
        Spacer(Modifier.height(1.dp))
        NudgieProgressBar(
            progress = value.replace("%", "").toFloatOrNull()?.div(100f) ?: 1f,
            color = color,
            trackColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.width(32.dp).height(2.dp)
        )
    }
}

@Composable
fun TasksSection(categorizedActivities: Map<CozyCategory, List<ActivityItem>>, currentTheme: AppTheme, onToggleHabit: (ActivityItem) -> Unit) {
    val activeCategories = CozyCategory.entries.filter { categorizedActivities[it]?.isNotEmpty() == true }
    if (activeCategories.isEmpty()) return
    var selectedCategory by remember { mutableStateOf(activeCategories.first()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(5.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Today's Tasks", color = LavenderText, fontWeight = FontWeight.Normal, fontSize = 18.sp)
            Text("View All", color = LavenderText, fontSize = 18.sp, fontWeight = FontWeight.Normal)
        }
        Spacer(Modifier.height(5.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            activeCategories.forEach { category ->
                val isSelected = selectedCategory == category
                val icon = when (category) { CozyCategory.BODY_VITALITY -> "💪"; CozyCategory.MIND_SPACE -> "🧠"; CozyCategory.DAILY_RHYTHMS -> "📅"; CozyCategory.SELF_CARE_RITUALS -> "✨"; CozyCategory.CONNECTIONS -> "🤝" }
                Box(
                    modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(if (isSelected) NavySurface else Color.Transparent).clickable { selectedCategory = category }
                        .drawBehind { drawRoundRect(color = NavyOutline, cornerRadius = CornerRadius(12.dp.toPx()), style = Stroke(1.dp.toPx())) },
                    contentAlignment = Alignment.Center
                ) { Text(text = icon, fontSize = 20.sp) }
            }
        }
        Spacer(Modifier.height(16.dp))
        val tasksInCategory = categorizedActivities[selectedCategory] ?: emptyList()
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            tasksInCategory.forEach { task -> TaskItem(task, currentTheme, onToggleHabit); Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
fun TaskItem(task: ActivityItem, currentTheme: AppTheme, onToggleHabit: (ActivityItem) -> Unit) {
    val isCompleted = task.isCompleted
    val statColors = getThemeStatColors(currentTheme)
    val successColor = statColors.success
    val context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if(isCompleted) successColor.copy(alpha=0.5f) else NavyOutline),
        modifier = Modifier.clickable { onToggleHabit(task) }
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
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
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(NavyBackground.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Text(text = if (task.icon.length <= 2) task.icon else "📌", fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.description, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                val progress = if (task.targetCount > 0) task.currentCount.toFloat() / task.targetCount else 0f
                NudgieProgressBar(
                    progress = progress,
                    color = if (isCompleted) successColor else MaterialTheme.colorScheme.primary,
                    trackColor = NavyOutline.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth(0.8f).height(4.dp)
                )
            }
            IconButton(
                onClick = {
                    showReminderTimePicker(context, task.description) { formattedTime ->
                        // This invokes your time picker utils
                    }
                },
                modifier = Modifier.size(32.dp)
            ) { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Set Reminder", tint = BrandGold) }
            Spacer(Modifier.width(8.dp))
            Text("15 XP", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NudgieDashboardPreview() {
    val mockActivities = listOf(
        ActivityItem(id = "1", icon = "</>", description = "Coding Lesson", category = CozyCategory.MIND_SPACE.name, time = "10:00 AM", isCompleted = true, targetCount = 1, currentCount = 1),
        ActivityItem(id = "2", icon = "📖", description = "Read 20 Pages", category = CozyCategory.MIND_SPACE.name, time = "1:00 PM", isCompleted = true, targetCount = 1, currentCount = 1),
        ActivityItem(id = "3", icon = "💧", description = "Drink 8 Glasses of Water", category = CozyCategory.BODY_VITALITY.name, time = "All Day", isCompleted = false, targetCount = 8, currentCount = 0),
        ActivityItem(id = "4", icon = "🧘", description = "Meditate 10 Minutes", category = CozyCategory.MIND_SPACE.name, time = "8:00 PM", isCompleted = false, targetCount = 1, currentCount = 0)
    )

    val categorizedActivities = mapOf(
        CozyCategory.MIND_SPACE to mockActivities
    )

    val sampleUiState = DashboardUiState(
        activities = mockActivities,
        categorizedActivities = categorizedActivities,
        currentScreenTimeMillis = 3600000L,
        screenTimeGoalMillis = 14400000L,
        currentTheme = AppTheme.RETRO_SPACE,
        petStats = PetStats(name = "Zorg", level = 1, xp = 0, happiness = 80, energy = 65),
        isLoading = false
    )

    NudgieTheme(appTheme = AppTheme.RETRO_SPACE) {
        NudgieDashboardContent(
            uiState = sampleUiState,
            statsUiState = StatsUiState(),
            archivedHabits = emptyList(),
            isOnboardingComplete = true,
            onToggleHabit = {},
            onAddHabit = { _, _, _, _ -> },
            onDeleteHabit = {},
            onUpdateScreenTimeGoal = {},
            onUpdateTheme = {},
            onUpdateOverlayEnabled = {},
            onUpdatePetName = {},
            onUpdatePetType = {},
            onArchiveHabit = {},
            onRestoreHabit = {},
            onBuyAccessory = {},
            onEquipAccessory = {},
            onPetTheNudgie = {},
            onReplyToPet = {},
            onSignOut = {},
            onLinkWithEmail = { _, _ -> },
            startDestination = Screen.Home.route
        )
    }
}

@Composable
fun DressedUpPet(basePetRes: Int, equippedAccessories: List<AccessoryItem>, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    var isBouncing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isBouncing) 1.15f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium), label = "bounce")
    LaunchedEffect(isBouncing) { if (isBouncing) { delay(150); isBouncing = false } }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer { scaleX *= scale; scaleY *= scale }
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { isBouncing = true; onClick() }
    ) {
        Image(painter = painterResource(id = basePetRes), contentDescription = "Nudgie Character", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)

        equippedAccessories.forEach { accessory ->
            val accessoryModifier = when (accessory.name) {
                "Space Ball" -> Modifier.size(40.dp).align(Alignment.BottomEnd).offset(x = (-10).dp, y = (-10).dp)
                "Bowl" -> Modifier.size(40.dp).align(Alignment.BottomStart).offset(x = 10.dp, y = (-10).dp)
                else -> Modifier.fillMaxSize()
                }
                Image(
                    painter = painterResource(id = accessory.assetResId),
                    contentDescription = accessory.name,
                    modifier = accessoryModifier,
                    contentScale = ContentScale.Fit
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBox(onSend: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Surface(
        color = NavySurface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, NavyOutline),
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { if (text.isNotBlank()) { onSend(text); text = ""; focusManager.clearFocus() } }),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) Text("Type your answer...", color = Color.Gray, fontSize = 16.sp)
                    innerTextField()
                }
            )
            IconButton(
                onClick = { if (text.isNotBlank()) { onSend(text); text = ""; focusManager.clearFocus() } },
                modifier = Modifier.size(32.dp)
            ) { Icon(Icons.Default.CheckCircle, contentDescription = "Send", tint = BrandGold) }
        }
    }
}

@Composable
fun SpeechBubble(
    message: String,
    currentTheme: AppTheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(280.dp), // .nudgieCardShadow removed if missing extension
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        ),
        border = BorderStroke(2.dp, NavyOutline)
    ) {
        Text(
            text = message.uppercase(),
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = VT323
        )
    }
}

// THE TABBED SHOP BOUTIQUE UI
@Composable
fun ShopDialog(
    currency: Int,
    accessories: List<AccessoryItem>,
    onBuy: (AccessoryItem) -> Unit,
    onEquip: (AccessoryItem) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(AccessoryCategory.CLOTHES) }

    // THIS DIALOG WRAPPER FORCES IT TO THE FRONT!
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = NavySurface,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(2.dp, NavyOutline),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nudgie Boutique", fontFamily = VT323, fontSize = 28.sp, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💎 $currency", fontFamily = VT323, fontSize = 22.sp, color = BrandGold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category Tabs Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AccessoryCategory.entries.forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SpaceAccent else NavyBackground)
                                .clickable { selectedCategory = category }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when(category) {
                                    AccessoryCategory.CLOTHES -> "Clothes"
                                    AccessoryCategory.HAT -> "Hats"
                                    AccessoryCategory.GLASSES -> "Glasses"
                                    AccessoryCategory.OUTFIT -> "Outfits"
                                    AccessoryCategory.TOY -> "Toys"
                                    AccessoryCategory.TOYS -> "Toys"
                                    AccessoryCategory.FOOD -> "Food"
                                    AccessoryCategory.STAT_BOOST -> "Boosts"
                                },
                                fontFamily = VT323,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtered Items Grid/List
                val filteredItems = accessories.filter { it.category == selectedCategory }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    filteredItems.forEach { item ->
                        ShopItemRow(item = item, onBuy = onBuy, onEquip = onEquip)
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyBackground)
                ) {
                    Text("Close Shop", fontFamily = VT323, fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ShopItemRow(
    item: AccessoryItem,
    onBuy: (AccessoryItem) -> Unit,
    onEquip: (AccessoryItem) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavyBackground),
        border = BorderStroke(1.dp, NavyOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontFamily = VT323, fontSize = 22.sp, color = Color.White)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontFamily = VT323,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    color = SpaceSecondaryText
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            val isConsumable = item.category == AccessoryCategory.FOOD || item.category == AccessoryCategory.STAT_BOOST

            if (isConsumable) {
                Button(
                    onClick = { onBuy(item) },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold)
                ) {
                    Text("${item.cost} 💎", fontFamily = VT323, fontSize = 16.sp, color = Color.Black)
                }
            } else {
                if (item.isPurchased) {
                    Button(
                        onClick = { onEquip(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = if (item.isEquipped) Color.Gray else SpaceAccent)
                    ) {
                        Text(if (item.isEquipped) "Unequip" else "Equip", fontFamily = VT323, fontSize = 16.sp, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = { onBuy(item) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGold)
                    ) {
                        Text("${item.cost} 💎", fontFamily = VT323, fontSize = 16.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalityTestSwitcher(viewModel: NudgieViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsState()
    val currentPersonality = uiState.petStats.personality
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Testing: Debug Personality Switcher", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = currentPersonality.name,
                onValueChange = {},
                label = { Text("Mascot Personality") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                PersonalityType.values().forEach { personality ->
                    DropdownMenuItem(
                        text = { Text(personality.name) },
                        onClick = {
                            viewModel.updatePersonality(personality)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}