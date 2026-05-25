package com.nightowlcrew.nudgie.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Habit-related data operations.
 * Acts as the abstract data pipeline for the presentation layer.
 */
interface HabitRepository {
    fun getAllHabitsWithLogs(): Flow<List<ActivityItem>>
    fun getHistoricalLogs(date: String): Flow<List<HabitLogEntity>>
    suspend fun insertHabit(habit: HabitEntity): Long
    suspend fun insertLog(log: HabitLogEntity): Long
    suspend fun deleteHabit(habit: HabitEntity)

    // Screen Time Operations
    /**
     * Retrieves the screen time record for a specific date.
     */
    fun getScreenTimeForDate(date: String): Flow<ScreenTimeRecord?>

    /**
     * Inserts or updates a screen time record.
     */
    suspend fun insertOrUpdateScreenTime(record: ScreenTimeRecord)
}
