package com.nightowlcrew.nudgie.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun HabitEntity.toActivityItem(lastLog: HabitLogEntity?, currentCount: Int): ActivityItem {
    var displayDescription = this.title

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

fun HabitWithLogs.toActivityItem(): ActivityItem {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todaysLogs = logs.filter { it.date == today }
    val completedCount = todaysLogs.count { it.isCompleted }

    // Sorts by completed time because UUIDs don't sort chronologically
    val latestLog = todaysLogs.maxByOrNull { it.completedAtTime }

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