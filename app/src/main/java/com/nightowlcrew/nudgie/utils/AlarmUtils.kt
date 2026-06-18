package com.nightowlcrew.nudgie.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.AlarmEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar // <-- Added the missing import for Calendar

/**
 * Utility object for scheduling and managing exact alarms.
 */
object AlarmUtils {

    /**
     * Schedules an exact alarm and persists it to the database for boot restoration.
     */
    fun setExactAlarm(context: Context, triggerAtMillis: Long, label: String = "Pet Alert") {
        if (!canScheduleExactAlarms(context)) {
            // Log or handle lack of permission (PermissionUtils handles the UI request)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_LABEL", label)
            putExtra("EXTRA_TIME", triggerAtMillis)
        }

        val uniqueRequestCode = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            uniqueRequestCode, // <--- Use the unique ID here
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

        // Persist to Room for boot persistence
        val database = (context.applicationContext as NudgieApplication).database
        CoroutineScope(Dispatchers.IO).launch {
            database.alarmDao().insertAlarm(AlarmEntity(triggerAtMillis = triggerAtMillis, label = label))
        }
    }

    /**
     * Cancels a scheduled alarm and removes it from the database.
     */
    fun cancelAlarm(context: Context, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            triggerAtMillis.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }

        // Remove from Room
        val database = (context.applicationContext as NudgieApplication).database
        CoroutineScope(Dispatchers.IO).launch {
            database.alarmDao().deleteAlarmByTime(triggerAtMillis)
        }
    }

    /**
     * Checks if the app has permission to schedule exact alarms (Required for SDK 31+).
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        } // <-- Fixed missing closing bracket
    }

    /**
     * Schedules a daily morning reminder at the specified hour and minute.
     * Kenneth's 'on_wake_up' implementation.
     */
    fun setWakeUpAlarm(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            // Using our existing receiver logic by just passing the label!
            putExtra("EXTRA_LABEL", "Good Morning! Check on your Nudgie ☀️")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001, // Unique ID so it doesn't overwrite your individual task alarms
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            // If the time has already passed today, set it for tomorrow morning
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // Sets it to fire every day at exactly that time
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /**
     * Schedules a repeating inexact alarm to remind the user to drink water.
     */
    fun setWaterReminders(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_LABEL", "Stay Hydrated! Time for a glass of water 💧")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1002, // Unique ID for water reminders
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set to trigger roughly every 2 hours
        val intervalMillis = AlarmManager.INTERVAL_HOUR * 2

        // InexactRepeating is vastly more battery-efficient for interval reminders
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )
    }
}