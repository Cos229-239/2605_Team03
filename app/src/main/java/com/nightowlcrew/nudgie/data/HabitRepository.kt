package com.nightowlcrew.nudgie.data

import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllHabitsWithLogs(): Flow<List<ActivityItem>>
    fun getHistoricalLogs(date: String): Flow<List<HabitLogEntity>>

    suspend fun insertHabit(habit: HabitEntity)
    suspend fun insertLog(log: HabitLogEntity)
    suspend fun deleteHabit(habit: HabitEntity)

    fun getArchivedHabits(): Flow<List<HabitEntity>>
    fun getAllHabits(): Flow<List<HabitEntity>>
    fun getAllLogs(): Flow<List<HabitLogEntity>>

    suspend fun archiveHabit(habitId: String, timestamp: Long = System.currentTimeMillis())
    suspend fun restoreHabit(habitId: String)

    fun getScreenTimeForDate(date: String): Flow<ScreenTimeRecord?>
    suspend fun insertOrUpdateScreenTime(record: ScreenTimeRecord)
}