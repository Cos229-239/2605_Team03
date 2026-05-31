package com.nightowlcrew.nudgie.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HABIT_TEMPLATES
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.HabitLogEntity
import com.nightowlcrew.nudgie.data.HabitRepository
import com.nightowlcrew.nudgie.data.HabitRepositoryImpl
import com.nightowlcrew.nudgie.data.ScreenTimeRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTheme { DEFAULT, CYBERPUNK, STEAMPUNK, GOTH, RETRO_SPACE }

// Data class to hold the pet's current status for the UI
data class PetStats(
    val name: String = "Your Pet",
    val level: Int = 1,
    val xp: Int = 0,
    val happiness: Int = 100,
    val energy: Int = 100
)

/**
 * UI State for the Dashboard screen.
 */
data class DashboardUiState(
    val activities: List<ActivityItem> = emptyList(),
    val categorizedActivities: Map<CozyCategory, List<ActivityItem>> = emptyMap(),
    val currentScreenTimeMillis: Long = 0L,
    val screenTimeGoalMillis: Long = 14400000L, // Default 4 hours (4 * 3600 * 1000)
    val currentTheme: AppTheme = AppTheme.RETRO_SPACE, // Defaulting to your new design
    val petStats: PetStats = PetStats(level = 5, xp = 450, happiness = 80, energy = 65), // Mock data matching Figma
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing Dashboard UI state and interactions.
 * Bridges the UI with the Repository layer.
 */
class NudgieViewModel(
    private val repository: HabitRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val archivedHabits: StateFlow<List<HabitEntity>> = repository.getArchivedHabits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isOverScreenTimeLimit: Boolean
        get() = uiState.value.currentScreenTimeMillis > uiState.value.screenTimeGoalMillis

    private val _currentTheme = MutableStateFlow(
        try {
            AppTheme.valueOf(sharedPreferences.getString("app_theme", AppTheme.RETRO_SPACE.name) ?: AppTheme.RETRO_SPACE.name)
        } catch (e: Exception) {
            AppTheme.RETRO_SPACE
        }
    )

    private val _petName = MutableStateFlow(sharedPreferences.getString("pet_name", "Your Pet") ?: "Your Pet")
    private val _happiness = MutableStateFlow(85)
    private val _energy = MutableStateFlow(62)
    private val _petLevel = MutableStateFlow(5)
    private val _petXP = MutableStateFlow(450)

    init {
        // Prepopulate default habits if it's the first time
        prepopulateDefaultHabits()

        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val petStatsFlow = combine(_petName, _happiness, _energy, _petLevel, _petXP) { name, h, e, l, xp ->
                PetStats(name, l, xp, h, e)
            }

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(today),
                petStatsFlow,
                _currentTheme
            ) { activities, screenTime, petStats, theme ->
                val categorized = CozyCategory.entries.associateWith { category ->
                    activities.filter { it.icon == category.name }
                }.filterValues { it.isNotEmpty() }

                DashboardUiState(
                    activities = activities,
                    categorizedActivities = categorized,
                    currentScreenTimeMillis = screenTime?.actualDurationMillis ?: 0L,
                    screenTimeGoalMillis = screenTime?.targetLimitMillis ?: 14400000L,
                    petStats = petStats,
                    currentTheme = theme,
                    isLoading = false
                )
            }.collect { updatedState ->
                _uiState.value = updatedState
            }
        }
    }

    fun updateTheme(newTheme: AppTheme) {
        _currentTheme.value = newTheme
        sharedPreferences.edit().putString("app_theme", newTheme.name).apply()
    }

    fun updatePetName(newName: String) {
        val limitedName = newName.take(13)
        _petName.value = limitedName
        sharedPreferences.edit().putString("pet_name", limitedName).apply()
    }

    private fun prepopulateDefaultHabits() {
        val alreadyAdded = sharedPreferences.getBoolean("default_habits_v2_added", false)
        if (!alreadyAdded) {
            viewModelScope.launch {
                HABIT_TEMPLATES.forEach { (category, templates) ->
                    templates.forEach { template ->
                        repository.insertHabit(
                            HabitEntity(
                                title = template.title,
                                icon = category.name,
                                targetFrequencyPerDay = template.defaultFrequency,
                                isStock = true
                            )
                        )
                    }
                }
                sharedPreferences.edit().putBoolean("default_habits_v2_added", true).apply()
            }
        }
    }

    fun toggleHabitCompletion(activityItem: ActivityItem) {
        viewModelScope.launch {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val isCompleting = !activityItem.isCompleted
            val log = HabitLogEntity(
                habitId = activityItem.id,
                completedAtTime = currentTime,
                isCompleted = isCompleting
            )
            repository.insertLog(log)

            if (isCompleting) {
                completeHabit()
            } else {
                missedHabit()
            }
        }
    }

    fun addNewHabit(title: String, icon: String, frequency: Int, isStock: Boolean = false, markAsCompleted: Boolean = false) {
        viewModelScope.launch {
            val habit = HabitEntity(
                title = title,
                icon = icon,
                targetFrequencyPerDay = frequency,
                isStock = isStock
            )
            val habitId = repository.insertHabit(habit).toInt()

            if (markAsCompleted) {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val log = HabitLogEntity(
                    habitId = habitId,
                    completedAtTime = currentTime,
                    isCompleted = true
                )
                repository.insertLog(log)
                completeHabit()
            }
        }
    }

    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            val habit = HabitEntity(
                id = id,
                title = "",
                icon = "",
                targetFrequencyPerDay = 0
            )
            repository.deleteHabit(habit)
        }
    }

    fun archiveHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.archiveHabit(habit.id, System.currentTimeMillis())
        }
    }

    fun restoreHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.restoreHabit(habit.id)
        }
    }

    fun updateScreenTimeGoal(hours: Int) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val currentRecord = uiState.value
            val limitMillis = hours.toLong() * 3600000L

            val record = ScreenTimeRecord(
                date = today,
                targetLimitMillis = limitMillis,
                actualDurationMillis = currentRecord.currentScreenTimeMillis
            )
            repository.insertOrUpdateScreenTime(record)
        }
    }

    fun missedHabit() {
        val penalty = 15
        val nonPunishmentFloor = 30
        _happiness.value = (_happiness.value - penalty).coerceAtLeast(nonPunishmentFloor)
    }

    private fun completeHabit() {
        _happiness.value = (_happiness.value + 20).coerceAtMost(100)
        _petXP.value += 15
        if (_petXP.value >= 800) {
            _petLevel.value += 1
            _petXP.value -= 800
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication
                val repository = HabitRepositoryImpl(
                    application.database.habitDao(),
                    application.database.screenTimeDao()
                )
                val sharedPrefs = application.getSharedPreferences("nudgie_prefs", Context.MODE_PRIVATE)
                return NudgieViewModel(repository, sharedPrefs) as T
            }
        }
    }
}
