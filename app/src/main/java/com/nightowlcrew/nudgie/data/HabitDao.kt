package com.nightowlcrew.nudgie.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity): Long

    @Transaction
    @Query("SELECT * FROM habits")
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>

    /**
     * Queries ALL records from the log table filtered by a specific time/date pattern.
     * Includes orphaned logs (habitId IS NULL) to preserve history.
     */
    @Query("SELECT * FROM habit_logs WHERE completedAtTime LIKE :datePattern || '%'")
    fun getLogsByDate(datePattern: String): Flow<List<HabitLogEntity>>

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
}
