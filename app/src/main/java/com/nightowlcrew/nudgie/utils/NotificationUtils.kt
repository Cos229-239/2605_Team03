package com.nightowlcrew.nudgie.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nightowlcrew.nudgie.R

object NotificationUtils {
    private const val CHANNEL_SCREEN_TIME = "screen_time_alerts"
    private const val CHANNEL_HABITS = "habit_reminders"
    private const val CHANNEL_GREETINGS = "daily_greetings"

    fun createChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val screenTimeChannel = NotificationChannel(
            CHANNEL_SCREEN_TIME, "Screen Time Alerts", NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Alerts for when you are nearing your screen time limit" }

        val habitChannel = NotificationChannel(
            CHANNEL_HABITS, "Habit Reminders", NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Reminders for habits like drinking water" }

        val greetingChannel = NotificationChannel(
            CHANNEL_GREETINGS, "Daily Greetings", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Morning summary of your tasks" }

        manager?.createNotificationChannels(listOf(screenTimeChannel, habitChannel, greetingChannel))
    }

    fun sendNotification(context: Context, title: String, message: String, channel: String, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, channel)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Fallback to launcher icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        manager.notify(notificationId, builder.build())
    }

    const val CH_SCREEN = CHANNEL_SCREEN_TIME
    const val CH_HABITS = CHANNEL_HABITS
    const val CH_GREET = CHANNEL_GREETINGS
}
