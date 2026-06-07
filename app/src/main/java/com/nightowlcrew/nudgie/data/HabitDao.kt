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
    @Query("SELECT * FROM habits WHERE isArchived = 0")
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>

    @Query("SELECT * FROM habits WHERE isArchived = 1 ORDER BY archivedAt DESC")
    fun getArchivedHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_logs")
    fun getAllLogs(): Flow<List<HabitLogEntity>>

    @Query("UPDATE habits SET isArchived = 1, archivedAt = :timestamp WHERE id = :habitId")
    suspend fun archiveHabit(habitId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE habits SET isArchived = 0, archivedAt = NULL WHERE id = :habitId")
    suspend fun restoreHabit(habitId: Int)

    /**
     * Queries ALL records from the log table filtered by a specific time/date pattern.
     * Includes orphaned logs (habitId IS NULL) to preserve history.
     */
    @Query("SELECT * FROM habit_logs WHERE completedAtTime LIKE :datePattern || '%'")
    fun getLogsByDate(datePattern: String): Flow<List<HabitLogEntity>>

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)
}
