package com.nightowlcrew.nudgie.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nightowlcrew.nudgie.NudgieApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver responsible for handling alarm triggers and system boot events.
 */
class AlarmReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("AlarmReceiver", "Device rebooted. Restoring Nudgie alarms...")
                restoreAlarmsOnBoot(context)
            }
            else -> {
                // 1. Team's logic: Extract the saved data
                val label = intent.getStringExtra("EXTRA_LABEL") ?: "Time to Level Up!"
                val time = intent.getLongExtra("EXTRA_TIME", 0L)
                Log.d("AlarmReceiver", "Nudgie Alarm Triggered: $label at $time")

                // 2. Your logic: Trigger the actual notification
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

                // Using the team's label variable for your notification title
                val builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setContentTitle(label)
                    .setContentText("Don't forget to complete your habit and earn XP for your Nudgie.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                val notificationId = System.currentTimeMillis().toInt()
                notificationManager.notify(notificationId, builder.build())

                // 3. Team's logic: Remove the alarm from the DB now that it has fired
                if (time != 0L) {
                    removeFiredAlarm(context, time)
                }
            }
        }
    }

    /**
     * Reschedules all valid future alarms stored in the Room database after a reboot.
     */
    private fun restoreAlarmsOnBoot(context: Context) {
        val pendingResult = goAsync()
        receiverScope.launch {
            try {
                val database = (context.applicationContext as NudgieApplication).database
                val currentTime = System.currentTimeMillis()

                // Cleanup old alarms that passed while device was off
                database.alarmDao().deleteOldAlarms(currentTime)

                val alarms = database.alarmDao().getAllAlarms()
                alarms.forEach { alarm ->
                    if (alarm.triggerAtMillis > currentTime) {
                        AlarmUtils.setExactAlarm(context, alarm.triggerAtMillis, alarm.label)
                        Log.d("AlarmReceiver", "Rescheduled alarm: ${alarm.label} for ${alarm.triggerAtMillis}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Failed to restore alarms", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * Cleans up the database after an alarm has been successfully delivered.
     */
    private fun removeFiredAlarm(context: Context, time: Long) {
        receiverScope.launch {
            val database = (context.applicationContext as NudgieApplication).database
            database.alarmDao().deleteAlarmByTime(time)
        }
    }
}