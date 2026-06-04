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
        }
    }
}