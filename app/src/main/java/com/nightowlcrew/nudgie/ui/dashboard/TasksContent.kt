package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.PressStart2P

/**
 * Redesigned TasksContent acting as the "MANAGEMENT CENTER".
 * Contains habit lists, habit creation, and digital wellbeing settings.
 */
@Composable
fun TasksContent(
    activities: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    screenTimeGoalMillis: Long,
    currentTheme: AppTheme,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    onDeleteHabit: (String) -> Unit,
    initialOpenSlider: Boolean = false // Maintained for nav compatibility
) {
    var expandedSection by rememberSaveable { mutableStateOf<String?>(if (initialOpenSlider) "balance" else null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "MANAGEMENT CENTER",
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PressStart2P),
            color = LavenderText,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Active Habits",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = PressStart2P),
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        CategorizedHabitList(
            activities = activities,
            archivedHabits = archivedHabits,
            currentTheme = currentTheme,
            onAddHabit = onAddHabit,
            onArchiveHabit = onArchiveHabit,
            onRestoreHabit = onRestoreHabit,
            expandedSection = expandedSection,
            onSectionToggle = { expandedSection = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 1000.dp)
        )

        HabitCreatorSection(
            onAddHabit = { title, selectedCategory, frequency -> 
                onAddHabit(title, selectedCategory.name, frequency, false) 
            },
            archivedHabits = archivedHabits,
            onRestoreHabit = onRestoreHabit,
            currentTheme = currentTheme,
            isExpanded = expandedSection == "creator",
            onToggleExpand = {
                expandedSection = if (expandedSection == "creator") null else "creator"
            }
        )

        val currentGoalHours = (screenTimeGoalMillis / 3600000L).toInt()

        DigitalBalanceCard(
            usageHours = currentGoalHours.toFloat(),
            currentTheme = currentTheme,
            onUsageChange = { newHours: Float -> onUpdateScreenTimeGoal(newHours.toInt()) },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TasksContentPreview() {
    val mockActivities = listOf(
        ActivityItem("1", "💪", "Morning Yoga", CozyCategory.BODY_VITALITY.name, "08:00", false),
        ActivityItem("2", "💧", "Drink Water", CozyCategory.BODY_VITALITY.name, "10:00", true),
    )

    MaterialTheme {
        Surface {
            TasksContent(
                activities = mockActivities,
                archivedHabits = emptyList<HabitEntity>(),
                screenTimeGoalMillis = 7200000L,
                currentTheme = AppTheme.DEFAULT,
                onToggleHabit = { },
                onAddHabit = { _, _, _, _ -> },
                onUpdateScreenTimeGoal = { },
                onArchiveHabit = { },
                onRestoreHabit = { },
                onDeleteHabit = { }
            )
        }
    }
}