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
    suspend fun insertOrUpdateRecord(record: ScreenTimeRecord)

    @Query("SELECT * FROM screen_time_records WHERE date = :date LIMIT 1")
    fun getRecordForDate(date: String): Flow<ScreenTimeRecord?>

    @Query("SELECT * FROM screen_time_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<ScreenTimeRecord>>
}
