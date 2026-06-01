package com.nightowlcrew.nudgie.data

/**
 * UI Domain Model for displaying tasks/habits in the dashboard.
 */
data class ActivityItem(
    val id: Int, 
    val icon: String, 
    val description: String, 
    val category: String, // CozyCategory name
    val time: String, 
    val isCompleted: Boolean,
    val targetCount: Int = 1,
    val currentCount: Int = 0,
    val isStock: Boolean = false,
    val originalTitle: String = ""
)
