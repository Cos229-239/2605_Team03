package com.nightowlcrew.nudgie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO for interacting with screen time records.
 */
@Dao
interface ScreenTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateScreenTime(record: ScreenTimeRecord)

    @Query("SELECT * FROM screen_time_records WHERE dateString = :date LIMIT 1")
    fun getScreenTimeForDay(date: String): Flow<ScreenTimeRecord?>
}
