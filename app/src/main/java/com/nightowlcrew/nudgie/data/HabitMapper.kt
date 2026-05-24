package com.nightowlcrew.nudgie.data

import java.text.SimpleDateFormat
import java.util.*

/**
 * Mapper extension functions to convert Database Entities into UI Domain Models.
 */
fun HabitEntity.toActivityItem(lastLog: HabitLogEntity?, currentCount: Int): ActivityItem {
    var displayDescription = this.title
    
    // Dynamic description for multi-step habits (like Water)
    // If it contains "8 Cups", and we have currentCount = 1, it should say "7 Cups"
    if (this.targetFrequencyPerDay > 1) {
        val remaining = (this.targetFrequencyPerDay - currentCount).coerceAtLeast(0)
        displayDescription = this.title.replace(Regex("\\d+"), remaining.toString())
    }

    return ActivityItem(
        id = this.id,
        icon = this.icon,
        description = displayDescription,
        time = lastLog?.completedAtTime ?: "--:--",
        isCompleted = currentCount >= this.targetFrequencyPerDay,
        targetCount = this.targetFrequencyPerDay,
        currentCount = currentCount
    )
}

/**
 * Converts a Habit with its logs into an ActivityItem based on the most recent log for a specific date.
 * Includes logic for midnight reset and "sleeping" habit persistence until awakening.
 */
fun HabitWithLogs.toActivityItem(todayDate: String, anyHabitLoggedToday: Boolean = true): ActivityItem {
    val isSleepingHabit = habit.title.contains("sleep", ignoreCase = true) || 
                         habit.title.contains("sleeping", ignoreCase = true)

    val todayLogs = logs.filter { it.completedDate == todayDate }
    
    // For sleeping habits, if nothing logged yet today AND they haven't "awakened" 
    // (no other habits logged today), show yesterday's status.
    val relevantLogs = if (isSleepingHabit && todayLogs.isEmpty() && !anyHabitLoggedToday) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(todayDate)
        val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayDate = sdf.format(calendar.time)
            
            val yesterdayLogs = logs.filter { it.completedDate == yesterdayDate }
            if (yesterdayLogs.isNotEmpty()) yesterdayLogs else todayLogs
        } else {
            todayLogs
        }
    } else {
        todayLogs
    }

    val completedCount = relevantLogs.count { it.isCompleted }
    val latestLog = relevantLogs.maxByOrNull { it.id }

    return habit.toActivityItem(latestLog, completedCount)
}
