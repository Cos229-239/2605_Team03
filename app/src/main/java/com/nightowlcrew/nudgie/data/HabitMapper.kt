package com.nightowlcrew.nudgie.data

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
 * Converts a Habit with its logs into an ActivityItem based on the most recent log.
 */
fun HabitWithLogs.toActivityItem(): ActivityItem {
    val completedLogs = logs.filter { it.isCompleted }
    val latestLog = logs.maxByOrNull { it.id } 
    return habit.toActivityItem(latestLog, completedLogs.size)
}
