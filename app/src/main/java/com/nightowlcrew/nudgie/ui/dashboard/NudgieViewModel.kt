package com.nightowlcrew.nudgie.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.AccessoryCategory
import com.nightowlcrew.nudgie.data.AccessoryItem
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.AuthRepository
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HABIT_TEMPLATES
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.HabitLogEntity
import com.nightowlcrew.nudgie.data.HabitRepository
import com.nightowlcrew.nudgie.data.HabitRepositoryImpl
import com.nightowlcrew.nudgie.data.ScreenTimeRecord
import com.nightowlcrew.nudgie.utils.PetType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.nightowlcrew.nudgie.data.PersonalityType
import com.nightowlcrew.nudgie.data.GeminiService




enum class AppTheme { DEFAULT, CYBERPUNK, STEAMPUNK, GOTH, RETRO_SPACE }
enum class AppIconTheme { BLUE, FOX, AXOLOTL, DRAGON }

data class PetStats(
    val name: String = "Adnap Hsart",
    val level: Int = 1,
    val xp: Int = 0,
    val happiness: Int = 100,
    val energy: Int = 100,
    val currency: Int = 250,
    val accessories: List<AccessoryItem> = emptyList(),
    val message: String? = null,
    val isAwaitingReply: Boolean = false,
    val currentActivity: String = "IDLE",
    val personality: PersonalityType = PersonalityType.SUPPORTIVE
)

data class DashboardUiState(
    val activities: List<ActivityItem> = emptyList(),
    val categorizedActivities: Map<CozyCategory, List<ActivityItem>> = emptyMap(),
    val currentScreenTimeMillis: Long = 0L,
    val screenTimeGoalMillis: Long = 14400000L,
    val currentTheme: AppTheme = AppTheme.RETRO_SPACE,
    val petStats: PetStats = PetStats(),
    val currentPetType: PetType = PetType.BLUE,
    val totalTasksDone: Int = 0,
    val isLoading: Boolean = true,
    val overlayEnabled: Boolean = false,
    val profileUserName: String = "Alex",
    val profileBio: String = "Cozy Nudger",
    val profileJoinDate: String = "October 2023",
    val profileAvatarRes: Int = com.nightowlcrew.nudgie.R.drawable.nudgie,
    val isNudgieUnlocked: Boolean = false,
    val isAnonymous: Boolean = true,
    val isUserLoggedIn: Boolean = false
)

data class StatsUiState(
    val activeStreak: Int = 0,
    val totalTasksDone: Int = 0,
    val categoryProgress: Map<String, Float> = emptyMap(),
    val petStats: PetStats = PetStats(),
    val isLoading: Boolean = true
)

class NudgieViewModel(
    private val repository: HabitRepository,
    private val authRepository: AuthRepository,
    private val sharedPreferences: SharedPreferences,
    private val geminiService: GeminiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    fun updatePersonality(newPersonality: PersonalityType) {
        sharedPreferences.edit().putString("PET_PERSONALITY", newPersonality.name).apply()
        _personality.value = newPersonality
    }

    private val _petMessage = MutableStateFlow<String?>(null)
    private val _isAwaitingReply = MutableStateFlow(false)
    private var messageJob: kotlinx.coroutines.Job? = null
    private val _currentActivity = MutableStateFlow("IDLE")

    // --- Onboarding State ---
    private val _isOnboardingCompleted =
        MutableStateFlow(sharedPreferences.getBoolean("is_onboarding_complete", false))
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isNudgieUnlocked = MutableStateFlow(sharedPreferences.getBoolean("is_nudgie_unlocked", false))

    // --- Onboarding Finalization ---
    fun completeOnboarding(
        userName: String,
        petType: PetType,
        petName: String,
        firstHabit: String
    ) {
        // Update Profile & Pet Details
        updateProfileUserName(userName)
        updatePetType(petType)
        updatePetName(petName)

        // Add their first habit from the interview
        if (firstHabit.isNotBlank()) {
            addNewHabit(
                title = firstHabit,
                category = CozyCategory.DAILY_RHYTHMS.name, // Defaulting to daily rhythms
                frequency = 1
            )
        }

        // Mark onboarding as complete
        sharedPreferences.edit { 
            putBoolean("is_onboarding_complete", true)
            if (petType == PetType.NUDGIE) {
                putBoolean("is_nudgie_unlocked", true)
            }
        }
        _isOnboardingCompleted.value = true
        _isNudgieUnlocked.value = sharedPreferences.getBoolean("is_nudgie_unlocked", false)
    }

    val statsUiState: StateFlow<StatsUiState> = combine(
        repository.getAllHabits(),
        repository.getAllLogs(),
        _uiState.map { it.petStats }
    ) { habits: List<HabitEntity>, logs: List<HabitLogEntity>, petStats: PetStats ->
        val completedLogs = logs.filter { it.isCompleted }
        val totalTasks = completedLogs.size
        val streak = calculateStreak(completedLogs)

        val catProgress = CozyCategory.entries.associate { category ->
            val habitsInCat = habits.filter { it.category == category.name }
            val habitIds = habitsInCat.map { it.id }.toSet()
            val completedInCat = completedLogs.filter { it.habitId in habitIds }.size
            val targetInCat = habitsInCat.sumOf { it.targetFrequencyPerDay } * 7
            val progress =
                if (targetInCat > 0) (completedInCat.toFloat() / targetInCat.toFloat()).coerceIn(
                    0f,
                    1f
                ) else 0f
            category.name to progress
        }

        StatsUiState(
            activeStreak = streak,
            totalTasksDone = totalTasks,
            categoryProgress = catProgress,
            petStats = petStats,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatsUiState())

    private fun calculateStreak(completedLogs: List<HabitLogEntity>): Int {
        if (completedLogs.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val completedDates =
            completedLogs.asSequence()
                .map { it.date }
                .filter { it.isNotEmpty() }
                .distinct()
                .sortedDescending()
                .toList()
        if (completedDates.isEmpty()) return 0

        var streak = 0
        val calendar = Calendar.getInstance()
        val today = sdf.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(calendar.time)

        val latestDate = completedDates[0]
        if (latestDate != today && latestDate != yesterday) return 0

        val streakCalendar = Calendar.getInstance()
        try {
            streakCalendar.time = sdf.parse(latestDate) ?: return 0
        } catch (_: Exception) {
            return 0
        }

        for (dateStr in completedDates) {
            if (dateStr == sdf.format(streakCalendar.time)) {
                streak++
                streakCalendar.add(Calendar.DAY_OF_YEAR, -1)
            } else break
        }
        return streak
    }

    val archivedHabits: StateFlow<List<HabitEntity>> = repository.getArchivedHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentTheme = MutableStateFlow(
        try {
            AppTheme.valueOf(
                sharedPreferences.getString("app_theme", AppTheme.RETRO_SPACE.name)
                    ?: AppTheme.RETRO_SPACE.name
            )
        } catch (_: Exception) {
            AppTheme.RETRO_SPACE
        }
    )

    private val _overlayEnabled =
        MutableStateFlow(sharedPreferences.getBoolean("overlay_enabled", false))
    private val _petName =
        MutableStateFlow(sharedPreferences.getString("pet_name", "Your Pet") ?: "Your Pet")
    private val _currentPetType = MutableStateFlow(
        try {
            PetType.valueOf(
                sharedPreferences.getString("pet_type", PetType.BLUE.name) ?: PetType.BLUE.name
            )
        } catch (_: Exception) {
            PetType.BLUE
        }
    )

    val appIconTheme: StateFlow<AppIconTheme> = _currentPetType.map { petType ->
        when (petType) {
            PetType.BLUE -> AppIconTheme.BLUE
            PetType.FOX -> AppIconTheme.FOX
            PetType.AXOLOTL -> AppIconTheme.AXOLOTL
            PetType.DRAGON -> AppIconTheme.DRAGON
            PetType.NUDGIE -> AppIconTheme.BLUE
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppIconTheme.BLUE)

    private val _happiness = MutableStateFlow(85)
    private val _personality = MutableStateFlow(
        try {
            PersonalityType.valueOf(
                sharedPreferences.getString("PET_PERSONALITY", PersonalityType.SUPPORTIVE.name) ?: PersonalityType.SUPPORTIVE.name
            )
        } catch (_: Exception) {
            PersonalityType.SUPPORTIVE
        }
    )
    private val _energy = MutableStateFlow(62)
    private val _petLevel = MutableStateFlow(1)
    private val _petXP = MutableStateFlow(0)
    private val _currency = MutableStateFlow(sharedPreferences.getInt("pet_currency", 250))

    private val _profileUserName =
        MutableStateFlow(sharedPreferences.getString("profile_user_name", "Alex") ?: "Alex")
    private val _profileBio =
        MutableStateFlow(sharedPreferences.getString("profile_bio", "Cozy Nudger") ?: "Cozy Nudger")
    private val _profileJoinDate = MutableStateFlow(
        sharedPreferences.getString("profile_join_date", "October 2023") ?: "October 2023"
    )
    private val _profileAvatarRes = MutableStateFlow(
        sharedPreferences.getInt(
            "profile_avatar_res",
            com.nightowlcrew.nudgie.R.drawable.nudgie
        )
    )

    private val _accessories = MutableStateFlow(
        listOf(
            AccessoryItem(
                "pink_hat",
                "Pink Hat",
                60,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                AccessoryCategory.HAT,
                "A stylish neon pink cap."
            ),
            AccessoryItem(
                "cool_shades",
                "Cool Shades",
                100,
                com.nightowlcrew.nudgie.R.drawable.feed,
                com.nightowlcrew.nudgie.R.drawable.feed,
                AccessoryCategory.GLASSES,
                "Dark UV protection for cool pets."
            ),
            AccessoryItem(
                "space_suit",
                "Space Suit",
                250,
                com.nightowlcrew.nudgie.R.drawable.play,
                com.nightowlcrew.nudgie.R.drawable.play,
                AccessoryCategory.OUTFIT,
                "Ready for zero gravity orbits."
            ),
            AccessoryItem(
                "squeaky_bone",
                "Squeaky Bone",
                75,
                com.nightowlcrew.nudgie.R.drawable.play,
                com.nightowlcrew.nudgie.R.drawable.play,
                AccessoryCategory.TOY,
                "Makes a loud squeak when chewed."
            ),
            AccessoryItem(
                "yarn_ball",
                "Yarn Ball",
                40,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                AccessoryCategory.TOY,
                "Perfect for rolling around the floor."
            ),
            AccessoryItem(
                "mega_burger",
                "Mega Burger",
                25,
                com.nightowlcrew.nudgie.R.drawable.feed,
                com.nightowlcrew.nudgie.R.drawable.feed,
                AccessoryCategory.FOOD,
                "Juicy pixel burger with extra cheese."
            ),
            AccessoryItem(
                "crunchy_taco",
                "Crunchy Taco",
                15,
                com.nightowlcrew.nudgie.R.drawable.feed,
                com.nightowlcrew.nudgie.R.drawable.feed,
                AccessoryCategory.FOOD,
                "Packed with spice and fresh ingredients."
            ),
            AccessoryItem(
                "energy_booster",
                "Energy Booster",
                120,
                com.nightowlcrew.nudgie.R.drawable.play,
                com.nightowlcrew.nudgie.R.drawable.play,
                AccessoryCategory.STAT_BOOST,
                "Instantly restores +50 Energy.",
                statEffect = "ENERGY+50"
            ),
            AccessoryItem(
                "happiness_elixir",
                "Happiness Elixir",
                150,
                com.nightowlcrew.nudgie.R.drawable.play,
                com.nightowlcrew.nudgie.R.drawable.play,
                AccessoryCategory.STAT_BOOST,
                "Instantly restores +40 Happiness.",
                statEffect = "HAPPINESS+40"
            )
        )
    )

    init {
        viewModelScope.launch {
            authRepository.signInAnonymously()
            prepopulateDefaultHabits()

            //  INITIAL GREETING
            try {

                val currentStats = _uiState.value.petStats

                val aiGreeting = generateContextualMessage(currentStats, emptyList())

                showMessage(aiGreeting, durationMillis = 6000L, isInteractive = false)
            } catch (e: Exception) {
                // Fallback just in case the AI fails or there is no internet
                showMessage("Hello!", durationMillis = 6000L, isInteractive = false)
            }

            //  IDLE LOOP
            launch {
                delay(15000L)
                while (true) {
                    delay(30000L) // Wait 30 seconds


                    if (_petMessage.value == null) {
                        try {
                            val currentStats = _uiState.value.petStats

                            val aiMessage = generateContextualMessage(currentStats, emptyList())

                            showMessage(aiMessage, durationMillis = 6000L, isInteractive = false)
                        } catch (e: Exception) {
                            // If the AI fails in the background, we just stay quiet
                        }
                    }
                }
            }

            repository.getAllLogs().onEach { logs ->
                val completedCount = logs.count { it.isCompleted }
                val totalXp = completedCount * 15
                _petLevel.update { (totalXp / 800) + 1 }
                _petXP.update { totalXp % 800 }
            }.launchIn(viewModelScope)

            // 1. Group the first three to keep the compiler happy
            val baseStatsFlow = combine(_petName, _happiness, _energy) { n, h, e ->
                Triple(n, h, e)
            }

            // 2. Add _personality into the updated petStatsFlow
            val petStatsFlow = combine(
                baseStatsFlow,
                _petLevel,
                _petXP,
                _personality
            ) { (name, h, e), l, xp, personality ->
                PetStats(
                    name = name,
                    level = l,
                    xp = xp,
                    happiness = h,
                    energy = e,
                    personality = personality // <-- This saves it!
                )
            }

            val extendedPetStatsFlow = combine(
                petStatsFlow,
                _currency,
                _accessories,
                _petMessage,
                _isAwaitingReply
            ) { stats, currency, accessories, msg, isAwaiting ->
                stats.copy(
                    currency = currency,
                    accessories = accessories,
                    message = msg,
                    isAwaitingReply = isAwaiting
                )
            }

            val profileStateFlow = combine(
                _profileUserName,
                _profileBio,
                _profileJoinDate,
                _profileAvatarRes
            ) { name, bio, date, avatar ->
                Triple(name, bio, Pair(date, avatar))
            }

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date())
                ),
                extendedPetStatsFlow,
                profileStateFlow,
                repository.getAllLogs(),
                _currentTheme,
                _currentPetType,
                _overlayEnabled,
                _isNudgieUnlocked,
                authRepository.currentUser
            ) { flows: Array<Any?> ->
                @Suppress("UNCHECKED_CAST") val activities = flows[0] as List<ActivityItem>
                val screenTime = flows[1] as ScreenTimeRecord?
                val petStats = flows[2] as PetStats
                @Suppress("UNCHECKED_CAST") val profile =
                    flows[3] as Triple<String, String, Pair<String, Int>>
                @Suppress("UNCHECKED_CAST") val allLogs = flows[4] as List<HabitLogEntity>
                val theme = flows[5] as AppTheme
                val petType = flows[6] as PetType
                val overlayEnabled = flows[7] as Boolean
                val isNudgieUnlocked = flows[8] as Boolean
                val firebaseUser = flows[9] as com.google.firebase.auth.FirebaseUser?

                val categorized = CozyCategory.entries.associateWith { category ->
                    activities.filter { it.category == category.name }
                }.filterValues { it.isNotEmpty() }

                val tasksDone = allLogs.count { it.isCompleted }

                DashboardUiState(
                    activities = activities,
                    categorizedActivities = categorized,
                    currentScreenTimeMillis = screenTime?.actualDurationMillis ?: 0L,
                    screenTimeGoalMillis = screenTime?.targetLimitMillis ?: 14400000L,
                    currentTheme = theme,
                    petStats = petStats.copy(currentActivity = _currentActivity.value),
                    currentPetType = petType,
                    totalTasksDone = tasksDone,
                    isLoading = false,
                    overlayEnabled = overlayEnabled,
                    profileUserName = profile.first,
                    profileBio = profile.second,
                    profileJoinDate = profile.third.first,
                    profileAvatarRes = profile.third.second,
                    isNudgieUnlocked = isNudgieUnlocked,
                    isAnonymous = firebaseUser?.isAnonymous ?: true,
                    isUserLoggedIn = firebaseUser != null
                )
            }.onEach { updatedState ->
                _uiState.update { updatedState }
            }.launchIn(viewModelScope)
        }
    }

    private fun showMessage(
        message: String,
        durationMillis: Long = 4000L,
        isInteractive: Boolean = false
    ) {
        messageJob?.cancel()
        _petMessage.value = message
        _isAwaitingReply.value = isInteractive
        if (!isInteractive) {
            messageJob = viewModelScope.launch {
                delay(durationMillis)
                _petMessage.value = null
            }
        }
    }

    fun replyToPet(reply: String) {
        _isAwaitingReply.value = false
        showMessage("Oh, got it! You said: $reply", 4000L, isInteractive = false)
    }

    fun buyAccessory(accessory: AccessoryItem) {
        val currentCurrency = _currency.value
        if (currentCurrency < accessory.cost) {
            showMessage("Not enough gems! 💎", 2000L, isInteractive = false)
            return
        }

        if ((accessory.category == AccessoryCategory.OUTFIT || accessory.category == AccessoryCategory.TOY) && accessory.isPurchased) {
            return
        }

        _currency.update { currency ->
            if (currency >= accessory.cost && !accessory.isPurchased) {
                val newCurrency = currency - accessory.cost
                sharedPreferences.edit { putInt("pet_currency", newCurrency) }

                if (accessory.category == AccessoryCategory.STAT_BOOST || accessory.category == AccessoryCategory.FOOD) {
                    applyStatEffect(accessory.statEffect)
                    showMessage("Consumed ${accessory.name}! ✨", 3000L, isInteractive = false)
                } else {
                    _accessories.update { currentAccessories ->
                        currentAccessories.map {
                            if (it.id == accessory.id) it.copy(isPurchased = true) else it
                        }
                    }
                    showMessage("Purchased ${accessory.name}! 🎉", 3000L, isInteractive = false)
                }
                newCurrency
            } else {
                currency
            }
        }
    }

    private fun applyStatEffect(effect: String) {
        if (effect.isEmpty()) return
        val parts = effect.split("+")
        if (parts.size != 2) return
        val value = parts[1].toIntOrNull() ?: 0

        when (parts[0]) {
            "ENERGY" -> _energy.update { (it + value).coerceAtMost(100) }
            "HAPPINESS" -> _happiness.update { (it + value).coerceAtMost(100) }
        }
    }

    fun equipAccessory(accessory: AccessoryItem) {
        if (accessory.isPurchased) {
            _accessories.update { currentAccessories ->
                currentAccessories.map { item ->
                    if (item.category == accessory.category) {
                        if (item.id == accessory.id) item.copy(isEquipped = !item.isEquipped) else item.copy(
                            isEquipped = false
                        )
                    } else item
                }
            }
        }
    }

    fun updateTheme(theme: AppTheme) {
        _currentTheme.update { theme }
        sharedPreferences.edit { putString("app_theme", theme.name) }
    }

    fun updateOverlayEnabled(enabled: Boolean) {
        _overlayEnabled.update { enabled }
        sharedPreferences.edit { putBoolean("overlay_enabled", enabled) }
    }

    fun updatePetName(newName: String) {
        _petName.update { newName }
        sharedPreferences.edit { putString("pet_name", newName) }
    }

    fun updatePetType(newType: PetType) {
        _currentPetType.update { newType }
        sharedPreferences.edit { putString("pet_type", newType.name) }
    }

    fun updateProfileUserName(newName: String) {
        _profileUserName.update { newName }
        sharedPreferences.edit { putString("profile_user_name", newName) }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun linkWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, password)
                authRepository.linkAccount(credential)
                showMessage("Account linked successfully! 🎉", 3000L)
            } catch (e: Exception) {
                showMessage("Failed to link account: ${e.localizedMessage}", 4000L)
            }
        }
    }

    private fun prepopulateDefaultHabits() {
        val alreadyAdded = sharedPreferences.getBoolean("default_habits_v12_added", false)
        if (!alreadyAdded) {
            viewModelScope.launch {
                val existingHabits = repository.getAllHabits().first()
                val existingTitles = existingHabits.map { it.title }.toSet()
                val emojiRegex = Regex("^(\\p{So}|\\p{Sk})\\s+(.*)$")

                HABIT_TEMPLATES.forEach { (category, templates) ->
                    templates.forEach { template ->
                        val matchResult = emojiRegex.find(template.title)
                        val (icon, finalTitle) = if (matchResult != null) matchResult.groupValues[1] to matchResult.groupValues[2] else "📌" to template.title

                        if (finalTitle !in existingTitles) {
                            repository.insertHabit(
                                HabitEntity(
                                    title = finalTitle,
                                    icon = icon,
                                    category = category.name,
                                    targetFrequencyPerDay = template.defaultFrequency,
                                    isStock = true
                                )
                            )
                        }
                    }
                }
                sharedPreferences.edit { putBoolean("default_habits_v12_added", true) }
            }
        }
    }

    fun toggleHabitCompletion(activityItem: ActivityItem) {
        viewModelScope.launch {
            val now = Date()
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
            val isCompleting = !activityItem.isCompleted
            repository.insertLog(
                HabitLogEntity(
                    habitId = activityItem.id,
                    completedAtTime = currentTime,
                    date = todayDate,
                    isCompleted = isCompleting
                )
            )
            if (isCompleting) completeHabit() else missedHabit()
        }
    }

    fun addNewHabit(
        title: String,
        category: String,
        frequency: Int,
        isStock: Boolean = false,
        markAsCompleted: Boolean = false
    ) {
        viewModelScope.launch {
            val now = Date()
            val matchResult = Regex("^(\\p{So}|\\p{Sk})\\s+(.*)$").find(title)
            val (icon, finalTitle) = if (matchResult != null) matchResult.groupValues[1] to matchResult.groupValues[2] else "📌" to title

            val habit = HabitEntity(
                title = finalTitle,
                icon = icon,
                category = category,
                targetFrequencyPerDay = frequency,
                isStock = isStock
            )
            repository.insertHabit(habit)

            if (markAsCompleted) {
                repository.insertLog(
                    HabitLogEntity(
                        habitId = habit.id,
                        completedAtTime = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(now),
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now),
                        isCompleted = true
                    )
                )
                completeHabit()
            }
        }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch {
            repository.deleteHabit(
                HabitEntity(
                    id = id,
                    title = "",
                    icon = "",
                    category = "",
                    targetFrequencyPerDay = 0
                )
            )
        }
    }

    fun archiveHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.archiveHabit(habit.id)
        }
    }

    fun restoreHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.restoreHabit(habit.id)
        }
    }

    fun updateScreenTimeGoal(newGoalHours: Int) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val limitMillis = newGoalHours.toLong() * 3600000L
            repository.insertOrUpdateScreenTime(
                ScreenTimeRecord(
                    date = today,
                    targetLimitMillis = limitMillis,
                    actualDurationMillis = _uiState.value.currentScreenTimeMillis
                )
            )
        }
    }

    fun missedHabit() {
        val penalty = 15
        val nonPunishmentFloor = 30
        _happiness.update { (it - penalty).coerceAtLeast(nonPunishmentFloor) }
    }

    private fun completeHabit() {
        _happiness.update { (it + 20).coerceAtMost(100) }

        _currency.update { currentCurrency ->
            val newCurrency = currentCurrency + 5
            sharedPreferences.edit { putInt("pet_currency", newCurrency) }
            newCurrency
        }

        viewModelScope.launch {
            try {
                // Let the AI know we just crushed a habit!
                val currentStats = _uiState.value.petStats.copy(currentActivity = "JUST_COMPLETED_HABIT")
                val aiPraise = generateContextualMessage(currentStats, emptyList())
                showMessage(aiPraise, 4000L)
            } catch (e: Exception) {
                showMessage("Great job! 🌟", 4000L)
            }

            // Level up logic
            val currentXp = _petXP.value
            if (currentXp + 15 >= 800) {
                _petLevel.value += 1
                _petXP.value -= 800

                try {
                    val levelStats = _uiState.value.petStats.copy(currentActivity = "JUST_LEVELED_UP")
                    val aiLevelUp = generateContextualMessage(levelStats, emptyList())
                    showMessage(aiLevelUp, 6000L)
                } catch (e: Exception) {
                    showMessage("Level UP! 🌟 Look at my stats now!", 6000L)
                }
            }
        }
    }

    fun petTheNudgie() {
        if (_happiness.value < 100) {
            _happiness.update { it + 1 }
        }

        viewModelScope.launch {
            try {
                // Temporarily flag the activity so the AI knows it is actively being pet
                val currentStats = _uiState.value.petStats.copy(currentActivity = "GETTING_PET")
                val aiMessage = generateContextualMessage(currentStats, emptyList())
                showMessage(aiMessage, 3000L)
            } catch (e: Exception) {
                // Fallback just in case
                showMessage("Purrrrr...", 2000L)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication
                return NudgieViewModel(
                    repository = HabitRepositoryImpl(application.database.habitDao(), application.database.screenTimeDao()),
                    authRepository = AuthRepository(),
                    sharedPreferences = application.getSharedPreferences("nudgie_prefs", Context.MODE_PRIVATE),
                    geminiService = GeminiService()
                ) as T
            }
        }
    }

    suspend fun generateContextualMessage(
        stats: PetStats,
        recentHabits: List<ActivityItem>
    ): String {

        val prompt = """
        You are Nudgie, a cute goo-monster mascot.
        Adopt this personality: ${stats.personality.traits}
        Current activity: ${stats.currentActivity}
        Recent habit:${'$'}{recentHabits.lastOrNull()?.habitName ?: "none"}
        
        Give a short, friendly response (under 15 words).
    """.trimIndent()

        return geminiService.generateResponse(prompt)
    }

    // --- PET DIALOGUE BANK ---
    object PetDialogBank {
        val morningGreetings = listOf(
            "Good morning! ☀️ Ready to conquer the day?",
            "Morning! Did you sleep well? 🛌",
            "Rise and shine! Let's get some tasks done! ☕",
            "Early bird gets the XP! 🦅",
            "A fresh start! What's first on the list? 🌱"
        )
        val afternoonGreetings = listOf(
            "Good afternoon! 🌤️ Halfway through the day!",
            "Afternoon check-in! Don't forget to stretch. 🧘",
            "Lunch break? Or task-crushing time? 🥪",
            "Hope your day is going great so far! ✨"
        )
        val eveningGreetings = listOf(
            "Good evening! 🌙 Time to wind down soon.",
            "Evening! Let's review what we finished today. 📝",
            "Great job surviving the day! Ready to relax? 🛋️",
            "Sunset vibes. Let's finish strong! 🌆"
        )
        val lateNightGreetings = listOf(
            "You're up late! 🦉 Rest is a habit too, you know.",
            "Night owl mode activated! 🌙",
            "Past midnight... go to sleep soon! 💤",
            "Still grinding? Don't forget to rest your eyes. 👀"
        )
        val sadThoughts = listOf(
            "I'm feeling a bit lonely... let's complete a task! 💔",
            "Could we do something fun? I'm sad. 🌧️",
            "A completed habit would really cheer me up! 🥺"
        )
        val tiredThoughts = listOf(
            "Yawn... running low on energy. Time for a break? 🔋",
            "My battery is running on fumes... 🪫",
            "So... sleepy... 🥱"
        )
        val questions = listOf(
            "How is your day going so far? 🌟",
            "What is our main goal for today? 🎯",
            "Are you remembering to drink water? 💧",
            "What's the best thing that happened today? ✨",
            "Are you feeling productive right now? 📈"
        )
        val idleThoughts = listOf(
            "Just hanging out... 🦠",
            "You're doing great! ✨",
            "I wonder what level 20 looks like... 🤔",
            "Nudgie loves you! ❤️",
            "A clean to-do list is a happy to-do list! 📋",
            "My pixels are tingling! ⚡"
        )
    }
}