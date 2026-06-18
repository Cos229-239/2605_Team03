package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HABIT_TEMPLATES
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.toEntity
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.nudgieCardShadow

@Composable
fun CategorizedHabitList(
    activities: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    currentTheme: AppTheme,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
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
        CozyCategory.values().forEach { category ->
            val sectionKey = "cat:${category.name}"
            val filteredActivities = activities.filter { 
                it.category == category.name 
            }
            val filteredArchived = archivedHabits.filter {
                it.category == category.name
            }
            ExpandableCategorySection(
                categoryTitle = category.displayName,
                habits = filteredActivities,
                archivedHabits = filteredArchived,
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
fun ExpandableCategorySection(
    categoryTitle: String,
    habits: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
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
        CommonManagementCard(
            currentTheme = currentTheme,
            modifier = Modifier.clickable { onToggleExpand() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                
                templates.forEach { template ->
                    // Extract title from template if it has an emoji prefix (matches ViewModel logic)
                    val emojiRegex = Regex("^(\\p{So}|\\p{Sk})\\s+(.*)$")
                    val matchResult = emojiRegex.find(template.title)
                    val templateTitleOnly = matchResult?.groupValues?.get(2) ?: template.title

                    val activeHabit = habits.find { it.originalTitle == templateTitleOnly || it.description == templateTitleOnly }
                    val archivedHabit = archivedHabits.find { it.title == templateTitleOnly }
                    
                    TemplateOptionRow(
                        title = template.title,
                        icon = if (activeHabit != null) Icons.Default.Delete else Icons.Default.Add,
                        currentTheme = currentTheme,
                        onClick = {
                            when {
                                activeHabit != null -> onArchiveHabit(activeHabit.toEntity())
                                archivedHabit != null -> onRestoreHabit(archivedHabit)
                                else -> onAddTemplate(template.title, template.defaultFrequency, true)
                            }
                        }
                    )
                }

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
    var isDropdownExpanded by remember { mutableStateOf(false) }

    CommonManagementCard(currentTheme = currentTheme) {
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
                            CozyCategory.values().forEach { category ->
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

@Composable
fun TemplateOptionRow(
    title: String,
    icon: ImageVector,
    currentTheme: AppTheme,
    onClick: () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
    CommonManagementCard(
        currentTheme = currentTheme,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                tint = if (icon == Icons.Default.Delete) Color.Red.copy(alpha = 0.7f) else contentColor
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
    CommonManagementCard(currentTheme = currentTheme) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
    CommonManagementCard(currentTheme = currentTheme) {
        Text(
            text = "Digital Balance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Slider(
            value = usageHours,
            onValueChange = onUsageChange,
            valueRange = 0f..12f,
            steps = 23 // 0.5 hour increments
        )
        
        val goalText = if (usageHours >= 12f) {
            "Goal: Unlimited today"
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

@Composable
fun CommonManagementCard(
    currentTheme: AppTheme,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .nudgieCardShadow(currentTheme, 4.dp, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = NavySurface
        ),
        border = BorderStroke(1.dp, NavyOutline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}
