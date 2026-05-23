package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightowlcrew.nudgie.data.ActivityItem

/**
 * Stateful container for the Settings screen.
 * Collects state from NudgieViewModel and passes it to the stateless SettingsContent.
 */
@Composable
fun SettingsScreen(
    viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        activities = uiState.activities,
        screenTimeGoalMillis = uiState.screenTimeGoalMillis,
        onAddHabit = { title, category, frequency -> 
            viewModel.addNewHabit(title, category.name, frequency)
        },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onUpdateScreenTimeGoal = { hours -> viewModel.updateScreenTimeGoal(hours) }
    )
}

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

/**
 * Static catalog of default habits.
 */
val HABIT_TEMPLATES = mapOf(
    CozyCategory.BODY_VITALITY to listOf("💧 Drink 8 Cups of water", "💊 Take vitamins", "🧘 Morning stretch", "🏃 15-min walk"),
    CozyCategory.MIND_SPACE to listOf("✍️ Evening journaling", "🌬️ Breathing exercise", "🧹 Clear workspace", "📚 Read 5 pages"),
    CozyCategory.DAILY_RHYTHMS to listOf("🛏️ Make the bed", "🍽️ Wash dishes", "📧 Clear inbox", "📅 Review schedule"),
    CozyCategory.SELF_CARE_RITUALS to listOf("🧼 Skincare routine", "🪥 Brush teeth", "🚿 Warm shower", "🔌 Unplug 30m before sleep"),
    CozyCategory.CONNECTIONS to listOf("📱 Check-in text", "📞 Call family", "🐾 Feed pets", "🪴 Water plants")
)

@Composable
fun SettingsContent(
    activities: List<ActivityItem>,
    screenTimeGoalMillis: Long,
    onAddHabit: (String, CozyCategory, Int) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "MANAGEMENT CENTERS",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Active Habits",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        CategorizedHabitList(
            activities = activities,
            onAddHabit = onAddHabit,
            onDeleteHabit = onDeleteHabit,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 1000.dp)
        )

        HabitCreatorSection(onAddHabit = onAddHabit)

        val currentGoalHours = (screenTimeGoalMillis / 3600000L).toInt()

        DigitalBalanceCard(
            usageHours = currentGoalHours.toFloat(),
            onUsageChange = { newHours -> onUpdateScreenTimeGoal(newHours.toInt()) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCreatorSection(
    onAddHabit: (String, CozyCategory, Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("1") }
    var selectedCategory by rememberSaveable { mutableStateOf(CozyCategory.BODY_VITALITY) }
    var selectedEmoji by rememberSaveable { mutableStateOf("💧") }
    val emojis = listOf("💧", "💊", "🧘", "🪥", "☕", "🏃", "📚", "🧹")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add New Habit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(if (isExpanded) 45f else 0f)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            CozyCategory.entries.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.displayName) },
                                    onClick = {
                                        selectedCategory = category
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        text = "Pick an Emoji",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(emojis) { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { selectedEmoji = emoji },
                                shape = MaterialTheme.shapes.medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = emoji, style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Habit Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { if (it.all { char -> char.isDigit() }) frequency = it },
                        label = { Text("Daily Goal (e.g. 8 times)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                val f = frequency.toIntOrNull() ?: 1
                                onAddHabit("$selectedEmoji $title", selectedCategory, f)
                                title = ""
                                frequency = "1"
                                isExpanded = false
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Habit")
                    }
                }
            }
        }
    }
}

@Composable
fun CategorizedHabitList(
    activities: List<ActivityItem>,
    onAddHabit: (String, CozyCategory, Int) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CozyCategory.entries.forEach { category ->
            val filteredActivities = activities.filter { 
                it.icon == category.name 
            }
            ExpandableCategorySection(
                categoryTitle = category.displayName,
                habits = filteredActivities,
                onDeleteHabit = onDeleteHabit,
                category = category,
                onAddTemplate = { title, frequency -> onAddHabit(title, category, frequency) }
            )
        }
    }
}

@Composable
private fun ExpandableCategorySection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    onDeleteHabit: (Int) -> Unit,
    category: CozyCategory,
    onAddTemplate: (String, Int) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.medium
        ) {
                Row(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationState),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val templates = HABIT_TEMPLATES[category] ?: emptyList()
                
                // Show templates with +/- toggle
                templates.forEach { templateTitle ->
                    val activeHabit = habits.find { it.description == templateTitle }
                    val isActive = activeHabit != null
                    
                    TemplateOptionRow(
                        title = templateTitle,
                        icon = if (isActive) Icons.Default.Remove else Icons.Default.Add,
                        onClick = {
                            if (activeHabit != null) {
                                onDeleteHabit(activeHabit.id)
                            } else {
                                // Extract frequency from title if it contains "8 Cups" or similar
                                val freqMatch = Regex("\\d+").find(templateTitle)
                                val freq = freqMatch?.value?.toIntOrNull() ?: 1
                                onAddTemplate(templateTitle, freq)
                            }
                        }
                    )
                }

                // Show custom habits (not in templates)
                val customHabits = habits.filter { habit -> 
                    templates.none { it == habit.description } 
                }
                
                customHabits.forEach { item ->
                    ActivityRow(
                        item = item,
                        onDelete = { onDeleteHabit(item.id) }
                    )
                }

                if (habits.isEmpty() && customHabits.isEmpty() && templates.isEmpty()) {
                    Text(
                        text = "* No habits available *",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateOptionRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}

@Composable
fun ActivityRow(
    item: ActivityItem,
    onDelete: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from Dashboard",
                    tint = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DigitalBalanceCard(
    usageHours: Float,
    onUsageChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Digital Balance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Slider(
                value = usageHours,
                onValueChange = onUsageChange,
                valueRange = 0f..8f,
                steps = 15 // 0.5 hour increments
            )
            
            val goalText = if (usageHours >= 8f) {
                "Goal: 8+ hours today"
            } else {
                "Goal: Less than ${"%.1f".format(usageHours)} hours today"
            }

            Text(
                text = goalText,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Track and log your estimated daily screen usage manually.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsContentPreview() {
    val mockActivities = listOf(
        ActivityItem(1, CozyCategory.BODY_VITALITY.name, "Morning Yoga", "08:00", false),
        ActivityItem(2, CozyCategory.BODY_VITALITY.name, "Drink Water", "10:00", true),
        ActivityItem(3, CozyCategory.MIND_SPACE.name, "Meditation", "07:00", false),
        ActivityItem(4, CozyCategory.DAILY_RHYTHMS.name, "Bedtime Reading", "22:00", false),
    )

    MaterialTheme {
        Surface {
            SettingsContent(
                activities = mockActivities,
                screenTimeGoalMillis = 7200000L,
                onAddHabit = { _, _, _ -> },
                onDeleteHabit = { },
                onUpdateScreenTimeGoal = { }
            )
        }
    }
}
