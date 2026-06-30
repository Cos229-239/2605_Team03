package com.nightowlcrew.nudgie.data

enum class AccessoryCategory {
    CLOTHES,     // Kept for backward compatibility
    HAT,
    GLASSES,
    OUTFIT,
    TOY,
    TOYS,        // Kept for backward compatibility
    FOOD,
    STAT_BOOST
}
data class AccessoryItem(
    val id: String,
    val name: String,
    val cost: Int,
    val iconResId: Int,
    val assetResId: Int,
    val category: AccessoryCategory,
    val description: String = "",
    val isPurchased: Boolean = false,
    val isEquipped: Boolean = false,
    val statEffect: String = "" // e.g., "ENERGY+50", "HAPPINESS+40"
)