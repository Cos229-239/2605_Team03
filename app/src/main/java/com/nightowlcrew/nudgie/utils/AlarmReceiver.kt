package com.nightowlcrew.nudgie.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Teammate requirement: retrieve saved alarms from local database and reschedule them here
            Log.d("AlarmReceiver", "Device booted! Rescheduling alarms...")
        } else {
            // Trigger your actual alarm logic (notifications, waking device, etc.)
            Log.d("AlarmReceiver", "Alarm triggered exactly on time!")


            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "nudgie_reminders"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Habit Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders to complete your habits"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder) // Default Android icon for now
                .setContentTitle("Time to Level Up!")
                .setContentText("Don't forget to complete your habit and earn XP for your Nudgie.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)


            val notificationId = System.currentTimeMillis().toInt()
            notificationManager.notify(notificationId, builder.build())
        }
    }
}