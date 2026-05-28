package com.nightowlcrew.nudgie.ui.dashboard

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTheme { DEFAULT, CYBERPUNK, STEAMPUNK, GOTH, RETRO_SPACE }

// Data class to hold the pet's current status for the UI
data class PetStats(
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
    private val sharedPreferences: android.content.SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val isOverScreenTimeLimit: Boolean
        get() = uiState.value.currentScreenTimeMillis > uiState.value.screenTimeGoalMillis

    init {
        // Load persisted theme immediately
        val savedThemeName = sharedPreferences.getString("app_theme", AppTheme.RETRO_SPACE.name)
        val initialTheme = try {
            AppTheme.valueOf(savedThemeName ?: AppTheme.RETRO_SPACE.name)
        } catch (e: Exception) {
            AppTheme.RETRO_SPACE
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

    /**
     * Updates the current app theme and persists the choice.
     */
    fun updateTheme(newTheme: AppTheme) {
        _uiState.value = _uiState.value.copy(currentTheme = newTheme)
        sharedPreferences.edit().putString("app_theme", newTheme.name).apply()
    }

    /**
     * Prepopulates the database with a set of default "stock" habits on first run.
     */
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
            val log = HabitLogEntity(
                habitId = activityItem.id,
                completedAtTime = currentTime,
                isCompleted = !activityItem.isCompleted
            )
            repository.insertLog(log)
        }
    }

    /**
     * Adds a new habit to the database.
     * Optionally marks it as completed for the current day immediately.
     */
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
            }
        }
    }

    /**
     * Deletes a specific habit.
     */
    fun deleteHabit(id: Int) {
        viewModelScope.launch {
            // Note: Since deleteHabit requires a HabitEntity, we create a shallow one with the ID.
            // Room uses the primary key (@PrimaryKey) for deletion.
            val habit = HabitEntity(
                id = id,
                title = "",
                icon = "",
                targetFrequencyPerDay = 0
            )
            repository.deleteHabit(habit)
        }
    }

    /**
     * Updates the screen time goal for the current day using hours.
     * Conversion: hours * 3,600,000 ms
     */
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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication

                // Get the repository through our lazy database property
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