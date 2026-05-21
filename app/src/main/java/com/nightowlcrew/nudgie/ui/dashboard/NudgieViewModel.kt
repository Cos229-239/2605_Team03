package com.nightowlcrew.nudgie.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewModelScope
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.HabitLogEntity
import com.nightowlcrew.nudgie.data.HabitRepository
import com.nightowlcrew.nudgie.data.HabitRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * UI State for the Dashboard screen.
 */
data class DashboardUiState(
    val activities: List<ActivityItem> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * ViewModel for managing Dashboard UI state and interactions.
 * Bridges the UI with the Repository layer.
 */
class NudgieViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllHabitsWithLogs().collect { activities ->
                _uiState.value = DashboardUiState(activities = activities, isLoading = false)
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
     */
    fun addNewHabit(title: String, icon: String, frequency: Int) {
        viewModelScope.launch {
            val habit = HabitEntity(
                title = title,
                icon = icon,
                targetFrequencyPerDay = frequency
            )
            repository.insertHabit(habit)
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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication
                // Get the repository through our lazy database property
                val repository = HabitRepositoryImpl(application.database.habitDao())

                return NudgieViewModel(repository) as T
            }
        }
    }
}
