package com.nightowlcrew.nudgie.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

/**
 * Enum representing the available launcher icons linked to activity-aliases in AndroidManifest.xml.
 */
enum class NudgieIcon(val aliasClassName: String) {
    BLUE("com.nightowlcrew.nudgie.MainActivity"),
    FOX("com.nightowlcrew.nudgie.MainActivityFox"),
    AXOLOTL("com.nightowlcrew.nudgie.MainActivityAxolotl"),
    DRAGON("com.nightowlcrew.nudgie.MainActivityDragon")
}

object IconSwitcherManager {

    fun switchToIcon(context: Context, targetIcon: NudgieIcon) {
        val packageManager = context.packageManager
        val packageName = context.packageName

        if (getCurrentIcon(context) == targetIcon) return

        // 1. If target is NOT Blue, enable it. (Blue/MainActivity is always enabled)
        if (targetIcon != NudgieIcon.BLUE) {
            packageManager.setComponentEnabledSetting(
                ComponentName(packageName, targetIcon.aliasClassName),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        // 2. Disable all other aliases. 
        // Note: We never disable MainActivity (BLUE) because it's the IDE's stable launch point
        // and the target for all aliases.
        NudgieIcon.entries.forEach { icon ->
            if (icon != targetIcon && icon != NudgieIcon.BLUE) {
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

        // Check aliases first
        val activeAlias = NudgieIcon.entries.filter { it != NudgieIcon.BLUE }.find { icon ->
            val state = packageManager.getComponentEnabledSetting(ComponentName(packageName, icon.aliasClassName))
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }

        return activeAlias ?: NudgieIcon.BLUE
    }
}