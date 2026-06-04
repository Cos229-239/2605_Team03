package com.nightowlcrew.nudgie.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        category = this.category,
        time = lastLog?.completedAtTime ?: "--:--",
        isCompleted = currentCount >= this.targetFrequencyPerDay,
        targetCount = this.targetFrequencyPerDay,
        currentCount = currentCount,
        isStock = this.isStock,
        originalTitle = this.title
    )
}

/**
 * Converts a Habit with its logs into an ActivityItem based on the most recent log.
 * Filters logs to only include those from today.
 */
fun HabitWithLogs.toActivityItem(): ActivityItem {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todaysLogs = logs.filter { it.date == today }
    val completedCount = todaysLogs.count { it.isCompleted }
    val latestLog = todaysLogs.maxByOrNull { it.id } 
    return habit.toActivityItem(latestLog, completedCount)
}

fun ActivityItem.toEntity() = HabitEntity(
    id = this.id,
    title = this.originalTitle,
    icon = this.icon,
    category = this.category,
    targetFrequencyPerDay = this.targetCount,
    isStock = this.isStock
)
