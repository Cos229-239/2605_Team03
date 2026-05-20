package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val icon: String,
    val targetFrequencyPerDay: Int,
    val createdAt: Long = System.currentTimeMillis()
)
