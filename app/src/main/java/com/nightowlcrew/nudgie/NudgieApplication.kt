package com.nightowlcrew.nudgie

import android.app.Application
import com.nightowlcrew.nudgie.data.NudgieDatabase

class NudgieApplication : Application() {
    val database: NudgieDatabase by lazy { NudgieDatabase.getDatabase(this) }
}
