package com.nightowlcrew.nudgie.utils

import com.nightowlcrew.nudgie.R

/**
 * Enum representing the available Nudgie pet types.
 */
enum class PetType(val displayName: String) {
    BLUE("Blue Trashpanda"),
    FOX("Fire Fox"),
    AXOLOTL("Pink Axolotl"),
    DRAGON("Space Dragon")
}

/**
 * Centralized manager for mapping [PetType] to drawable resources and launcher icons.
 */
object PetAssetManager {

    /**
     * Returns the drawable resource ID for the character based on [petType].
     */
    fun getPetDrawable(petType: PetType): Int {
        return when (petType) {
            PetType.BLUE -> R.drawable.blue_trashpanda
            PetType.FOX -> R.drawable.red_fox
            PetType.AXOLOTL -> R.drawable.oxylotyl
            PetType.DRAGON -> R.drawable.orange_blue_dragon_bgno
        }
    }

    /**
     * Returns the corresponding [NudgieIcon] for the given [petType] to sync the app icon.
     */
    fun getNudgieIcon(petType: PetType): NudgieIcon {
        return when (petType) {
            PetType.BLUE -> NudgieIcon.BLUE
            PetType.FOX -> NudgieIcon.FOX
            PetType.AXOLOTL -> NudgieIcon.AXOLOTL
            PetType.DRAGON -> NudgieIcon.DRAGON
        }
    }
}
