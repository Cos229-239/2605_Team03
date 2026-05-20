package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity to store screen time limits and actual usage for a specific date.
 */
@Entity(tableName = "screen_time_records")
data class ScreenTimeRecord(
    @PrimaryKey val dateString: String, // Format: YYYY-MM-DD
    val limitMinutes: Int,              // User-defined limit from the slider
    val actualMinutes: Int              // Actual usage fetched from the system
)
