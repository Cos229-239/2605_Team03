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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppTheme { DEFAULT, CYBERPUNK, STEAMPUNK, GOTH, RETRO_SPACE }

/**
 * REFACTORED: Represents the available app icon themes.
 * Used to decouple the ViewModel from Android Context.
 */
enum class AppIconTheme { BLUE, FOX, AXOLOTL, DRAGON }

data class PetStats(
    val name: String = "Adnap Hsart",
    val level: Int = 1,
    val xp: Int = 0,
    val happiness: Int = 100,
    val energy: Int = 100,
    val currency: Int = 250,
    val accessories: List<AccessoryItem> = emptyList(),
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

    // Analytics: Asynchronously calculated stats exposed via stateIn
    val statsUiState: StateFlow<StatsUiState> = combine(
        repository.getAllHabits(),
        repository.getAllLogs(),
        _uiState.map { it.petStats }
    ) { habits: List<HabitEntity>, logs: List<HabitLogEntity>, petStats: PetStats ->
        val completedLogs = logs.filter { it.isCompleted }
        val totalTasks = completedLogs.size

        // Streak calculation logic
        val streak = calculateStreak(completedLogs)

        // Category progress calculation
        val catProgress = CozyCategory.values().associate { category ->
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
        
        // Use a set to handle distinct dates
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

    private val _petName = MutableStateFlow(sharedPreferences.getString("pet_name", "Your Pet") ?: "Your Pet")
    private val _currentPetType = MutableStateFlow(
        try { PetType.valueOf(sharedPreferences.getString("pet_type", PetType.BLUE.name) ?: PetType.BLUE.name) }
        catch (e: Exception) { PetType.BLUE }
    )

    /**
     * REFACTORED: Expose the app icon theme as a reactive StateFlow.
     * This allows the UI to handle the platform-specific icon switching.
     */
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
                else -> AppIconTheme.BLUE // Fallback
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
            AccessoryItem("hat_1", "Cowboy Hat", 50, com.nightowlcrew.nudgie.R.drawable.zustomize, com.nightowlcrew.nudgie.R.drawable.zustomize, AccessoryCategory.HAT),
            AccessoryItem("glasses_1", "Cool Shades", 100, com.nightowlcrew.nudgie.R.drawable.feed, com.nightowlcrew.nudgie.R.drawable.feed, AccessoryCategory.GLASSES),
            AccessoryItem("outfit_1", "Space Suit", 250, com.nightowlcrew.nudgie.R.drawable.play, com.nightowlcrew.nudgie.R.drawable.play, AccessoryCategory.OUTFIT)
        )
    )

    private val petStatsFlow = combine(_petName, _happiness, _energy, _petLevel, _petXP) { name, h, e, l, xp ->
        PetStats(name, l, xp, h, e)
    }

    private val extendedPetStatsFlow = combine(petStatsFlow, _currency, _accessories) { stats, currency, accessories ->
        stats.copy(currency = currency, accessories = accessories)
    }

    init {
        viewModelScope.launch {
            prepopulateDefaultHabits()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Sync XP and Level with the database record
            repository.getAllLogs().onEach { logs ->
                val completedCount = logs.count { it.isCompleted }
                val totalXp = completedCount * 15
                _petLevel.update { (totalXp / 800) + 1 }
                _petXP.update { totalXp % 800 }
            }.launchIn(viewModelScope)

            val profileStateFlow = combine(_profileUserName, _profileBio, _profileJoinDate, _profileAvatarRes) { name, bio, joinDate, avatar ->
                Triple(name, bio, Pair(joinDate, avatar))
            }

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(today),
                extendedPetStatsFlow,
                profileStateFlow,
                repository.getAllLogs(),
                _currentTheme,
                _currentPetType
            ) { flows: Array<Any?> ->
                val activities = flows[0] as List<ActivityItem>
                val screenTime = flows[1] as ScreenTimeRecord?
                val petStats = flows[2] as PetStats
                val profile = flows[3] as Triple<String, String, Pair<String, Int>>
                val allLogs = flows[4] as List<HabitLogEntity>
                val theme = flows[5] as AppTheme
                val petType = flows[6] as PetType

                val categorized = CozyCategory.values().associateWith { category ->
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
                        it.copy(isEquipped = it.id == accessory.id)
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
        val alreadyAdded = sharedPreferences.getBoolean("default_habits_v7_added", false)
        if (!alreadyAdded) {
            viewModelScope.launch {
                HABIT_TEMPLATES.forEach { (category, templates) ->
                    templates.forEach { template ->
                        repository.insertHabit(
                            HabitEntity(title = template.title, icon = "📌", category = category.name, targetFrequencyPerDay = template.defaultFrequency, isStock = true)
                        )
                    }
                }
                sharedPreferences.edit().putBoolean("default_habits_v7_added", true).apply()
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

        _petXP.update { currentXP ->
            val nextXP = currentXP + 15
            if (nextXP >= 800) {
                _petLevel.update { it + 1 }
                nextXP - 800
            } else {
                nextXP
            }
        }
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
