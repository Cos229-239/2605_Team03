package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HabitEntity
import com.nightowlcrew.nudgie.data.toActivityItem
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
    screenTimeGoalMillis: Long,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddHabit: (String, String, Int, Boolean) -> Unit,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
    initialOpenSlider: Boolean = false
) {
    val categoryTabs = CozyCategory.entries
    var selectedCategoryTab by remember { mutableStateOf(CozyCategory.BODY_VITALITY) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isSliderExpanded by remember { mutableStateOf(initialOpenSlider) }
    
    val completedCount = activities.count { it.isCompleted }
    val totalCount = activities.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground),
    ) {
        // ... (Header Box remains same)
        // Header Image and Progress Section (Sticky)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.task_header),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, NavyBackground.copy(alpha = 0.4f), NavyBackground),
                        ),
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "My Tasks",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🏆", fontSize = 20.sp)
                    Text(
                        text = "$completedCount / $totalCount Completed",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = SuccessGreen,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
            }
        }

        // Main Content Area (Tabs + List + Button)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            DigitalBalanceSliderCard(
                screenTimeGoalMillis = screenTimeGoalMillis,
                onUpdateScreenTimeGoal = onUpdateScreenTimeGoal,
                isExpanded = isSliderExpanded,
                onToggleExpand = { isSliderExpanded = !isSliderExpanded }
            )

            // 5 Tabs (Categories only)
            LazyRow(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryTabs) { category ->
                    CategoryChip(
                        label = category.displayName.split(" ").first(),
                        isSelected = selectedCategoryTab == category,
                        onClick = { selectedCategoryTab = category }
                    )
                }
            }

            // Scrollable Task List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredTasks = activities.filter { it.category == selectedCategoryTab.name }
                val filteredArchived = archivedHabits.filter { it.category == selectedCategoryTab.name }

                items(filteredTasks) { task ->
                    TaskListItem(
                        task = task,
                        onToggle = { onToggleHabit(task) },
                        onDelete = { onArchiveHabit(task.toEntity()) }
                    )
                }
                
                items(filteredArchived) { habit ->
                    TaskListItem(
                        task = habit.toActivityItem(null, 0),
                        isArchived = true,
                        onRestore = { onRestoreHabit(habit) }
                    )
                }
                
                // Extra padding at the bottom so list doesn't get hidden behind button
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
    
    // Persistent Add Task Button at the bottom
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("+ Add Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            initialCategory = selectedCategoryTab,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, frequency ->
                onAddHabit(title, category.name, frequency, false)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddTaskDialog(
    initialCategory: CozyCategory,
    onDismiss: () -> Unit,
    onConfirm: (String, CozyCategory, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("1") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var expanded by remember { mutableStateOf(false) }
    var selectedEmoji by remember { mutableStateOf("💧") }
    val emojis = listOf("💧", "💊", "🧘", "🪥", "☕", "🏃", "📚", "🧹", "📓", "🌬️", "🛏️", "📱", "🐾")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task", color = Color.White) },
        containerColor = NavySurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6200EE),
                        unfocusedBorderColor = NavyOutline
                    )
                )

                Text("Pick an Emoji", color = LavenderText, fontSize = 12.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(emojis) { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedEmoji = emoji },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF6200EE).copy(alpha = 0.3f) else NavyBackground,
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF6200EE) else NavyOutline)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Box {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                "Dropdown",
                                Modifier.clickable { expanded = true },
                                tint = Color.White
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6200EE),
                            unfocusedBorderColor = NavyOutline
                        )
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(NavySurface)
                    ) {
                        CozyCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName, color = Color.White) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = frequency,
                    onValueChange = { if (it.all { char -> char.isDigit() }) frequency = it },
                    label = { Text("Target Count (Times per day)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6200EE),
                        unfocusedBorderColor = NavyOutline
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm("$selectedEmoji $title", selectedCategory, frequency.toIntOrNull() ?: 1)
                    }
                }
            ) {
                Text("ADD", color = Color(0xFF6200EE), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = LavenderText)
            }
        }
    )
}

@Composable
fun DigitalBalanceSliderCard(
    screenTimeGoalMillis: Long,
    onUpdateScreenTimeGoal: (Int) -> Unit,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val goalHours = (screenTimeGoalMillis / 3600000f)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() },
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyOutline.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📱", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Digital Balance Goal",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Slider(
                        value = goalHours,
                        onValueChange = { onUpdateScreenTimeGoal(it.toInt()) },
                        valueRange = 1f..12f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6200EE),
                            activeTrackColor = Color(0xFF6200EE),
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    val displayGoal = if (goalHours >= 12f) "Unlimited ∞" else "${goalHours.toInt()} hours per day"
                    Text(
                        text = "Goal: $displayGoal",
                        color = LavenderText,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF2D2D3F) else Color.Transparent,
        border = if (isSelected) null else BorderStroke(1.dp, NavyOutline),
        modifier = Modifier.height(40.dp),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else LavenderText,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TaskListItem(
    task: ActivityItem,
    isArchived: Boolean = false,
    onToggle: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRestore: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (isArchived) 0.6f else 1f }
            .clickable {
                if (isArchived) onRestore?.invoke() else onToggle?.invoke()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyOutline.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon backdrop
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = NavyBackground.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = getIconForTask(task.description),
                        fontSize = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info row: Description + XP (XP on the right in mockup)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.description,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "20 XP",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (!isArchived) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Archive",
                    tint = Color.Red.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onDelete?.invoke() }
                )
                
                Spacer(modifier = Modifier.width(12.dp))

                // Circular Checkmark
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (task.isCompleted) SuccessGreen else Color.Transparent)
                        .border(2.dp, if (task.isCompleted) SuccessGreen else NavyOutline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Restore",
                    tint = SuccessGreen,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }
    }
}

fun getIconForTask(description: String): String {
    // If the description starts with an emoji, we use it.
    val emojiRegex = Regex("^(\\p{So}|\\p{Sk})")
    val match = emojiRegex.find(description)
    if (match != null) return match.value

    return when {
        description.contains("Journal", ignoreCase = true) -> "📔"
        description.contains("Read", ignoreCase = true) -> "📚"
        description.contains("Spanish", ignoreCase = true) -> "📖"
        description.contains("Math", ignoreCase = true) -> "🧮"
        description.contains("Workout", ignoreCase = true) -> "🏋️"
        description.contains("Water", ignoreCase = true) -> "💧"
        description.contains("Breathe", ignoreCase = true) -> "🌬️"
        else -> "📌"
    }
}

@Preview
@Composable
fun TasksContentPreview() {
    val mockTasks = listOf(
        ActivityItem(id = 1, icon = CozyCategory.MIND_SPACE.name, description = "Spanish Lesson", category = CozyCategory.MIND_SPACE.name, time = "10:00 AM", isCompleted = true),
        ActivityItem(id = 2, icon = CozyCategory.MIND_SPACE.name, description = "Math Practice", category = CozyCategory.MIND_SPACE.name, time = "11:00 AM", isCompleted = true),
        ActivityItem(id = 3, icon = CozyCategory.BODY_VITALITY.name, description = "Workout", category = CozyCategory.BODY_VITALITY.name, time = "5:00 PM", isCompleted = false),
        ActivityItem(id = 4, icon = CozyCategory.SELF_CARE_RITUALS.name, description = "Journal", category = CozyCategory.SELF_CARE_RITUALS.name, time = "9:00 PM", isCompleted = false)
    )
    TasksContent(
        activities = mockTasks,
        archivedHabits = emptyList(),
        screenTimeGoalMillis = 14400000L,
        onToggleHabit = {},
        onAddHabit = { _, _, _, _ -> },
        onUpdateScreenTimeGoal = {},
        onArchiveHabit = {},
        onRestoreHabit = {}
    )
}
