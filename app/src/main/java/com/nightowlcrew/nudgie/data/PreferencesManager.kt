package com.nightowlcrew.nudgie.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("nudgie_prefs", Context.MODE_PRIVATE)

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()

    var lastMorningGreetingDate: String
        get() = prefs.getString("last_morning_date", "") ?: ""
        set(value) = prefs.edit().putString("last_morning_date", value).apply()
    // ADDED: Track if the user has gone through the intake interview
    var isOnboardingComplete: Boolean
        get() = prefs.getBoolean("is_onboarding_complete", false)
        set(value) = prefs.edit().putBoolean("is_onboarding_complete", value).apply()
}
