package com.nightowlcrew.nudgie.data

// Make sure your enum looks like this now:
enum class AccessoryCategory {
    HAT,
    GLASSES,
    OUTFIT,
    TOY,
    FOOD
}

data class AccessoryItem(
    val id: String,
    val name: String,
    val cost: Int,
    val assetResId: Int, // The actual image on the pet
    val iconResId: Int,  // The image shown in the shop menu
    val category: AccessoryCategory,
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false
)