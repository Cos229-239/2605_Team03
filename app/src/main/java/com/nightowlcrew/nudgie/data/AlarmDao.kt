package com.nightowlcrew.nudgie.data

import androidx.room.*

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Query("SELECT * FROM alarms")
    suspend fun getAllAlarms(): List<AlarmEntity>

    @Query("DELETE FROM alarms WHERE triggerAtMillis = :triggerAtMillis")
    suspend fun deleteAlarmByTime(triggerAtMillis: Long)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarms WHERE triggerAtMillis < :currentTime")
    suspend fun deleteOldAlarms(currentTime: Long)
}
