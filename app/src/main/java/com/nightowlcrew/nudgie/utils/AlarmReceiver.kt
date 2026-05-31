package com.nightowlcrew.nudgie.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Teammate requirement: retrieve saved alarms from local database and reschedule them here
            Log.d("AlarmReceiver", "Device booted! Rescheduling alarms...")
        } else {
            // Trigger your actual alarm logic (notifications, waking device, etc.)
            Log.d("AlarmReceiver", "Alarm triggered exactly on time!")
        }
    }
}