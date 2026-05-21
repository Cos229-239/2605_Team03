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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
        onAddHabit = { title, category -> 
            viewModel.addNewHabit(title, category.name, 1) 
        },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onToggleHabit = { item -> viewModel.toggleHabitCompletion(item) },
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
    CozyCategory.BODY_VITALITY to listOf("Drink 8oz water", "Take daily vitamins or medications", "Morning full-body stretch", "15-minute outdoor walk"),
    CozyCategory.MIND_SPACE to listOf("5-minute evening journaling", "Deep breathing exercise", "Clear off workspace desk", "Read 5 pages of a book"),
    CozyCategory.DAILY_RHYTHMS to listOf("Make the bed", "Wash the breakfast dishes", "Check and clear email inbox", "Review the daily study schedule"),
    CozyCategory.SELF_CARE_RITUALS to listOf("Evening skincare routine", "Brush and floss teeth", "Morning warm shower", "Unplug from electronics 30 mins before sleep"),
    CozyCategory.CONNECTIONS to listOf("Send a check-in text to a friend", "Call a family member", "Feed and water household pets", "Water the indoor plants")
)

@Composable
fun SettingsContent(
    activities: List<ActivityItem>,
    screenTimeGoalMillis: Long,
    onAddHabit: (String, CozyCategory) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onToggleHabit: (ActivityItem) -> Unit,
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
            onToggleHabit = onToggleHabit,
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
    onAddHabit: (String, CozyCategory) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(CozyCategory.BODY_VITALITY) }
    var selectedEmoji by rememberSaveable { mutableStateOf("💧") }
    val emojis = listOf("💧", "💊", "🧘", "🪥", "☕", "🏃", "📚", "🧹")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAddHabit("$selectedEmoji $title", selectedCategory)
                                title = ""
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
    onAddHabit: (String, CozyCategory) -> Unit,
    onToggleHabit: (ActivityItem) -> Unit,
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
                onToggleHabit = onToggleHabit,
                onDeleteHabit = onDeleteHabit,
                category = category,
                onAddTemplate = { title -> onAddHabit(title, category) }
            )
        }
    }
}

@Composable
private fun ExpandableCategorySection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    category: CozyCategory,
    onAddTemplate: (String) -> Unit
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
            color = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.rotate(rotationState),
                    tint = MaterialTheme.colorScheme.primary
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
                // Show currently active custom or template habits
                habits.forEach { item ->
                    val isDefaultHabit = HABIT_TEMPLATES[category]?.contains(item.description) == true
                    ActivityRow(
                        item = item,
                        onToggle = { onToggleHabit(item) },
                        onDelete = if (isDefaultHabit) null else { { onDeleteHabit(item.id) } }
                    )
                }

                // Show inactive templates as options
                val templates = HABIT_TEMPLATES[category] ?: emptyList()
                templates.forEach { templateTitle ->
                    val isAlreadyActive = habits.any { it.description == templateTitle }
                    if (!isAlreadyActive) {
                        TemplateOptionRow(
                            title = templateTitle,
                            onEnable = { onAddTemplate(templateTitle) }
                        )
                    }
                }

                if (habits.isEmpty() && templates.all { t -> habits.any { it.description == t } }) {
                    Text(
                        text = "* No custom habits added yet *",
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
    onEnable: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = false,
                onCheckedChange = { if (it) onEnable() }
            )
        }
    }
}

@Composable
fun ActivityRow(
    item: ActivityItem,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
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
                modifier = Modifier.weight(1f)
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Habit",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                Switch(
                    checked = item.isCompleted,
                    onCheckedChange = { onToggle() }
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
        modifier = Modifier.fillMaxWidth()
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
                onAddHabit = { _, _ -> },
                onDeleteHabit = { },
                onToggleHabit = { },
                onUpdateScreenTimeGoal = { }
            )
        }
    }
}
