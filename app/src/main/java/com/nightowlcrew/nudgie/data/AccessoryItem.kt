package com.nightowlcrew.nudgie.data

data class AccessoryItem(
    val id: String,
    val name: String,
    val cost: Int,
    val iconResId: Int, // The icon to display in the shop
    val assetResId: Int, // The actual transparent PNG that goes on the pet
    val category: AccessoryCategory,
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false
)

enum class AccessoryCategory {
    HAT, GLASSES, OUTFIT, BACKGROUND
}