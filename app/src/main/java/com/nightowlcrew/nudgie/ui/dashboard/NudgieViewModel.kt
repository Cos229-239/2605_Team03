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
import com.nightowlcrew.nudgie.utils.IconSwitcherManager
import com.nightowlcrew.nudgie.utils.PetAssetManager
import com.nightowlcrew.nudgie.utils.PetType
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

data class PetStats(
    val name: String = "Your Pet",
    val level: Int = 1,
    val xp: Int = 0,
    val happiness: Int = 100,
    val energy: Int = 100,
    val currency: Int = 250, // <-- Added Currency here
    val accessories: List<AccessoryItem> = emptyList() // <-- Added Accessories here
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
    val isLoading: Boolean = true
)

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
        try { AppTheme.valueOf(sharedPreferences.getString("app_theme", AppTheme.RETRO_SPACE.name) ?: AppTheme.RETRO_SPACE.name) }
        catch (e: Exception) { AppTheme.RETRO_SPACE }
    )

    private val _petName = MutableStateFlow(sharedPreferences.getString("pet_name", "Your Pet") ?: "Your Pet")
    private val _currentPetType = MutableStateFlow(
        try { PetType.valueOf(sharedPreferences.getString("pet_type", PetType.BLUE.name) ?: PetType.BLUE.name) }
        catch (e: Exception) { PetType.BLUE }
    )
    private val _happiness = MutableStateFlow(85)
    private val _energy = MutableStateFlow(62)
    private val _petLevel = MutableStateFlow(5)
    private val _petXP = MutableStateFlow(450)

    private val _currency = MutableStateFlow(sharedPreferences.getInt("pet_currency", 250))

    // Dummy Shop Items (Using generic Android icons temporarily until you add your PNGs)
    private val _accessories = MutableStateFlow(
        listOf(
            AccessoryItem("hat_1", "Cowboy Hat", 50, com.nightowlcrew.nudgie.R.drawable.zustomize, com.nightowlcrew.nudgie.R.drawable.zustomize, AccessoryCategory.HAT),
            AccessoryItem("glasses_1", "Cool Shades", 100, com.nightowlcrew.nudgie.R.drawable.feed, com.nightowlcrew.nudgie.R.drawable.feed, AccessoryCategory.GLASSES),
            AccessoryItem("outfit_1", "Space Suit", 250, com.nightowlcrew.nudgie.R.drawable.play, com.nightowlcrew.nudgie.R.drawable.play, AccessoryCategory.OUTFIT)
        )
    )

    init {
        viewModelScope.launch {
            prepopulateDefaultHabits()
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val petStatsFlow = combine(_petName, _happiness, _energy, _petLevel, _petXP) { name, h, e, l, xp ->
                PetStats(name, l, xp, h, e) // Currency and Accessories added in the next step
            }

            // Combine the pet stats with the shop data
            val extendedPetStatsFlow = combine(petStatsFlow, _currency, _accessories) { stats, currency, accessories ->
                stats.copy(currency = currency, accessories = accessories)
            }

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(today),
                extendedPetStatsFlow, // <--- Using the combined state
                _currentTheme,
                _currentPetType
            ) { activities, screenTime, petStats, theme, petType ->
                val categorized = CozyCategory.entries.associateWith { category ->
                    activities.filter { it.category == category.name }
                }.filterValues { it.isNotEmpty() }

                val tasksDone = activities.sumOf { it.currentCount }

                DashboardUiState(
                    activities = activities,
                    categorizedActivities = categorized,
                    currentScreenTimeMillis = screenTime?.actualDurationMillis ?: 0L,
                    screenTimeGoalMillis = screenTime?.targetLimitMillis ?: 14400000L,
                    petStats = petStats,
                    currentTheme = theme,
                    currentPetType = petType,
                    totalTasksDone = tasksDone,
                    isLoading = false
                )
            }.collect { updatedState ->
                _uiState.value = updatedState
            }
        }
    }

    fun buyAccessory(accessory: AccessoryItem) {
        val currentCurrency = _currency.value
        if (currentCurrency >= accessory.cost && !accessory.isPurchased) {
            _currency.value = currentCurrency - accessory.cost
            sharedPreferences.edit().putInt("pet_currency", _currency.value).apply()

            // Update list to trigger UI recomposition
            _accessories.value = _accessories.value.map {
                if (it.id == accessory.id) it.copy(isPurchased = true) else it
            }
        }
    }

    fun equipAccessory(accessory: AccessoryItem) {
        if (accessory.isPurchased) {
            _accessories.value = _accessories.value.map {
                // Un-equip other items of the same category so they don't stack
                if (it.category == accessory.category) {
                    it.copy(isEquipped = it.id == accessory.id)
                } else {
                    it
                }
            }
        }
    }

    // --- EVERYTHING BELOW HERE REMAINS UNCHANGED ---

    fun updateTheme(theme: AppTheme) {
        _currentTheme.value = theme
        sharedPreferences.edit().putString("app_theme", theme.name).apply()
    }

    fun updatePetName(newName: String) {
        _petName.value = newName
        sharedPreferences.edit().putString("pet_name", newName).apply()
    }

    fun updatePetType(newType: PetType) {
        _currentPetType.value = newType
        sharedPreferences.edit().putString("pet_type", newType.name).apply()
    }

    fun syncAppIcon(context: Context) {
        IconSwitcherManager.switchToIcon(context, PetAssetManager.getNudgieIcon(_currentPetType.value))
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
            val record = com.nightowlcrew.nudgie.data.ScreenTimeRecord(date = today, targetLimitMillis = limitMillis, actualDurationMillis = currentRecord.currentScreenTimeMillis)
            repository.insertOrUpdateScreenTime(record)
        }
    }

    fun missedHabit() {
        val penalty = 15
        val nonPunishmentFloor = 30
        _happiness.value = (_happiness.value - penalty).coerceAtLeast(nonPunishmentFloor)
    }

    fun drainEnergy(amount: Int) {
        _energy.value = (_energy.value - amount).coerceAtLeast(0)
    }

    private fun completeHabit() {
        _happiness.value = (_happiness.value + 20).coerceAtMost(100)
        _petXP.value += 15
        _currency.value += 5
        sharedPreferences.edit().putInt("pet_currency", _currency.value).apply()

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
                val repository = HabitRepositoryImpl(application.database.habitDao(), application.database.screenTimeDao())
                val sharedPrefs = application.getSharedPreferences("nudgie_prefs", Context.MODE_PRIVATE)
                return NudgieViewModel(repository, sharedPrefs) as T
            }
        }
    }
}