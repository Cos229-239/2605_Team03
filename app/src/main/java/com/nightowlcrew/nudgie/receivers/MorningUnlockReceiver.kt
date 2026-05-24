package com.nightowlcrew.nudgie.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nightowlcrew.nudgie.workers.ScreenTimeSyncWorker
import com.nightowlcrew.nudgie.utils.NotificationUtils
import com.nightowlcrew.nudgie.data.PreferencesManager
import java.util.Calendar

/**
 * Receiver that handles device unlock and boot completed events.
 * Used to ensure background workers are scheduled and to provide morning nudges.
 */
class MorningUnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if ((intent.action == Intent.ACTION_USER_PRESENT) || (intent.action == Intent.ACTION_BOOT_COMPLETED)) {
            // Re-schedule or ensure the periodic screen time sync worker is running
            ScreenTimeSyncWorker.schedule(context)

            // Provide a morning nudge if the user unlocks their phone between 6 AM and 10 AM
            val hour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
            if (hour in 6..10) {
                val prefs = PreferencesManager(context)
                if (prefs.notificationsEnabled) {
                    NotificationUtils.sendNotification(
                        context,
                        "Good Morning!",
                        "Time to tackle your habits and stay within your screen time goals today.",
                        NotificationUtils.CH_SCREEN,
                        102,
                    )
                }
            }
        }
    }
}
