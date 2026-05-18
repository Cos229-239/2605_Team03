package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
        onAddHabit = { title, category -> 
            viewModel.addNewHabit(title, category.name, 1) 
        },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onToggleHabit = { item -> viewModel.toggleHabitCompletion(item) }
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

@Composable
fun SettingsContent(
    activities: List<ActivityItem>,
    onAddHabit: (String, CozyCategory) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onToggleHabit: (ActivityItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var screenTimeGoal by rememberSaveable { mutableFloatStateOf(2f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "MANAGEMENT CENTERS",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        HabitCreatorSection(onAddHabit = onAddHabit)

        DigitalBalanceCard(
            usageHours = screenTimeGoal,
            onUsageChange = { screenTimeGoal = it }
        )

        Text(
            text = "Active Inventory",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        CategorizedInventoryList(
            activities = activities,
            onToggleHabit = onToggleHabit,
            modifier = Modifier.weight(1f)
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
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
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
                                onAddHabit(title, selectedCategory)
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
fun CategorizedInventoryList(
    activities: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        CozyCategory.entries.forEach { category ->
            item {
                val filteredActivities = activities.filter { 
                    it.icon == category.name 
                }
                ExpandableCategorySection(
                    categoryTitle = category.displayName,
                    habits = filteredActivities,
                    onToggleHabit = onToggleHabit
                )
            }
        }
    }
}

@Composable
private fun ExpandableCategorySection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit
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
            color = MaterialTheme.colorScheme.surface,
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
                if (habits.isEmpty()) {
                    Text(
                        text = "* No habits added yet *",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                } else {
                    habits.forEach { item ->
                        ActivityRow(
                            item = item,
                            onToggle = { onToggleHabit(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityRow(
    item: ActivityItem,
    onToggle: () -> Unit
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
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = item.isCompleted,
                onCheckedChange = { onToggle() }
            )
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
                onAddHabit = { _, _ -> },
                onDeleteHabit = { },
                onToggleHabit = { }
            )
        }
    }
}
