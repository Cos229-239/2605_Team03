package com.nightowlcrew.nudgie.ui.dashboard


import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.window.Dialog
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.nightowlcrew.nudgie.utils.AlarmUtils
import com.nightowlcrew.nudgie.utils.PetAssetManager
import com.nightowlcrew.nudgie.utils.PetType
import kotlinx.coroutines.delay
import java.util.Calendar

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

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Default.FlashOn)
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
    val context = LocalContext.current

    NudgieDashboardContent(
        uiState = uiState,
        statsUiState = statsUiState,
        archivedHabits = archivedHabits,
        onToggleHabit = { viewModel.toggleHabitCompletion(it) },
        onAddHabit = { title, category, frequency, isStock -> viewModel.addNewHabit(title, category, frequency, isStock) },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onUpdateScreenTimeGoal = { hours -> viewModel.updateScreenTimeGoal(hours) },
        onUpdateTheme = { theme -> viewModel.updateTheme(theme) },
        onUpdatePetName = { viewModel.updatePetName(it) },
        onUpdatePetType = { viewModel.updatePetType(it) },
        onArchiveHabit = { viewModel.archiveHabit(it) },
        onRestoreHabit = { viewModel.restoreHabit(it) },
        onBuyAccessory = { viewModel.buyAccessory(it) },
        onEquipAccessory = { viewModel.equipAccessory(it) },
        onPetTheNudgie = { viewModel.petTheNudgie() },
        onReplyToPet = { viewModel.replyToPet(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboardContent(
    uiState: DashboardUiState,
    statsUiState: StatsUiState,
    archivedHabits: List<HabitEntity>,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdatePetName: (String) -> Unit,
    onUpdatePetType: (PetType) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    onBuyAccessory: (AccessoryItem) -> Unit,
    onEquipAccessory: (AccessoryItem) -> Unit,
    onPetTheNudgie: () -> Unit,
    onReplyToPet: (String) -> Unit
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
            currentPetType = uiState.currentPetType
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
            val showBottomBar = currentDestination?.route != Screen.Splash.route
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
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = LavenderText,
                                unselectedTextColor = LavenderText,
                                indicatorColor = Color(0xFF6200EE).copy(alpha = 0.5f)
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
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Splash.route) {
                NudgieSplashScreen(onSplashFinished = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } })
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
                    onToggleHabit = onToggleHabit,
                    onAddHabit = onAddHabit,
                    onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                    onArchiveHabit = onArchiveHabit,
                    onRestoreHabit = onRestoreHabit,
                    initialOpenSlider = openSlider
                )
            }
            composable(Screen.Stats.route) { StatsContent(statsUiState) }
            composable(Screen.Profile.route) { ComingSoonScreen("Profile") }
            composable(Screen.Settings.route) {
                SettingsContent(uiState.activities, archivedHabits, uiState.screenTimeGoalMillis, uiState.currentTheme, onAddHabit, onDeleteHabit, onUpdateScreenTimeGoal, onUpdateTheme, onArchiveHabit, onRestoreHabit)
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
            Text(text = label, style = TextStyle(fontFamily = VT323, fontSize = 14.sp, color = Color.White))
        }
    }
}

@Composable
fun PetActionButtons(onCustomizeClick: () -> Unit, onShopClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        PetActionButton("Customize", R.drawable.zustomize, onClick = onCustomizeClick)
        PetActionButton("Shop", android.R.drawable.ic_menu_manage, onClick = onShopClick)
        PetActionButton("Food", R.drawable.feed)
        PetActionButton("Play", R.drawable.play)
    }
}

@Composable
fun NudgiePetScreen(
    petStats: PetStats,
    currentPetType: PetType,
    onUpdatePetName: (String) -> Unit,
    onCustomizeClick: () -> Unit,
    onShopClick: () -> Unit,
    onPetTheNudgie: () -> Unit,
    onReplyToPet: (String) -> Unit
) {
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

                // Separate clothes from toys so toys aren't glued to the pet!
                val equippedClothes = petStats.accessories.filter { it.isEquipped && it.category == AccessoryCategory.CLOTHES }
                val activeToy = petStats.accessories.firstOrNull { it.isEquipped && it.category == AccessoryCategory.TOYS }

                InteractivePetPlaypen(
                    basePetRes = currentPetImage,
                    equippedClothes = equippedClothes,
                    activeToy = activeToy,
                    petMessage = petStats.message,
                    onPetTheNudgie = onPetTheNudgie
                )
            }

            if (petStats.isAwaitingReply) {
                Spacer(modifier = Modifier.height(16.dp))
                ChatInputBox(onSend = { replyText -> onReplyToPet(replyText) })
            }

            Spacer(modifier = Modifier.weight(0.5f))
            PetActionButtons(onCustomizeClick = onCustomizeClick, onShopClick = onShopClick)
        }
    }
}

@Composable
fun InteractivePetPlaypen(
    basePetRes: Int,
    equippedClothes: List<AccessoryItem>,
    activeToy: AccessoryItem?,
    petMessage: String?,
    onPetTheNudgie: () -> Unit
) {
    // 1. Track the Toy's position (Starts offset to the right)
    var toyOffsetX by remember { mutableStateOf(150f) }
    var toyOffsetY by remember { mutableStateOf(0f) }

    // 2. The Pet dynamically chases the Toy's position using a spring animation!
    // If no toy is active, the pet returns to the center (0f, 0f)
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

            // Pet's Speech Bubble follows the pet
            if (petMessage != null) {
                SpeechBubble(
                    message = petMessage,
                    modifier = Modifier.align(Alignment.TopCenter).offset(x = 90.dp, y = (-20).dp).graphicsLayer { scaleX = -1f } // Unflip the text
                        .graphicsLayer { scaleX = -1f }
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
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Your Nudgie", style = TextStyle(fontFamily = VT323, fontSize = 32.sp, color = Color.White))
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
            LinearProgressIndicator(
                progress = { petStats.xp.toFloat() / maxXp.toFloat() },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = SuccessGreen, trackColor = Color.White.copy(alpha = 0.2f), strokeCap = StrokeCap.Round
            )
        }
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
    Column(modifier = Modifier.fillMaxSize().background(NavyBackground).verticalScroll(rememberScrollState())) {
        PetFrame(petStats, currentTheme, currentPetType, currentScreenTimeMillis, screenTimeGoalMillis, onUpdatePetName, onDigitalBalanceDoubleTap, streak, currency, onPetTheNudgie)
        Spacer(modifier = Modifier.height(48.dp))
        TasksSection(categorizedActivities, currentTheme, onToggleHabit)
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

    Box(modifier = Modifier.fillMaxWidth().height(510.dp)) {
        Image(painter = painterResource(id = R.drawable.pethero_dashboard), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxWidth().height(140.dp).align(Alignment.BottomCenter).background(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, NavyBackground))))

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val streakIcon = when { streak >= 10 -> "🔥"; streak >= 5 -> "⭐"; else -> "•" }
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
                Box(modifier = Modifier.width(320.dp).height(180.dp).clip(RoundedCornerShape(50.dp)), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = R.drawable.clock_date_alarm), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                    Column(modifier = Modifier.padding(bottom = 1.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("08:30", fontSize = 72.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Text("AM", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp))
                        }
                        Text("Thursday, May 7", fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }

                Surface(color = SpaceSurface.copy(alpha = 1f), shape = RoundedCornerShape(8.dp), border = BorderStroke(2.dp, NavyOutline), modifier = Modifier.offset(y = 10.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
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
                    LinearProgressIndicator(progress = { petStats.xp / 800f }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = MaterialTheme.colorScheme.primary, trackColor = Color.White.copy(alpha = 0.3f), strokeCap = StrokeCap.Round)
                }

                val goalHours = screenTimeGoalMillis / 3600000f
                val currentHours = currentScreenTimeMillis / 3600000f
                Column(modifier = Modifier.pointerInput(Unit) { detectTapGestures(onDoubleTap = { onDigitalBalanceDoubleTap() }) }) {
                    LinearProgressIndicator(progress = { (currentHours / goalHours.coerceAtLeast(0.1f)).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = if (currentHours > goalHours) Color.Red else statColors.success, trackColor = Color.White.copy(alpha = 0.3f), strokeCap = StrokeCap.Round)
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Digital Balance", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${"%.1f".format(currentHours)}h / ${"%.1f".format(goalHours)}h", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).offset(y = (-40).dp), verticalAlignment = Alignment.Bottom) {
            Spacer(Modifier.weight(1f))
            Box(contentAlignment = Alignment.BottomCenter) {
                val isEvolved = petStats.level >= 10
                val currentPetImage = if (isEvolved) R.drawable.zustomize else PetAssetManager.getPetDrawable(currentPetType)
                DressedUpPet(basePetRes = currentPetImage, equippedAccessories = petStats.accessories.filter { it.isEquipped }, modifier = Modifier.size(if (isEvolved) 275.dp else 225.dp), onClick = onPetTheNudgie)
                if (petStats.message != null) {
                    SpeechBubble(message = petStats.message, modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = (-20).dp))
                }
            }
            Spacer(Modifier.weight(3f))
        }

        Surface(color = NavySurface, shape = RoundedCornerShape(12.dp), border = BorderStroke(2.dp, NavyOutline), modifier = Modifier.fillMaxWidth().padding(horizontal = 55.dp).align(Alignment.BottomCenter).offset(y = 55.dp).height(70.dp)) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 2.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
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
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(5.dp))
            Text(label, color = Color.White, fontSize = 16.sp)
        }
        Spacer(Modifier.height((-8).dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 18.sp, modifier = Modifier.offset(y = (-4).dp))
        Spacer(Modifier.height(1.dp))
        LinearProgressIndicator(progress = { value.replace("%", "").toFloatOrNull()?.div(100f) ?: 1f }, modifier = Modifier.width(32.dp).height(2.dp).clip(RoundedCornerShape(1.dp)), color = color, trackColor = MaterialTheme.colorScheme.background, strokeCap = StrokeCap.Round)
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
                modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(if (isCompleted) successColor else NavyBackground.copy(alpha = 0.5f)).border(1.dp, if (isCompleted) successColor else NavyOutline, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) { if (isCompleted) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) }
            Spacer(Modifier.width(16.dp))
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(NavyBackground.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                Text(text = if (task.icon.length <= 2) task.icon else "📌", fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.description, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                val progress = if (task.targetCount > 0) task.currentCount.toFloat() / task.targetCount else 0f
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(0.8f).height(4.dp).clip(RoundedCornerShape(2.dp)), color = if (isCompleted) successColor else MaterialTheme.colorScheme.primary, trackColor = NavyOutline.copy(alpha = 0.3f), strokeCap = StrokeCap.Round)
            }
            androidx.compose.material3.IconButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    android.app.TimePickerDialog(context, { _, h, m ->
                        val t = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, h); set(Calendar.MINUTE, m); set(Calendar.SECOND, 0) }.timeInMillis
                        AlarmUtils.setExactAlarm(context, t, task.description)
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
                },
                modifier = Modifier.size(32.dp)
            ) { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Set Reminder", tint = BrandGold) }
            Spacer(Modifier.width(8.dp))
            Text("15 XP", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DressedUpPet(basePetRes: Int, equippedAccessories: List<AccessoryItem>, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    var isBouncing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isBouncing) 1.15f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium), label = "bounce")
    LaunchedEffect(isBouncing) { if (isBouncing) { delay(150); isBouncing = false } }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.graphicsLayer { scaleX *= scale; scaleY *= scale }.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { isBouncing = true; onClick() }
    ) {
        Image(painter = painterResource(id = basePetRes), contentDescription = "Nudgie Character", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        equippedAccessories.forEach { accessory ->
            Image(painter = painterResource(id = accessory.assetResId), contentDescription = accessory.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBox(onSend: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Surface(
        color = NavySurface, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, NavyOutline),
        modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            BasicTextField(
                value = text, onValueChange = { text = it }, textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
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
fun SpeechBubble(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color.White, RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp)).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = message, color = NavyBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = VT323)
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
                    AccessoryCategory.values().forEach { category ->
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
                // FIXED: Now uses the same Yellow BrandGold button as clothing!
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