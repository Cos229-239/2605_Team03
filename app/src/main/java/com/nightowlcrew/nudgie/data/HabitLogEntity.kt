package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["habitId"])]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val habitId: Int?,
    val completedAtTime: String,
    val isCompleted: Boolean
)
