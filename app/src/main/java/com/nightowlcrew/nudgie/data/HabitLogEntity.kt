package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: String, // Matches HabitEntity's String ID
    val date: String,
    val completedAtTime: String,
    val isCompleted: Boolean = true
)