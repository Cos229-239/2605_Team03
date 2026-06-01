package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.R
import com.nightowlcrew.nudgie.data.ActivityItem
import com.nightowlcrew.nudgie.data.CozyCategory
import com.nightowlcrew.nudgie.data.HabitEntity
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
    onAddHabit: (String, CozyCategory, Int, Boolean) -> Unit,
    onArchiveHabit: (HabitEntity) -> Unit,
    onRestoreHabit: (HabitEntity) -> Unit,
) {
    // Categories based on CozyCategory + "All"
    val categoryTabs = listOf("All") + CozyCategory.entries.map { it.displayName.split(" ").first() }
    var selectedCategoryTab by remember { mutableStateOf("All") }
    
    val completedCount = activities.count { it.isCompleted }
    val totalCount = activities.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
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
                            colors = listOf(Color.Transparent, NavyBackground.copy(alpha = 0.4f), NavyBackground)
                        )
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
                        fontWeight = FontWeight.Medium
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
            // 5 Tabs (All + 4 Categories)
            LazyRow(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryTabs) { tab ->
                    CategoryChip(
                        label = tab,
                        isSelected = selectedCategoryTab == tab,
                        onClick = { selectedCategoryTab = tab }
                    )
                }
            }

            // Scrollable Task List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val filteredTasks = if (selectedCategoryTab == "All") {
                    activities
                } else {
                    activities.filter { it.icon == selectedCategoryTab || it.description.contains(selectedCategoryTab, ignoreCase = true) }
                }

                items(filteredTasks) { task ->
                    TaskListItem(
                        task = task,
                        onToggle = { onToggleHabit(task) }
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
            onClick = { /* TODO */ },
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
}

@Composable
fun CategoryChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF2D2D3F) else Color.Transparent,
        border = if (isSelected) null else BorderStroke(1.dp, NavyOutline),
        modifier = Modifier.height(40.dp)
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
fun TaskListItem(task: ActivityItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyOutline.copy(alpha = 0.5f))
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

            // Circular Checkmark
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
        }
    }
}

fun getIconForTask(description: String): String {
    return when {
        description.contains("Spanish", ignoreCase = true) -> "📖"
        description.contains("Math", ignoreCase = true) -> "🧮"
        description.contains("Workout", ignoreCase = true) -> "🏋️"
        description.contains("Journal", ignoreCase = true) -> "📓"
        else -> "📌"
    }
}

@Preview
@Composable
fun TasksContentPreview() {
    val mockTasks = listOf(
        ActivityItem(id = 1, icon = "📖", description = "Spanish Lesson", time = "10:00 AM", isCompleted = true),
        ActivityItem(id = 2, icon = "🧮", description = "Math Practice", time = "11:00 AM", isCompleted = true),
        ActivityItem(id = 3, icon = "🏋️", description = "Workout", time = "5:00 PM", isCompleted = false),
        ActivityItem(id = 4, icon = "📓", description = "Journal", time = "9:00 PM", isCompleted = false)
    )
    TasksContent(
        activities = mockTasks,
        archivedHabits = emptyList(),
        onToggleHabit = {},
        onAddHabit = { _, _, _, _ -> },
        onArchiveHabit = {},
        onRestoreHabit = {}
    )
}
