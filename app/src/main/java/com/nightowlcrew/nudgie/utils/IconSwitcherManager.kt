package com.nightowlcrew.nudgie.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Enum representing the available launcher icons linked to activity-aliases in AndroidManifest.xml.
 */
enum class NudgieIcon(val aliasClassName: String) {
    BLUE("com.nightowlcrew.nudgie.MainActivityBlue"),
    FOX("com.nightowlcrew.nudgie.MainActivityFox"),
    AXOLOTL("com.nightowlcrew.nudgie.MainActivityAxolotl"),
    DRAGON("com.nightowlcrew.nudgie.MainActivityDragon")
}

/**
 * Architected manager to handle programmatic switching of the application's launcher icon.
 *
 * This implementation uses [PackageManager.setComponentEnabledSetting] to toggle the visibility
 * of <activity-alias> components defined in the manifest.
 *
 * IMPORTANT: Enabling/Disabling the active LAUNCHER component typically triggers an application
 * process restart by the Android system to refresh the launcher cache. Users may experience
 * a brief "app closed" behavior.
 */
object IconSwitcherManager {

    /**
     * Switches the app icon to the specified [targetIcon].
     * Disables all other icons to ensure only one launcher entry exists.
     *
     * @param context The application or activity context.
     * @param targetIcon The [NudgieIcon] to enable.
     */
    fun switchToIcon(context: Context, targetIcon: NudgieIcon) {
        val packageManager = context.packageManager
        val packageName = context.packageName

        NudgieIcon.entries.forEach { icon ->
            val componentName = ComponentName(packageName, icon.aliasClassName)
            val state = if (icon == targetIcon) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            // We use DONT_KILL_APP to minimize disruption, but be aware that the system
            // might still kill the process if the active launcher component is disabled.
            packageManager.setComponentEnabledSetting(
                componentName,
                state,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    /**
     * Returns the currently enabled [NudgieIcon] by checking component states.
     */
    fun getCurrentIcon(context: Context): NudgieIcon? {
        val packageManager = context.packageManager
        val packageName = context.packageName

        return NudgieIcon.entries.find { icon ->
            val componentName = ComponentName(packageName, icon.aliasClassName)
            val state = packageManager.getComponentEnabledSetting(componentName)
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } ?: NudgieIcon.BLUE // Default to BLUE if none are explicitly enabled (initial state)
    }
}
