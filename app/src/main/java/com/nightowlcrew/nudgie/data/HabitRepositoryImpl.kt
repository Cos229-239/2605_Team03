package com.nightowlcrew.nudgie.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of the HabitRepository.
 * Bridges Room DAO operations with the UI domain models.
 */
class HabitRepositoryImpl(
    private val habitDao: HabitDao,
    private val screenTimeDao: ScreenTimeDao
) : HabitRepository {

    override fun getAllHabitsWithLogs(): Flow<List<ActivityItem>> {
        return habitDao.getHabitsWithLogs().map { list ->
            list.map { it.toActivityItem() }
        }
    }

    override fun getHistoricalLogs(date: String): Flow<List<HabitLogEntity>> {
        return habitDao.getLogsByDate(date)
    }

    override suspend fun insertHabit(habit: HabitEntity): Long {
        return habitDao.insertHabit(habit)
    }

    override suspend fun insertLog(log: HabitLogEntity): Long {
        return habitDao.insertHabitLog(log)
    }

    override suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
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
