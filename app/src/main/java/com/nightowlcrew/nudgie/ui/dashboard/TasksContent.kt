package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HABIT_TEMPLATES
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.HabitTemplate
import com.nightowlcrew.nudgie.data.toEntity
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.SuccessGreen

@Composable
fun TasksContent(
    activities: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, CozyCategory, Int) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
) {
    var selectedCategory by remember { mutableStateOf<CozyCategory?>(null) }
    var expandedCategoryId by remember { mutableStateOf<CozyCategory?>(null) }
    
    val filteredActivities = if (selectedCategory == null) {
        activities
    } else {
        activities.filter { it.icon == selectedCategory!!.name }
    }

    val completedCount = activities.count { it.isCompleted }
    val totalCount = activities.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
    ) {
        // Header Section
        Text(
            text = "My Tasks",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Progress Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.pethero_dashboard),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Progress",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedCount / $totalCount Completed",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = SuccessGreen,
                        trackColor = Color.White.copy(alpha = 0.3f),
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        // Category Filters
        LazyRow(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    label = "All",
                    isSelected = selectedCategory == null,
                    onClick = { selectedCategory = null }
                )
            }
            items(CozyCategory.entries) { category ->
                CategoryChip(
                    label = category.displayName.split(" ").first(),
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        // Task List with Management Behavior
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedCategory == null) {
                // Show categorized view with templates when "All" is selected (similar to old settings)
                items(CozyCategory.entries) { category ->
                    ExpandableTaskSection(
                        category = category,
                        habits = activities.filter { it.icon == category.name },
                        archivedHabits = archivedHabits.filter { it.icon == category.name },
                        isExpanded = expandedCategoryId == category,
                        onToggleExpand = {
                            expandedCategoryId = if (expandedCategoryId == category) null else category
                        },
                        onToggleHabit = onToggleHabit,
                        onAddHabit = onAddHabit,
                        onDeleteHabit = onDeleteHabit,
                        onArchiveHabit = onArchiveHabit,
                        onRestoreHabit = onRestoreHabit
                    )
                }
            } else {
                // Show flat list for specific category
                items(filteredActivities) { task ->
                    TaskListItem(
                        task = task,
                        onToggle = { onToggleHabit(task) },
                        onDelete = { onDeleteHabit(task.id) }
                    )
                }
                
                // Also show templates for this category at the bottom
                item {
                    Text(
                        "Add Suggested Tasks",
                        color = LavenderText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                
                val templates = HABIT_TEMPLATES[selectedCategory!!] ?: emptyList()
                items(templates) { template ->
                    val activeHabit = activities.find { (it.originalTitle == template.title) && (it.icon == selectedCategory!!.name) }
                    val archivedHabit = archivedHabits.find { (it.title == template.title) && (it.icon == selectedCategory!!.name) }

                    StockTemplateItem(
                        template = template,
                        activeHabit = activeHabit,
                        archivedHabit = archivedHabit,
                        onAdd = { onAddHabit(template.title, selectedCategory!!, template.defaultFrequency) },
                        onArchive = { activeHabit?.let { onArchiveHabit(it.toEntity()) } },
                        onRestore = { archivedHabit?.let { onRestoreHabit(it) } }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableTaskSection(
    category: CozyCategory,
    habits: List<ActivityItem>,
    archivedHabits: List<HabitEntity>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, CozyCategory, Int) -> Unit,
    onDeleteHabit: (Int) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
) {
    val rotationState by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "RotationAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NavyOutline)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category.displayName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = LavenderText,
                    modifier = Modifier.rotate(rotationState)
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active Habits in this category
                habits.forEach { habit ->
                    TaskListItem(
                        task = habit,
                        onToggle = { onToggleHabit(habit) },
                        onDelete = { onDeleteHabit(habit.id) }
                    )
                }

                // Templates for this category
                val templates = HABIT_TEMPLATES[category] ?: emptyList()
                templates.forEach { template ->
                    val activeHabit = habits.find { it.originalTitle == template.title }
                    val archivedHabit = archivedHabits.find { it.title == template.title }
                    
                    StockTemplateItem(
                        template = template,
                        activeHabit = activeHabit,
                        archivedHabit = archivedHabit,
                        onAdd = { onAddHabit(template.title, category, template.defaultFrequency) },
                        onArchive = { activeHabit?.let { onArchiveHabit(it.toEntity()) } },
                        onRestore = { archivedHabit?.let { onRestoreHabit(it) } }
                    )
                }
            }
        }
    }
}

@Composable
fun StockTemplateItem(
    template: HabitTemplate,
    activeHabit: ActivityItem?,
    archivedHabit: HabitEntity?,
    onAdd: () -> Unit,
    onArchive: () -> Unit,
    onRestore: () -> Unit
) {
    val isActive = activeHabit != null
    val isArchived = archivedHabit != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                when {
                    isActive -> onArchive()
                    isArchived -> onRestore()
                    else -> onAdd()
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) NavySurface else NavySurface.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isActive) NavyOutline else NavyOutline.copy(alpha = 0.5f)
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
                text = template.title,
                color = if (isActive) Color.White else LavenderText,
                fontSize = 16.sp
            )
            Icon(
                imageVector = if (isActive) Icons.Default.Delete else Icons.Default.Add,
                contentDescription = if (isActive) "Archive Task" else "Add Task",
                tint = if (isActive) Color.Red.copy(alpha = 0.7f) else LavenderText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF2D2D3F) else Color.Transparent,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, NavyOutline)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else LavenderText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TaskListItem(task: ActivityItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyOutline)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task Checkbox (Clickable area separate or the whole row?)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) SuccessGreen else Color.Transparent)
                    .border(2.dp, if (task.isCompleted) SuccessGreen else NavyOutline, CircleShape)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Task Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "20 XP",
                    color = LavenderText,
                    fontSize = 12.sp
                )
            }

            // Delete Button (New Behavior from Settings)
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(0.dp),
    color: Color = MaterialTheme.colorScheme.surface,
    border: androidx.compose.foundation.BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .then(if (border != null) Modifier.border(border, shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview
@Composable
fun TasksContentPreview() {
    val mockTasks = listOf(
        ActivityItem(id = 1, icon = CozyCategory.BODY_VITALITY.name, description = "Spanish Lesson", time = "10:00 AM", isCompleted = true, originalTitle = "Spanish Lesson"),
        ActivityItem(id = 2, icon = CozyCategory.MIND_SPACE.name, description = "Math Practice", time = "11:00 AM", isCompleted = true, originalTitle = "Math Practice"),
        ActivityItem(id = 3, icon = CozyCategory.BODY_VITALITY.name, description = "Workout", time = "5:00 PM", isCompleted = false, originalTitle = "Workout"),
        ActivityItem(id = 4, icon = CozyCategory.MIND_SPACE.name, description = "Journal", time = "9:00 PM", isCompleted = false, originalTitle = "Journal")
    )
    TasksContent(
        activities = mockTasks,
        archivedHabits = emptyList(),
        onToggleHabit = {},
        onAddHabit = { _, _, _ -> },
        onDeleteHabit = {},
        onArchiveHabit = {},
        onRestoreHabit = {}
    )
}
