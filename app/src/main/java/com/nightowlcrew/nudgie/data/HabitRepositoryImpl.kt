package com.nightowlcrew.nudgie.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Concrete implementation of the HabitRepository.
 * Bridges Room DAO operations with the UI domain models.
 */
class HabitRepositoryImpl(private val habitDao: HabitDao) : HabitRepository {

    override fun getAllHabitsWithLogs(): Flow<List<ActivityItem>> {
        return habitDao.getHabitsWithLogs().map { list ->
            list.map { it.toActivityItem() }
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
}
