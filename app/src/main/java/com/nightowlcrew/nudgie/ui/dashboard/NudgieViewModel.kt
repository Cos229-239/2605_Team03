package com.nightowlcrew.nudgie.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.*
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
    val message: String? = null,
    val isAwaitingReply: Boolean = false,
    val currentActivity: String = "IDLE"
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

    private val _petMessage = MutableStateFlow<String?>(null)
    private val _isAwaitingReply = MutableStateFlow(false)
    private var messageJob: kotlinx.coroutines.Job? = null
    private val _currentActivity = MutableStateFlow("IDLE")


    val statsUiState: StateFlow<StatsUiState> = combine(
        repository.getAllHabits(),
        repository.getAllLogs(),
        _uiState.map { it.petStats }
    ) { habits: List<HabitEntity>, logs: List<HabitLogEntity>, petStats: PetStats ->
        val completedLogs = logs.filter { it.isCompleted }
        val totalTasks = completedLogs.size
        val streak = calculateStreak(completedLogs)

        val catProgress = CozyCategory.values().associate { category ->
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
            completedLogs.map { it.date }.filter { it.isNotEmpty() }.toSet().toList()
                .sortedDescending()
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
        } catch (e: Exception) {
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
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isOverScreenTimeLimit: Boolean get() = uiState.value.currentScreenTimeMillis > uiState.value.screenTimeGoalMillis

    private val _currentTheme = MutableStateFlow(
        try {
            AppTheme.valueOf(
                sharedPreferences.getString("app_theme", AppTheme.RETRO_SPACE.name)
                    ?: AppTheme.RETRO_SPACE.name
            )
        } catch (e: Exception) {
            AppTheme.RETRO_SPACE
        }
    )

    private val _petName =
        MutableStateFlow(sharedPreferences.getString("pet_name", "Your Pet") ?: "Your Pet")
    private val _currentPetType = MutableStateFlow(
        try {
            PetType.valueOf(
                sharedPreferences.getString("pet_type", PetType.BLUE.name) ?: PetType.BLUE.name
            )
        } catch (e: Exception) {
            PetType.BLUE
        }
    )

    val appIconTheme: StateFlow<AppIconTheme> = _currentPetType.map { petType ->
        when (petType) {
            PetType.BLUE -> AppIconTheme.BLUE
            PetType.FOX -> AppIconTheme.FOX
            PetType.AXOLOTL -> AppIconTheme.AXOLOTL
            PetType.DRAGON -> AppIconTheme.DRAGON
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppIconTheme.BLUE)

    private val _happiness = MutableStateFlow(85)
    private val _energy = MutableStateFlow(62)
    private val _petLevel = MutableStateFlow(5)
    private val _petXP = MutableStateFlow(450)
    private val _currency = MutableStateFlow(sharedPreferences.getInt("pet_currency", 250))

    // THE EXPANDED SHOP INVENTORY
    private val _accessories = MutableStateFlow(
        listOf(
            // Clothes
            AccessoryItem(
                "pink_hat",
                "Pink Hat",
                60,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                AccessoryCategory.CLOTHES,
                "A stylish neon pink cap."
            ),
            AccessoryItem(
                "cool_shades",
                "Cool Shades",
                100,
                com.nightowlcrew.nudgie.R.drawable.feed,
                com.nightowlcrew.nudgie.R.drawable.feed,
                AccessoryCategory.CLOTHES,
                "Dark UV protection for cool pets."
            ),
            AccessoryItem(
                "space_suit",
                "Space Suit",
                250,
                com.nightowlcrew.nudgie.R.drawable.play,
                com.nightowlcrew.nudgie.R.drawable.play,
                AccessoryCategory.CLOTHES,
                "Ready for zero gravity orbits."
            ),
            // Toys
            AccessoryItem(
                "squeaky_bone",
                "Squeaky Bone",
                75,
                com.nightowlcrew.nudgie.R.drawable.play,
                com.nightowlcrew.nudgie.R.drawable.play,
                AccessoryCategory.TOYS,
                "Makes a loud squeak when chewed."
            ),
            AccessoryItem(
                "yarn_ball",
                "Yarn Ball",
                40,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                com.nightowlcrew.nudgie.R.drawable.zustomize,
                AccessoryCategory.TOYS,
                "Perfect for rolling around the floor."
            ),
            // Food
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
            // Stat Boosts
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
            prepopulateDefaultHabits()

            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = when (hour) {
                in 5..11 -> PetDialogBank.morningGreetings.random()
                in 12..17 -> PetDialogBank.afternoonGreetings.random()
                in 18..21 -> PetDialogBank.eveningGreetings.random()
                else -> PetDialogBank.lateNightGreetings.random()
            }
            showMessage(greeting, 6000L, isInteractive = false)

            launch {
                delay(15000L)
                while (true) {
                    delay(30000L)
                    if (_petMessage.value == null) {
                        val happiness = _happiness.value
                        val energy = _energy.value

                        val messageType = when {
                            happiness < 50 -> "Sad"
                            energy < 40 -> "Tired"
                            (1..10).random() > 7 -> "Question"
                            else -> "Thought"
                        }

                        when (messageType) {
                            "Sad" -> showMessage(
                                PetDialogBank.sadThoughts.random(),
                                isInteractive = false
                            )

                            "Tired" -> showMessage(
                                PetDialogBank.tiredThoughts.random(),
                                isInteractive = false
                            )

                            "Question" -> showMessage(
                                PetDialogBank.questions.random(),
                                isInteractive = true
                            )

                            else -> {
                                // Check for equipped items
                                val activeToy =
                                    _accessories.value.firstOrNull { it.isEquipped && it.category == AccessoryCategory.TOYS }
                                val isWearingSpaceSuit =
                                    _accessories.value.any { it.isEquipped && it.id == "space_suit" }

                                // Decide what to say based on context
                                val contextAwareMessage = when {
                                    activeToy != null -> "I'm having so much fun chasing this ${activeToy.name}! 🎾"
                                    isWearingSpaceSuit -> "I feel like a real astronaut in this suit! Shall we explore the galaxy? 🚀"
                                    else -> PetDialogBank.idleThoughts.random()
                                }

                                showMessage(contextAwareMessage, 5000L, isInteractive = false)
                            }
                        }
                    }
                }
            }

            val petStatsFlow = combine(
                _petName,
                _happiness,
                _energy,
                _petLevel,
                _petXP
            ) { name, h, e, l, xp -> PetStats(name, l, xp, h, e) }

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

            combine(
                repository.getAllHabitsWithLogs(),
                repository.getScreenTimeForDate(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(Date())
                ),
                extendedPetStatsFlow,
                _currentTheme,
                _currentPetType,
                _currentActivity
            ) { args ->
                // Kotlin passes everything into a single 'args' array here
                val activities = args[0] as List<ActivityItem>
                val screenTime = args[1] as ScreenTimeRecord?
                val petStats = args[2] as PetStats
                val theme = args[3] as AppTheme
                val petType = args[4] as PetType
                val activity = args[5] as String

                val updatedPetStats = petStats.copy(currentActivity = activity)
                val categorized = CozyCategory.values()
                    .associateWith { category -> activities.filter { it.category == category.name } }
                    .filterValues { it.isNotEmpty() }
                val tasksDone = activities.sumOf { it.currentCount }

                DashboardUiState(
                    activities = activities,
                    categorizedActivities = categorized,
                    currentScreenTimeMillis = screenTime?.actualDurationMillis ?: 0L,
                    screenTimeGoalMillis = screenTime?.targetLimitMillis ?: 14400000L,
                    currentTheme = theme,
                    petStats = updatedPetStats,
                    currentPetType = petType,
                    totalTasksDone = tasksDone,
                    isLoading = false
                )
            }.onEach { _uiState.value = it }.launchIn(viewModelScope)
        }
    }
    private fun showMessage(message: String, durationMillis: Long = 4000L, isInteractive: Boolean = false) {
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

    // THE NEW SHOP PURCHASING LOGIC
    fun buyAccessory(accessory: AccessoryItem) {
        val currentCurrency = _currency.value
        if (currentCurrency < accessory.cost) {
            showMessage("Not enough gems! 💎", 2000L, isInteractive = false)
            return
        }

        if ((accessory.category == AccessoryCategory.CLOTHES || accessory.category == AccessoryCategory.TOYS) && accessory.isPurchased) {
            return
        }

        _currency.value = currentCurrency - accessory.cost
        sharedPreferences.edit().putInt("pet_currency", _currency.value).apply()

        if (accessory.category == AccessoryCategory.STAT_BOOST || accessory.category == AccessoryCategory.FOOD) {
            applyStatEffect(accessory.statEffect)
            showMessage("Consumed ${accessory.name}! ✨", 3000L, isInteractive = false)
        } else {
            _accessories.value = _accessories.value.map {
                if (it.id == accessory.id) it.copy(isPurchased = true) else it
            }
            showMessage("Purchased ${accessory.name}! 🎉", 3000L, isInteractive = false)
        }
    }

    private fun applyStatEffect(effect: String) {
        if (effect.isEmpty()) return
        val parts = effect.split("+")
        if (parts.size != 2) return
        val value = parts[1].toIntOrNull() ?: 0

        when (parts[0]) {
            "ENERGY" -> _energy.value = (_energy.value + value).coerceAtMost(100)
            "HAPPINESS" -> _happiness.value = (_happiness.value + value).coerceAtMost(100)
        }
    }

    fun equipAccessory(accessory: AccessoryItem) {
        if (accessory.isPurchased) {
            _accessories.value = _accessories.value.map { item ->
                if (item.category == accessory.category) {
                    if (item.id == accessory.id) item.copy(isEquipped = !item.isEquipped) else item.copy(isEquipped = false)
                } else item
            }
        }
    }

    fun updateTheme(theme: AppTheme) { _currentTheme.value = theme; sharedPreferences.edit().putString("app_theme", theme.name).apply() }
    fun updatePetName(newName: String) { _petName.value = newName; sharedPreferences.edit().putString("pet_name", newName).apply() }
    fun updatePetType(newType: PetType) { _currentPetType.value = newType; sharedPreferences.edit().putString("pet_type", newType.name).apply() }

    private fun prepopulateDefaultHabits() {
        val alreadyAdded = sharedPreferences.getBoolean("default_habits_v7_added", false)
        if (!alreadyAdded) {
            viewModelScope.launch {
                HABIT_TEMPLATES.forEach { (category, templates) ->
                    templates.forEach { template -> repository.insertHabit(HabitEntity(title = template.title, icon = "📌", category = category.name, targetFrequencyPerDay = template.defaultFrequency, isStock = true)) }
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
            repository.insertLog(HabitLogEntity(habitId = activityItem.id, completedAtTime = currentTime, date = todayDate, isCompleted = isCompleting))
            if (isCompleting) completeHabit() else missedHabit()
        }
    }

    fun addNewHabit(title: String, category: String, frequency: Int, isStock: Boolean = false, markAsCompleted: Boolean = false) {
        viewModelScope.launch {
            val now = Date()
            val matchResult = Regex("^(\\p{So}|\\p{Sk})\\s+(.*)$").find(title)
            val (icon, finalTitle) = if (matchResult != null) matchResult.groupValues[1] to matchResult.groupValues[2] else "📌" to title
            val habitId = repository.insertHabit(HabitEntity(title = finalTitle, icon = icon, category = category, targetFrequencyPerDay = frequency, isStock = isStock)).toInt()

            if (markAsCompleted) {
                repository.insertLog(HabitLogEntity(habitId = habitId, completedAtTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now), date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(now), isCompleted = true))
                completeHabit()
            }
        }
    }

    fun deleteHabit(id: Int) { viewModelScope.launch { repository.deleteHabit(HabitEntity(id = id, title = "", icon = "", targetFrequencyPerDay = 0)) } }
    fun archiveHabit(habit: HabitEntity) { viewModelScope.launch { repository.archiveHabit(habit.id) } }
    fun restoreHabit(habit: HabitEntity) { viewModelScope.launch { repository.restoreHabit(habit.id) } }
    fun updateScreenTimeGoal(newGoalHours: Int) { viewModelScope.launch { repository.insertOrUpdateScreenTime(ScreenTimeRecord(date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()), targetLimitMillis = newGoalHours.toLong() * 3600000L, actualDurationMillis = _uiState.value.currentScreenTimeMillis)) } }

    fun missedHabit() { _happiness.value = (_happiness.value - 15).coerceAtLeast(30) }
    fun drainEnergy(amount: Int) { _energy.value = (_energy.value - amount).coerceAtLeast(0) }

    private fun completeHabit() {
        _happiness.value = (_happiness.value + 20).coerceAtMost(100)
        _petXP.value += 15
        _currency.value += 5
        sharedPreferences.edit().putInt("pet_currency", _currency.value).apply()

        showMessage(PetDialogBank.praise.random())

        if (_petXP.value >= 800) {
            _petLevel.value += 1
            _petXP.value -= 800
            val levelUpPhrases = listOf("I Leveled Up!! 🎊 We are getting stronger!", "Level UP! 🌟 Look at my stats now!", "Powering up! 🔋 Thanks for the hard work!")
            showMessage(levelUpPhrases.random(), 6000L)
        }
    }

    fun petTheNudgie() {
        if (_happiness.value < 100) _happiness.value += 1
        showMessage(PetDialogBank.tapReactions.random(), 2000L)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NudgieApplication
                return NudgieViewModel(HabitRepositoryImpl(application.database.habitDao(), application.database.screenTimeDao()), application.getSharedPreferences("nudgie_prefs", Context.MODE_PRIVATE)) as T
            }
        }
    }
}

// --- PET DIALOGUE BANK ---
object PetDialogBank {
    val morningGreetings = listOf("Good morning! ☀️ Ready to conquer the day?", "Morning! Did you sleep well? 🛌", "Rise and shine! Let's get some tasks done! ☕", "Early bird gets the XP! 🦅", "A fresh start! What's first on the list? 🌱")
    val afternoonGreetings = listOf("Good afternoon! 🌤️ Halfway through the day!", "Afternoon check-in! Don't forget to stretch. 🧘", "Lunch break? Or task-crushing time? 🥪", "Hope your day is going great so far! ✨")
    val eveningGreetings = listOf("Good evening! 🌙 Time to wind down soon.", "Evening! Let's review what we finished today. 📝", "Great job surviving the day! Ready to relax? 🛋️", "Sunset vibes. Let's finish strong! 🌆")
    val lateNightGreetings = listOf("You're up late! 🦉 Rest is a habit too, you know.", "Night owl mode activated! 🌙", "Past midnight... go to sleep soon! 💤", "Still grinding? Don't forget to rest your eyes. 👀")
    val sadThoughts = listOf("I'm feeling a bit lonely... let's complete a task! 💔", "Could we do something fun? I'm sad. 🌧️", "A completed habit would really cheer me up! 🥺")
    val tiredThoughts = listOf("Yawn... running low on energy. Time for a break? 🔋", "My battery is running on fumes... 🪫", "So... sleepy... 🥱")
    val questions = listOf("How is your day going so far? 🌟", "What is our main goal for today? 🎯", "Are you remembering to drink water? 💧", "What's the best thing that happened today? ✨", "Are you feeling productive right now? 📈")
    val idleThoughts = listOf("Just hanging out... 🦠", "You're doing great! ✨", "I wonder what level 20 looks like... 🤔", "Nudgie loves you! ❤️", "A clean to-do list is a happy to-do list! 📋", "My pixels are tingling! ⚡")
    val praise = listOf("Great job! 🌟", "Way to go! 🚀", "Keep it up! 🔥", "XP get! 💎", "You're crushing it today! ⚡", "Task complete! Proud of you. 😎", "Boom! Another one bites the dust. 💥")
    val tapReactions = listOf("Purrrrr...", "Happy!", "❤️", "Yay!", "Hehehe that tickles! 🤭", "Aha! Keep the pats coming. 🥰", "Mutually assured happiness! ✨", "You're my favorite human. 🥺")
}