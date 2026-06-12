package com.nightowlcrew.nudgie.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.AccessoryCategory
import com.nightowlcrew.nudgie.data.AccessoryItem
import com.nightowlcrew.nudgie.data.ActivityItem
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
    val message: String? = null // Powers the Speech Bubble!
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
    /** Whether the "Draw over other apps" overlay is user-enabled in Settings */
    val overlayEnabled: Boolean = false,
    // Additive Profile Properties
    val profileUserName: String = "Alex",
    val profileBio: String = "Cozy Nudger",
    val profileJoinDate: String = "October 2023",
    val profileAvatarRes: Int = com.nightowlcrew.nudgie.R.drawable.nudgie
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
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // --- Dialog/Speech Bubble State ---
    private val _petMessage = MutableStateFlow<String?>(null)
    private var messageJob: kotlinx.coroutines.Job? = null

    // Analytics: Asynchronously calculated stats exposed via stateIn
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
            val targetInCat = habitsInCat.sumOf { it.targetFrequencyPerDay } * 7 // Weekly context

            val progress = if (targetInCat > 0) {
                (completedInCat.toFloat() / targetInCat.toFloat()).coerceIn(0f, 1f)
            } else 0f
            category.name to progress
        }

        StatsUiState(
            activeStreak = streak,
            totalTasksDone = totalTasks,
            categoryProgress = catProgress,
            petStats = petStats,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsUiState()
    )

    private fun calculateStreak(completedLogs: List<HabitLogEntity>): Int {
        if (completedLogs.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dateSet = mutableSetOf<String>()
        for (log in completedLogs) {
            if (log.date.isNotEmpty()) {
                dateSet.add(log.date)
            }
        }

        val completedDates = dateSet.toList().sortedDescending()
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
            val date = sdf.parse(latestDate)
            if (date != null) {
                streakCalendar.time = date
            } else {
                return 0
            }
        } catch (e: Exception) {
            return 0
        }

        for (dateStr in completedDates) {
            if (dateStr == sdf.format(streakCalendar.time)) {
                streak++
                streakCalendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    val archivedHabits: StateFlow<List<HabitEntity>> = repository.getArchivedHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isOverScreenTimeLimit: Boolean
        get() = uiState.value.currentScreenTimeMillis > uiState.value.screenTimeGoalMillis

    private val _currentTheme = MutableStateFlow(
        try { AppTheme.valueOf(sharedPreferences.getString("app_theme", AppTheme.RETRO_SPACE.name) ?: AppTheme.RETRO_SPACE.name) }
        catch (e: Exception) { AppTheme.RETRO_SPACE }
    )

    private val _overlayEnabled = MutableStateFlow(sharedPreferences.getBoolean("overlay_enabled", false))

    private val _petName = MutableStateFlow(sharedPreferences.getString("pet_name", "Your Pet") ?: "Your Pet")
    private val _currentPetType = MutableStateFlow(
        try { PetType.valueOf(sharedPreferences.getString("pet_type", PetType.BLUE.name) ?: PetType.BLUE.name) }
        catch (e: Exception) { PetType.BLUE }
    )

    val appIconTheme: StateFlow<AppIconTheme> = _currentPetType
        .map { petType ->
            when (petType) {
                PetType.BLUE -> AppIconTheme.BLUE
                PetType.FOX -> AppIconTheme.FOX
                PetType.AXOLOTL -> AppIconTheme.AXOLOTL
                PetType.DRAGON -> AppIconTheme.DRAGON
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = when (_currentPetType.value) {
                PetType.BLUE -> AppIconTheme.BLUE
                PetType.FOX -> AppIconTheme.FOX
                PetType.AXOLOTL -> AppIconTheme.AXOLOTL
                PetType.DRAGON -> AppIconTheme.DRAGON
                else -> AppIconTheme.BLUE
            }
        )

    private val _happiness = MutableStateFlow(85)
    private val _energy = MutableStateFlow(62)
    private val _petLevel = MutableStateFlow(1)
    private val _petXP = MutableStateFlow(0)

    private val _currency = MutableStateFlow(sharedPreferences.getInt("pet_currency", 250))

    // Profile States
    private val _profileUserName = MutableStateFlow(sharedPreferences.getString("profile_user_name", "Alex") ?: "Alex")
    private val _profileBio = MutableStateFlow(sharedPreferences.getString("profile_bio", "Cozy Nudger") ?: "Cozy Nudger")
    private val _profileJoinDate = MutableStateFlow(sharedPreferences.getString("profile_join_date", "October 2023") ?: "October 2023")
    private val _profileAvatarRes = MutableStateFlow(sharedPreferences.getInt("profile_avatar_res", com.nightowlcrew.nudgie.R.drawable.nudgie))

    private val _accessories = MutableStateFlow(
        listOf(
            AccessoryItem("hat_1", "Nudgie Sweater", 50, com.nightowlcrew.nudgie.R.drawable.zustomize, com.nightowlcrew.nudgie.R.drawable.zustomize, AccessoryCategory.HAT),
            AccessoryItem("glasses_1", "Bowl", 100, com.nightowlcrew.nudgie.R.drawable.feed, com.nightowlcrew.nudgie.R.drawable.feed, AccessoryCategory.GLASSES),
            AccessoryItem("outfit_1", "Space Ball", 250, com.nightowlcrew.nudgie.R.drawable.play, com.nightowlcrew.nudgie.R.drawable.play, AccessoryCategory.OUTFIT),
            AccessoryItem("toy_1", "Squeaky Bone", 75, com.nightowlcrew.nudgie.R.drawable.play, com.nightowlcrew.nudgie.R.drawable.play, AccessoryCategory.TOY),
            AccessoryItem("toy_2", "Yarn Ball", 40, com.nightowlcrew.nudgie.R.drawable.zustomize, com.nightowlcrew.nudgie.R.drawable.zustomize, AccessoryCategory.TOY),
            AccessoryItem("food_1", "Mega Burger", 20, com.nightowlcrew.nudgie.R.drawable.feed, com.nightowlcrew.nudgie.R.drawable.feed, AccessoryCategory.FOOD),
            AccessoryItem("food_2", "Health Potion", 35, com.nightowlcrew.nudgie.R.drawable.play, com.nightowlcrew.nudgie.R.drawable.play, AccessoryCategory.FOOD)
        )
    )

    init {
        viewModelScope.launch {
            prepopulateDefaultHabits()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // 1. Time of Day Greeting on Launch
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = when (hour) {
                in 5..11 -> listOf(
                    "Good morning! ☀️ Ready to conquer the day?",
                    "Morning! Did you sleep well? 🛌",
                    "Rise and shine! Let's get some tasks done! ☕"
                ).random()
                in 12..17 -> listOf(
                    "Good afternoon! 🌤️ Halfway through the day!",
                    "Afternoon check-in! Don't forget to stretch. 🧘",
                    "Hope your day is going great so far! ✨"
                ).random()
                in 18..21 -> listOf(
                    "Good evening! 🌙 Time to wind down soon.",
                    "Great job surviving the day! Ready to relax? 🛋️",
                    "Sunset vibes. Let's finish strong! 🌆"
                ).random()
                else -> listOf(
                    "You're up late! 🦉 Rest is a habit too, you know.",
                    "Night owl mode activated! 🌙",
                    "Past midnight... go to sleep soon! 💤"
                ).random()
            }
            showMessage(greeting, 6000L)

            // 2. The Automatic Idle/Situational Timer
            launch {
                delay(15000L) // Wait 15 seconds after app launch before starting the idle loop
                while (true) {
                    delay(30000L) // Trigger every 30 seconds

                    // Only speak if there isn't already a message on the screen
                    if (_petMessage.value == null) {
                        val happiness = _happiness.value
                        val energy = _energy.value

                        val message = when {
                            happiness < 50 -> "I'm feeling a bit sad... let's complete a task! 💔"
                            energy < 40 -> "Yawn... running low on energy. Time for a break? 🔋"
                            else -> listOf(
                                "Just hanging out... 🦠",
                                "Did you drink water today? 💧",
                                "You're doing great! ✨",
                                "I wonder what level 20 looks like... 🤔",
                                "Nudgie loves you! ❤️",
                                "A clean to-do list is a happy to-do list! 📋"
                            ).random()
                        }
                        showMessage(message, 5000L)
                    }
                }
            }

            // Sync XP and Level with the database record
            repository.getAllLogs().onEach { logs ->
                val completedCount = logs.count { it.isCompleted }
                val totalXp = completedCount * 15
                _petLevel.update { (totalXp / 800) + 1 }
                _petXP.update { totalXp % 800 }
            }.launchIn(viewModelScope)

            val petStatsFlow = combine(_petName, _happiness, _energy, _petLevel, _petXP) { name, h, e, l, xp ->
                PetStats(name, l, xp, h, e)
            }

            val extendedPetStatsFlow = combine(petStatsFlow, _currency, _accessories, _petMessage) { stats, currency, accessories, msg ->
                stats.copy(currency = currency, accessories = accessories, message = msg)
            }

            val profileStateFlow = combine(_profileUserName, _profileBio, _profileJoinDate, _profileAvatarRes) { name, bio, date, avatar ->
                Triple(name, bio, Pair(date, avatar))
            }

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(today),
                extendedPetStatsFlow,
                profileStateFlow,
                repository.getAllLogs(),
                _currentTheme,
                _currentPetType,
                _overlayEnabled
            ) { flows: Array<Any?> ->
                val activities = flows[0] as List<ActivityItem>
                val screenTime = flows[1] as ScreenTimeRecord?
                val petStats = flows[2] as PetStats
                val profile = flows[3] as Triple<String, String, Pair<String, Int>>
                val allLogs = flows[4] as List<HabitLogEntity>
                val theme = flows[5] as AppTheme
                val petType = flows[6] as PetType
                val overlayEnabled = flows[7] as Boolean

                val categorized = CozyCategory.entries.associateWith { category ->
                    activities.filter { it.category == category.name }
                }.filterValues { it.isNotEmpty() }

                val tasksDone = allLogs.count { it.isCompleted }

                DashboardUiState(
                    activities = activities,
                    categorizedActivities = categorized,
                    currentScreenTimeMillis = screenTime?.actualDurationMillis ?: 0L,
                    screenTimeGoalMillis = screenTime?.targetLimitMillis ?: 14400000L,
                    petStats = petStats,
                    currentTheme = theme,
                    currentPetType = petType,
                    totalTasksDone = tasksDone,
                    isLoading = false,
                    overlayEnabled = overlayEnabled,
                    profileUserName = profile.first,
                    profileBio = profile.second,
                    profileJoinDate = profile.third.first,
                    profileAvatarRes = profile.third.second
                )
            }.onEach { updatedState ->
                _uiState.update { updatedState }
            }.launchIn(viewModelScope)
        }
    }

    // Helper function to show messages and automatically clear them
    private fun showMessage(message: String, durationMillis: Long = 4000L) {
        messageJob?.cancel() // Cancel any existing timer so they don't overlap
        _petMessage.value = message
        messageJob = viewModelScope.launch {
            delay(durationMillis)
            _petMessage.value = null
        }
    }

    fun buyAccessory(accessory: AccessoryItem) {
        _currency.update { currentCurrency ->
            if (currentCurrency >= accessory.cost && !accessory.isPurchased) {
                val newCurrency = currentCurrency - accessory.cost
                sharedPreferences.edit().putInt("pet_currency", newCurrency).apply()
                
                _accessories.update { currentAccessories ->
                    currentAccessories.map {
                        if (it.id == accessory.id) it.copy(isPurchased = true) else it
                    }
                }
                newCurrency
            } else {
                currentCurrency
            }
        }
    }

    fun equipAccessory(accessory: AccessoryItem) {
        if (accessory.isPurchased) {
            _accessories.update { currentAccessories ->
                currentAccessories.map {
                    if (it.category == accessory.category) {
                        if (it.id == accessory.id) {
                            it.copy(isEquipped = !it.isEquipped)
                        } else {
                            it.copy(isEquipped = false)
                        }
                    } else {
                        it
                    }
                }
            }
        }
    }

    fun updateTheme(theme: AppTheme) {
        _currentTheme.update { theme }
        sharedPreferences.edit().putString("app_theme", theme.name).apply()
    }

    /**
     * Updates the user preference for the overlay feature and persists it.
     */
    fun updateOverlayEnabled(enabled: Boolean) {
        _overlayEnabled.update { enabled }
        sharedPreferences.edit().putBoolean("overlay_enabled", enabled).apply()
    }

    fun updatePetName(newName: String) {
        _petName.update { newName }
        sharedPreferences.edit().putString("pet_name", newName).apply()
    }

    fun updatePetType(newType: PetType) {
        _currentPetType.update { newType }
        sharedPreferences.edit().putString("pet_type", newType.name).apply()
    }

    fun updateProfileUserName(newName: String) {
        _profileUserName.update { newName }
        sharedPreferences.edit().putString("profile_user_name", newName).apply()
    }

    fun updateProfileBio(newBio: String) {
        _profileBio.update { newBio }
        sharedPreferences.edit().putString("profile_bio", newBio).apply()
    }

    fun updateProfileAvatar(avatarResId: Int) {
        _profileAvatarRes.update { avatarResId }
        sharedPreferences.edit().putInt("profile_avatar_res", avatarResId).apply()
    }

    private fun prepopulateDefaultHabits() {
        val alreadyAdded = sharedPreferences.getBoolean("default_habits_v8_added", false)
        if (!alreadyAdded) {
            viewModelScope.launch {
                val existingHabits = repository.getAllHabits().first()
                val existingTitles = existingHabits.map { it.title }.toSet()

                HABIT_TEMPLATES.forEach { (category, templates) ->
                    templates.forEach { template ->
                        if (template.title !in existingTitles) {
                            repository.insertHabit(
                                HabitEntity(title = template.title, icon = "📌", category = category.name, targetFrequencyPerDay = template.defaultFrequency, isStock = true)
                            )
                        }
                    }
                }
                sharedPreferences.edit().putBoolean("default_habits_v8_added", true).apply()
            }
        }
    }

    fun toggleHabitCompletion(activityItem: ActivityItem) {
        viewModelScope.launch {
            val now = Date()
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
            val isCompleting = !activityItem.isCompleted
            val log = HabitLogEntity(habitId = activityItem.id, completedAtTime = currentTime, date = todayDate, isCompleted = isCompleting)
            repository.insertLog(log)

            if (isCompleting) completeHabit() else missedHabit()
        }
    }

    fun addNewHabit(title: String, category: String, frequency: Int, isStock: Boolean = false, markAsCompleted: Boolean = false) {
        viewModelScope.launch {
            val now = Date()
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now)
            val emojiRegex = Regex("^(\\p{So}|\\p{Sk})\\s+(.*)$")
            val matchResult = emojiRegex.find(title)
            val (icon, finalTitle) = if (matchResult != null) matchResult.groupValues[1] to matchResult.groupValues[2] else "📌" to title

            val habit = HabitEntity(title = finalTitle, icon = icon, category = category, targetFrequencyPerDay = frequency, isStock = isStock)
            val habitId = repository.insertHabit(habit).toInt()

            if (markAsCompleted) {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
                val log = HabitLogEntity(habitId = habitId, completedAtTime = currentTime, date = todayDate, isCompleted = true)
                repository.insertLog(log)
                completeHabit()
            }
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch { repository.deleteHabit(HabitEntity(id = id, title = "", icon = "", targetFrequencyPerDay = 0)) }
    }

    fun archiveHabit(habit: HabitEntity) {
        viewModelScope.launch { repository.archiveHabit(habit.id) }
    }

    fun restoreHabit(habit: HabitEntity) {
        viewModelScope.launch { repository.restoreHabit(habit.id) }
    }

    fun updateScreenTimeGoal(newGoalHours: Int) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val limitMillis = newGoalHours.toLong() * 3600000L
            val currentRecord = _uiState.value
            val record = ScreenTimeRecord(date = today, targetLimitMillis = limitMillis, actualDurationMillis = currentRecord.currentScreenTimeMillis)
            repository.insertOrUpdateScreenTime(record)
        }
    }

    fun missedHabit() {
        val penalty = 15
        val nonPunishmentFloor = 30
        _happiness.update { (it - penalty).coerceAtLeast(nonPunishmentFloor) }
    }

    fun drainEnergy(amount: Int) {
        _energy.update { (it - amount).coerceAtLeast(0) }
    }

    private fun completeHabit() {
        _happiness.update { (it + 20).coerceAtMost(100) }
        
        _currency.update { currentCurrency ->
            val newCurrency = currentCurrency + 5
            sharedPreferences.edit().putInt("pet_currency", newCurrency).apply()
            newCurrency
        }

        // 3. Dynamic Praise Options
        val praisePhrases = listOf(
            "Great job! 🌟",
            "Way to go! 🚀",
            "Keep it up! 🔥",
            "XP get! 💎",
            "You're crushing it today! ⚡",
            "Task complete! Proud of you. 😎",
            "Boom! Another one bites the dust. 💥"
        )
        showMessage(praisePhrases.random())

        // XP and Level are handled by the repository collector in init{}
        // But we check for Level UP message here
        viewModelScope.launch {
            val currentXp = _petXP.value
            if (currentXp + 15 >= 800) {
                val levelUpPhrases = listOf(
                    "I Leveled Up!! 🎊 We are getting stronger!",
                    "Level UP! 🌟 Look at my stats now!",
                    "Powering up! 🔋 Thanks for the hard work!"
                )
                showMessage(levelUpPhrases.random(), 6000L)
            }
        }
    }

    fun petTheNudgie() {
        if (_happiness.value < 100) {
            _happiness.update { it + 1 }
        }

        // 4. Fun Tap Reactions
        val petPhrases = listOf(
            "Purrrrr...",
            "Happy!",
            "❤️",
            "Yay!",
            "Hehehe that tickles! 🤭",
            "Aha! Keep the pats coming. 🥰",
            "Mutually assured happiness! ✨",
            "You're my favorite human. 🥺"
        )
        showMessage(petPhrases.random(), 2000L)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication
                val repository = HabitRepositoryImpl(application.database.habitDao(), application.database.screenTimeDao())
                val sharedPrefs = application.getSharedPreferences("nudgie_prefs", Context.MODE_PRIVATE)
                return NudgieViewModel(repository, sharedPrefs) as T
            }
        }
    }
}
