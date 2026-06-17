package com.nightowlcrew.nudgie.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(), // Generates a String UUID
    val title: String,
    val icon: String,
    val category: String,
    val targetFrequencyPerDay: Int,
    val isArchived: Boolean = false,
    val archivedAt: Long? = null,
    val isStock: Boolean = false
)