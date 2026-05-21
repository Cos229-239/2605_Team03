package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity to store screen time limits and actual usage for a specific date.
 */
@Entity(tableName = "screen_time_records")
data class ScreenTimeRecord(
    @PrimaryKey val date: String,     // Format: YYYY-MM-DD
    val targetLimitMillis: Long,      // User-defined limit in milliseconds
    val actualDurationMillis: Long    // Actual usage in milliseconds
)
