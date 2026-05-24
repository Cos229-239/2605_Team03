package com.nightowlcrew.nudgie.workers

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.work.*
import com.nightowlcrew.nudgie.NudgieApplication
import com.nightowlcrew.nudgie.data.HabitRepositoryImpl
import com.nightowlcrew.nudgie.data.ScreenTimeRecord
import com.nightowlcrew.nudgie.data.PreferencesManager
import com.nightowlcrew.nudgie.utils.NotificationUtils
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Background worker that captures device screen time from the OS UsageStatsManager
 * and synchronizes it with the local Room database.
 */
class ScreenTimeSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext as NudgieApplication
        val repository = HabitRepositoryImpl(
            appContext.database.habitDao(),
            appContext.database.screenTimeDao()
        )

        // 1. Get total screen time for today (midnight to now) from System API
        val totalUsageMillis = getTodayTotalUsageMillis()

// 2. Fetch or create today's record in Room
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existingRecord = repository.getScreenTimeForDate(today).firstOrNull()

        // --- NEW LOGIC FOR NOTIFICATION ---
        val prefs = PreferencesManager(applicationContext)
        var hasWarned = existingRecord?.warningNotified ?: false

        val limit = existingRecord?.targetLimitMillis ?: 14400000L
        val remainingMillis = limit - totalUsageMillis

        if (prefs.notificationsEnabled && !hasWarned && remainingMillis in 1..1800000L) {
            NotificationUtils.sendNotification(
                applicationContext,
                "Screen Time Warning",
                "You only have 30 minutes of screen time left today!",
                NotificationUtils.CH_SCREEN,
                101
            )
            hasWarned = true
        }
        // ----------------------------------

        val updatedRecord = if (existingRecord != null) {
            existingRecord.copy(actualDurationMillis = totalUsageMillis, warningNotified = hasWarned)
        } else {
            ScreenTimeRecord(
                date = today,
                targetLimitMillis = 14400000L,
                actualDurationMillis = totalUsageMillis,
                warningNotified = hasWarned
            )
        }

        // 3. Save to database
        repository.insertOrUpdateScreenTime(updatedRecord)

        return Result.success()
    }

    private fun getTodayTotalUsageMillis(): Long {
        val usageStatsManager = applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        
        val calendar = Calendar.getInstance()
        val end = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        val stats = usageStatsManager.queryAndAggregateUsageStats(start, end)
        
        // Sum totalTimeInForeground for all apps
        return stats.values.sumOf { it.totalTimeInForeground }
    }

    companion object {
        private const val WORK_NAME = "ScreenTimeSyncWorker"

        /**
         * Schedules a periodic work request to sync screen time every 15 minutes.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<ScreenTimeSyncWorker>(
                15, TimeUnit.MINUTES
            )
            .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
    }
}
