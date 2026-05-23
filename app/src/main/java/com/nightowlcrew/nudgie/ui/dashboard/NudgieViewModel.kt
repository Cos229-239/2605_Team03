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

enum class AppTheme { DEFAULT, CYBERPUNK, STEAMPUNK }

/**
 * UI State for the Dashboard screen.
 */
data class DashboardUiState(
    val activities: List<ActivityItem> = emptyList(),
    val currentScreenTimeMillis: Long = 0L,
    val screenTimeGoalMillis: Long = 14400000L, // Default 4 hours (4 * 3600 * 1000)
    val currentTheme: AppTheme = AppTheme.DEFAULT,
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing Dashboard UI state and interactions.
 * Bridges the UI with the Repository layer.
 */
class NudgieViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val isOverScreenTimeLimit: Boolean
        get() = uiState.value.currentScreenTimeMillis > uiState.value.screenTimeGoalMillis

    init {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(today)
            ) { activities, screenTime ->
                _uiState.value.copy(
                    activities = activities,
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
     * Updates the current app theme.
     */
    fun updateTheme(newTheme: AppTheme) {
        _uiState.value = _uiState.value.copy(currentTheme = newTheme)
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

                return NudgieViewModel(repository) as T
            }
        }
    }
}
