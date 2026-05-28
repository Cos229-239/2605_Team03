package com.nightowlcrew.nudgie.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppTheme { DEFAULT, CYBERPUNK, STEAMPUNK, GOTH }

/**
 * UI State for the Dashboard screen.
 */
data class DashboardUiState(
    val activities: List<ActivityItem> = emptyList(),
    val categorizedActivities: Map<CozyCategory, List<ActivityItem>> = emptyMap(),
    val currentScreenTimeMillis: Long = 0L,
    val screenTimeGoalMillis: Long = 14400000L, // Default 4 hours (4 * 3600 * 1000)
    val currentTheme: AppTheme = AppTheme.DEFAULT,
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing Dashboard UI state and interactions.
 * Bridges the UI with the Repository layer.
 */
class NudgieViewModel(
    private val repository: HabitRepository,
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModel() {


    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val isOverScreenTimeLimit: Boolean
        get() = uiState.value.currentScreenTimeMillis > uiState.value.screenTimeGoalMillis



    private val _happiness = MutableStateFlow(85)
    val happiness: StateFlow<Int> = _happiness.asStateFlow()

    private val _energy = MutableStateFlow(62)
    val energy: StateFlow<Int> = _energy.asStateFlow()

    private val _petLevel = MutableStateFlow(5)
    val petLevel: StateFlow<Int> = _petLevel.asStateFlow()

    private val _petXP = MutableStateFlow(0)
    val petXP: StateFlow<Int> = _petXP.asStateFlow()
    // ==========================================


    init {
        // Load persisted theme immediately
        val savedThemeName = sharedPreferences.getString("app_theme", AppTheme.DEFAULT.name)
        val initialTheme = try {
            AppTheme.valueOf(savedThemeName ?: AppTheme.DEFAULT.name)
        } catch (e: Exception) {
            AppTheme.DEFAULT
        }
        _uiState.value = _uiState.value.copy(currentTheme = initialTheme)

        // Prepopulate default habits if it's the first time
        prepopulateDefaultHabits()

        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(today)
            ) { activities, screenTime ->
                val categorized = CozyCategory.entries.associateWith { category ->
                    activities.filter { it.icon == category.name }
                }.filterValues { it.isNotEmpty() }

                _uiState.value.copy(
                    activities = activities,
                    categorizedActivities = categorized,
                    currentScreenTimeMillis = screenTime?.actualDurationMillis ?: 0L,
                    screenTimeGoalMillis = screenTime?.targetLimitMillis ?: 14400000L,
                    isLoading = false
                )
            }.collect { updatedState ->
                _uiState.value = updatedState
            }
        }
    }

    fun updateTheme(newTheme: AppTheme) {
        _uiState.value = _uiState.value.copy(currentTheme = newTheme)
        sharedPreferences.edit().putString("app_theme", newTheme.name).apply()
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
                                targetFrequencyPerDay = template.defaultFrequency
                            )
                        )
                    }
                }
                sharedPreferences.edit().putBoolean("default_habits_v2_added", true).apply()
            }
        }
    }

    /**
     * Toggles the completion status of a habit by adding a new log entry.
     */
    fun toggleHabitCompletion(activityItem: ActivityItem) {
        viewModelScope.launch {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

            // Check if they are completing it or un-checking it
            val isCompleting = !activityItem.isCompleted

            val log = HabitLogEntity(
                habitId = activityItem.id,
                completedAtTime = currentTime,
                isCompleted = isCompleting
            )
            repository.insertLog(log)

            // ----------------------------------------------------
            // NEW: Fire the Pet logic based on the user's action!
            // ----------------------------------------------------
            if (isCompleting) {
                completeHabit() // Give XP and Happiness
            } else {
                missedHabit()   // Deduct Happiness if they unchecked it
            }
            android.util.Log.d("PET_STATS", "Level: ${petLevel.value} | XP: ${petXP.value} | Happiness: ${happiness.value}")
        }
    }

    fun addNewHabit(title: String, icon: String, frequency: Int, markAsCompleted: Boolean = false) {
        viewModelScope.launch {
            val habit = HabitEntity(
                title = title,
                icon = icon,
                targetFrequencyPerDay = frequency
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
                completeHabit() // Trigger pet rewards here too
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


    // ==========================================
    // PET LOGIC (Ported from C++ NudgiePet.cpp)
    // ==========================================

    fun drainEnergy(amount: Int) {
        _energy.value = (_energy.value - amount).coerceAtLeast(0)
    }

    fun missedHabit() {
        val penalty = 15
        val nonPunishmentFloor = 30
        _happiness.value = (_happiness.value - penalty).coerceAtLeast(nonPunishmentFloor)
    }

    private fun completeHabit() {
        _happiness.value = (_happiness.value + 20).coerceAtMost(100)

        _petXP.value += 15
        if (_petXP.value >= 100) {
            _petLevel.value += 1
            _petXP.value -= 100
        }
    }

    fun feedPet() {
        _happiness.value = (_happiness.value + 10).coerceAtMost(100)
        _energy.value = (_energy.value + 15).coerceAtMost(100)
    }

    // ==========================================

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication
                val repository = HabitRepositoryImpl(
                    application.database.habitDao(),
                    application.database.screenTimeDao()
                )
                val sharedPrefs = application.getSharedPreferences("nudgie_prefs", android.content.Context.MODE_PRIVATE)

                return NudgieViewModel(repository, sharedPrefs) as T
            }
        }
    }
}