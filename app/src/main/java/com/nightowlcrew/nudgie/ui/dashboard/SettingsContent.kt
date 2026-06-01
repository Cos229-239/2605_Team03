package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HABIT_TEMPLATES
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.toEntity
import com.nightowlcrew.nudgie.ui.theme.PressStart2P
import com.nightowlcrew.nudgie.ui.theme.nudgieCardShadow

/**
 * Stateful container for the Settings screen.
 * Collects state from NudgieViewModel and passes it to the stateless SettingsContent.
 */
@Composable
fun SettingsScreen(
    viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val archivedHabits by viewModel.archivedHabits.collectAsStateWithLifecycle()

    SettingsContent(
        activities = uiState.activities,
        archivedHabits = archivedHabits,
        screenTimeGoalMillis = uiState.screenTimeGoalMillis,
        currentTheme = uiState.currentTheme,
        onAddHabit = { title, category, frequency, isStock -> 
            viewModel.addNewHabit(title, category, frequency, isStock)
        },
        onDeleteHabit = { id -> viewModel.deleteHabit(id) },
        onUpdateScreenTimeGoal = { hours -> viewModel.updateScreenTimeGoal(hours) },
        onUpdateTheme = { theme -> viewModel.updateTheme(theme) },
        onArchiveHabit = { viewModel.archiveHabit(it) },
        onRestoreHabit = { viewModel.restoreHabit(it) },
    )
}

@Composable
fun SettingsContent(
    activities: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    screenTimeGoalMillis: Long,
    currentTheme: AppTheme,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSection by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "MANAGEMENT CENTERS",
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PressStart2P),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Active Habits",
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = PressStart2P),
            fontWeight = FontWeight.SemiBold
        )

        CategorizedHabitList(
            activities = activities,
            archivedHabits = archivedHabits,
            currentTheme = currentTheme,
            onAddHabit = onAddHabit,
            onDeleteHabit = onDeleteHabit,
            onArchiveHabit = onArchiveHabit,
            onRestoreHabit = onRestoreHabit,
            expandedSection = expandedSection,
            onSectionToggle = { expandedSection = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 1000.dp)
        )

        HabitCreatorSection(
            onAddHabit = { title, category, frequency -> onAddHabit(title, category.name, frequency, false) },
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
            onUsageChange = { newHours -> onUpdateScreenTimeGoal(newHours.toInt()) },
        )

        ThemeSelectionCard(
            currentTheme = currentTheme,
            onUpdateTheme = onUpdateTheme,
            isExpanded = expandedSection == "theme",
            onToggleExpand = {
                expandedSection = if (expandedSection == "theme") null else "theme"
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelectionCard(
    currentTheme: AppTheme,
    onUpdateTheme: (AppTheme) -> Unit,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "App Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { onToggleExpand() },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentTheme.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Theme") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { onToggleExpand() },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    AppTheme.entries.forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(theme.name) },
                            onClick = {
                                onUpdateTheme(theme)
                                onToggleExpand()
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCreatorSection(
    onAddHabit: (String, CozyCategory, Int) -> Unit,
    archivedHabits: List<HabitEntity>,
    onRestoreHabit: (HabitEntity) -> Unit,
    currentTheme: AppTheme,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("1") }
    var selectedCategory by rememberSaveable { mutableStateOf(CozyCategory.BODY_VITALITY) }
    var selectedEmoji by rememberSaveable { mutableStateOf("💧") }
    val emojis = listOf("💧", "💊", "🧘", "🪥", "☕", "🏃", "📚", "🧹")
    var isDropdownExpanded by remember { mutableStateOf(value = false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium),
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
                    .clickable { onToggleExpand() },
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
                                    .nudgieCardShadow(currentTheme, 2.dp, MaterialTheme.shapes.medium)
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
                                onToggleExpand()
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save Habit")
                    }

                    if (archivedHabits.isNotEmpty()) {
                        val customArchived = archivedHabits.filter { !it.isStock }
                        if (customArchived.isNotEmpty()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = "Recently Deleted Custom Habits",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                customArchived.forEach { habit ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = habit.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(
                                                onClick = { onRestoreHabit(habit) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Restore Habit",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategorizedHabitList(
    activities: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    currentTheme: AppTheme,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    expandedSection: String?,
    onSectionToggle: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CozyCategory.entries.forEach { category ->
            val sectionKey = "cat:${category.name}"
            val filteredActivities = activities.filter { 
                it.icon == category.name 
            }
            val filteredArchived = archivedHabits.filter {
                it.icon == category.name
            }
            ExpandableCategorySection(
                categoryTitle = category.displayName,
                habits = filteredActivities,
                archivedHabits = filteredArchived,
                onDeleteHabit = onDeleteHabit,
                onArchiveHabit = onArchiveHabit,
                onRestoreHabit = onRestoreHabit,
                category = category,
                currentTheme = currentTheme,
                expanded = expandedSection == sectionKey,
                onToggleExpand = {
                    onSectionToggle(if (expandedSection == sectionKey) null else sectionKey)
                },
                onAddTemplate = { title, frequency, isStock -> onAddHabit(title, category.name, frequency, isStock) }
            )
        }
    }
}

@Composable
private fun ExpandableCategorySection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    onDeleteHabit: (Int) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    category: CozyCategory,
    currentTheme: AppTheme,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onAddTemplate: (String, Int, Boolean) -> Unit
) {
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium)
                    .clickable { onToggleExpand() },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = categoryTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        modifier = Modifier.rotate(rotationState),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                templates.forEach { template ->
                    val activeHabit = habits.find { it.description == template.title }
                    val archivedHabit = archivedHabits.find { it.title == template.title }
                    val isActive = activeHabit != null
                    val isArchived = archivedHabit != null
                    
                    TemplateOptionRow(
                        title = template.title,
                        icon = if (isActive) Icons.Default.Delete else Icons.Default.Add,
                        currentTheme = currentTheme,
                        onClick = {
                    when {
                        isActive -> onArchiveHabit(activeHabit.toEntity())
                        isArchived -> onRestoreHabit(archivedHabit)
                        else -> onAddTemplate(template.title, template.defaultFrequency, true)
                    }
                }
            )
        }

                // Show custom habits (not in templates)
                val customHabits = habits.filter { habit -> 
                    templates.none { it.title == habit.description }
                }
                
                customHabits.forEach { item ->
                    ActivityRow(
                        item = item,
                        currentTheme = currentTheme,
                        onDelete = { onArchiveHabit(item.toEntity()) }
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
    currentTheme: AppTheme,
    onClick: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium)
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
                tint = if (icon == Icons.Default.Delete) androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.7f) else contentColor
            )
        }
    }
}

@Composable
fun ActivityRow(
    item: ActivityItem,
    currentTheme: AppTheme,
    onDelete: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium),
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
    currentTheme: AppTheme,
    onUsageChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium),
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
        ActivityItem(1, CozyCategory.BODY_VITALITY.name, "Morning Yoga", CozyCategory.BODY_VITALITY.name, "08:00", false),
        ActivityItem(2, CozyCategory.BODY_VITALITY.name, "Drink Water", CozyCategory.BODY_VITALITY.name, "10:00", true),
        ActivityItem(3, CozyCategory.MIND_SPACE.name, "Meditation", CozyCategory.MIND_SPACE.name, "07:00", false),
        ActivityItem(4, CozyCategory.DAILY_RHYTHMS.name, "Bedtime Reading", CozyCategory.DAILY_RHYTHMS.name, "22:00", false),
    )

    MaterialTheme {
        Surface {
            SettingsContent(
                activities = mockActivities,
                archivedHabits = emptyList(),
                screenTimeGoalMillis = 7200000L,
                currentTheme = AppTheme.DEFAULT,
                onAddHabit = { _, _, _, _ -> },
                onDeleteHabit = { },
                onUpdateScreenTimeGoal = { },
                onUpdateTheme = { },
                onArchiveHabit = { },
                onRestoreHabit = { }
            )
        }
    }
}
