package com.nightowlcrew.nudgie.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WalkProgress(
    val currentSteps: Int,
    val targetSteps: Int,
    val currentDistance: Float,
    val targetDistance: Float,
    val lastWalkDate: String
)

class WalkProgressManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("walk_progress_prefs", Context.MODE_PRIVATE)

    fun saveProgress(progress: WalkProgress) {
        prefs.edit {
            putInt("current_steps", progress.currentSteps)
            putInt("target_steps", progress.targetSteps)
            putFloat("current_distance", progress.currentDistance)
            putFloat("target_distance", progress.targetDistance)
            putString("last_walk_date", progress.lastWalkDate)
        }
    }

    fun loadProgressIfValid(): WalkProgress? {
        val lastDate = prefs.getString("last_walk_date", null) ?: return null
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return if (lastDate == today) {
            WalkProgress(
                currentSteps = prefs.getInt("current_steps", 0),
                targetSteps = prefs.getInt("target_steps", 0),
                currentDistance = prefs.getFloat("current_distance", 0f),
                targetDistance = prefs.getFloat("target_distance", 0f),
                lastWalkDate = lastDate
            )
        } else {
            clearProgress()
            null
        }
    }

    fun clearProgress() {
        prefs.edit {
            remove("current_steps")
            remove("target_steps")
            remove("current_distance")
            remove("target_distance")
            remove("last_walk_date")
        }
    }
}
