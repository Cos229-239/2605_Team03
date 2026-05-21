package com.nightowlcrew.nudgie.data

/**
 * Mapper extension functions to convert Database Entities into UI Domain Models.
 */
fun HabitEntity.toActivityItem(lastLog: HabitLogEntity?): ActivityItem {
    return ActivityItem(
        id = this.id,
        icon = this.icon,
        description = this.title,
        time = lastLog?.completedAtTime ?: "--:--",
        isCompleted = lastLog?.isCompleted ?: false
    )
}

/**
 * Converts a Habit with its logs into an ActivityItem based on the most recent log.
 */
fun HabitWithLogs.toActivityItem(): ActivityItem {
    val latestLog = logs.maxByOrNull { it.id } // Assuming higher ID is more recent
    return habit.toActivityItem(latestLog)
}
