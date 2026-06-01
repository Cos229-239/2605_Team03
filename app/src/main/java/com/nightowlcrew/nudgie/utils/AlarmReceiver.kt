package com.nightowlcrew.nudgie.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
                val label = intent.getStringExtra("EXTRA_LABEL") ?: "Pet Alert"
                val time = intent.getLongExtra("EXTRA_TIME", 0L)
                Log.d("AlarmReceiver", "Nudgie Alarm Triggered: $label at $time")
                
                // Trigger notification or UI update here
                
                // Remove the alarm from DB now that it has fired
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
