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

object IconSwitcherManager {

    fun switchToIcon(context: Context, targetIcon: NudgieIcon) {
        val packageManager = context.packageManager
        val packageName = context.packageName

        // 1. Check if the target is already enabled to avoid unnecessary restarts
        if (getCurrentIcon(context) == targetIcon) return

        // 2. IMPORTANT: Enable the NEW icon first!
        // This ensures the app always has at least one launcher icon active.
        packageManager.setComponentEnabledSetting(
            ComponentName(packageName, targetIcon.aliasClassName),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // 3. Disable all the OTHER icons safely afterwards.
        NudgieIcon.entries.forEach { icon ->
            if (icon != targetIcon) {
                packageManager.setComponentEnabledSetting(
                    ComponentName(packageName, icon.aliasClassName),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }

    fun getCurrentIcon(context: Context): NudgieIcon {
        val packageManager = context.packageManager
        val packageName = context.packageName

        return NudgieIcon.entries.find { icon ->
            val componentName = ComponentName(packageName, icon.aliasClassName)
            val state = packageManager.getComponentEnabledSetting(componentName)

            // MainActivityBlue is enabled by default in the Manifest.
            // Therefore, its state will be DEFAULT (0) unless explicitly changed.
            if (icon == NudgieIcon.BLUE) {
                state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED ||
                        state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
            } else {
                state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
        } ?: NudgieIcon.BLUE // Fallback
    }
}