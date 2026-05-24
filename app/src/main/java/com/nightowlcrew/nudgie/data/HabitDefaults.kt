package com.nightowlcrew.nudgie.data

/**
 * The 5 cozy life-balance categories.
 */
enum class CozyCategory(val displayName: String) {
    BODY_VITALITY("Body & Vitality"),
    MIND_SPACE("Mind & Space"),
    DAILY_RHYTHMS("Daily Rhythms"),
    SELF_CARE_RITUALS("Self-Care Rituals"),
    CONNECTIONS("Connections")
}

data class HabitTemplate(
    val title: String,
    val defaultFrequency: Int = 1
)

/**
 * Static catalog of default habits.
 */
val HABIT_TEMPLATES = mapOf(
    CozyCategory.BODY_VITALITY to listOf(
        HabitTemplate("💧 Drink 8 Cups of water", 8),
        HabitTemplate("💊 Take vitamins", 1),
        HabitTemplate("🧘 Morning stretch", 1),
        HabitTemplate("🏃 15-min walk", 1)
    ),
    CozyCategory.MIND_SPACE to listOf(
        HabitTemplate("✍️ Evening journaling", 1),
        HabitTemplate("🌬️ Breathing exercise", 1),
        HabitTemplate("🧹 Clear workspace", 1),
        HabitTemplate("📚 Read 5 pages", 1)
    ),
    CozyCategory.DAILY_RHYTHMS to listOf(
        HabitTemplate("🛏️ Make the bed", 1),
        HabitTemplate("🍽️ Wash dishes", 1),
        HabitTemplate("📧 Clear inbox", 1),
        HabitTemplate("📅 Review schedule", 1)
    ),
    CozyCategory.SELF_CARE_RITUALS to listOf(
        HabitTemplate("🧼 Skincare routine", 1),
        HabitTemplate("🪥 Brush teeth", 2),
        HabitTemplate("🚿 Warm shower", 1),
        HabitTemplate("🔌 Unplug 30m before sleep", 1)
    ),
    CozyCategory.CONNECTIONS to listOf(
        HabitTemplate("📱 Check-in text", 1),
        HabitTemplate("📞 Call family", 1),
        HabitTemplate("🐾 Feed pets", 2),
        HabitTemplate("🪴 Water plants", 1)
    )
)
