package com.nightowlcrew.nudgie.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val screenTimeDao: ScreenTimeDao
) : HabitRepository {

    override fun getAllHabitsWithLogs(): Flow<List<ActivityItem>> {
        return combine(habitDao.getAllHabits(), habitDao.getAllLogs()) { habits, logs ->
            habits.map { habit ->
                val habitLogs = logs.filter { it.habitId == habit.id }
                HabitWithLogs(habit, habitLogs).toActivityItem()
            }
        }
    }

    override fun getHistoricalLogs(date: String): Flow<List<HabitLogEntity>> {
        return habitDao.getLogsByDate(date)
    }

    override suspend fun insertHabit(habit: HabitEntity) {
        habitDao.insertHabit(habit)
    }

    override suspend fun insertLog(log: HabitLogEntity) {
        habitDao.insertHabitLog(log)
    }

    override suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    override fun getArchivedHabits(): Flow<List<HabitEntity>> {
        return habitDao.getArchivedHabits()
    }

    override fun getAllHabits(): Flow<List<HabitEntity>> {
        return habitDao.getAllHabits()
    }

    override fun getAllLogs(): Flow<List<HabitLogEntity>> {
        return habitDao.getAllLogs()
    }

    override suspend fun archiveHabit(habitId: String, timestamp: Long) {
        habitDao.archiveHabit(habitId, timestamp)
    }

    override suspend fun restoreHabit(habitId: String) {
        habitDao.restoreHabit(habitId)
    }

    override fun getScreenTimeForDate(date: String): Flow<ScreenTimeRecord?> {
        return screenTimeDao.getRecordForDate(date)
    }

    override suspend fun insertOrUpdateScreenTime(record: ScreenTimeRecord) {
        withContext(Dispatchers.IO) {
            screenTimeDao.insertOrUpdateRecord(record)
        }
    }
}