package com.nightowlcrew.nudgie.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Habit-related data operations.
 * Acts as the abstract data pipeline for the presentation layer.
 */
interface HabitRepository {
    fun getAllHabitsWithLogs(): Flow<List<ActivityItem>>
    fun getHistoricalLogs(date: String): Flow<List<HabitLogEntity>>
    suspend fun insertHabit(habit: HabitEntity)
    suspend fun insertLog(log: HabitLogEntity)
    suspend fun deleteHabit(habit: HabitEntity)
}
