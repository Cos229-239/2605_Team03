package com.nightowlcrew.nudgie.data

data class ActivityItem(
    val id: String, // String ID for the UI
    val icon: String,
    val description: String,
    val category: String,
    val time: String,
    val isCompleted: Boolean,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isStock: Boolean = false,
    val originalTitle: String = ""
)