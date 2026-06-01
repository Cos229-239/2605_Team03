package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a scheduled alarm to persist metadata across device reboots.
 */
@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val triggerAtMillis: Long,
    val label: String = "Pet Alert"
)
