package com.nightowlcrew.nudgie

import android.app.Application
import com.nightowlcrew.nudgie.data.NudgieDatabase
import com.nightowlcrew.nudgie.workers.ScreenTimeSyncWorker

class NudgieApplication : Application() {
    val database: NudgieDatabase by lazy { NudgieDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        // Schedule the background screen time synchronization
        ScreenTimeSyncWorker.schedule(this)
    }
}
