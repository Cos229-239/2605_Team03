package com.nightowlcrew.nudgie.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE isArchived = 0")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 1")
    fun getArchivedHabits(): Flow<List<HabitEntity>>

    @Query("UPDATE habits SET isArchived = 1, archivedAt = :timestamp WHERE id = :habitId")
    suspend fun archiveHabit(habitId: String, timestamp: Long)

    @Query("UPDATE habits SET isArchived = 0, archivedAt = NULL WHERE id = :habitId")
    suspend fun restoreHabit(habitId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity)

    @Query("SELECT * FROM habit_logs WHERE date = :date")
    fun getLogsByDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs")
    fun getAllLogs(): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 0")
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>
}