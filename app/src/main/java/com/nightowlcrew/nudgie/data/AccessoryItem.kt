package com.nightowlcrew.nudgie.data

enum class AccessoryCategory {
    CLOTHES,     // Combines hats, outfits, and apparel
    TOYS,
    FOOD,
    STAT_BOOST   // New category for temporary attribute updates
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