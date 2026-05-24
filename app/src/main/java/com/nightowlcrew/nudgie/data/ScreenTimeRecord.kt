package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_time_records")
data class ScreenTimeRecord(
    @PrimaryKey val date: String,     
    val targetLimitMillis: Long,      
    val actualDurationMillis: Long,
    val warningNotified: Boolean = false // NEW FIELD
)
