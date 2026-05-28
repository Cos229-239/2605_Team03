package com.nightowlcrew.nudgie.ui.dashboard

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
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.SuccessGreen

@Composable
fun TasksContent(
    activities: List<ActivityItem>,
    onToggleHabit: (ActivityItem) -> Unit,
    onAddTask: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf<CozyCategory?>(null) }
    
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
                    painter = painterResource(id = R.drawable.pethero_dashboard), // Reusing dashboard bg for landscape feel
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
                            text = "y...", // Placeholder for icon/text in mockup
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
                    label = category.displayName.split(" ").first(), // Short name for chip
                    isSelected = selectedCategory == category,
                    onClick = { selectedCategory = category }
                )
            }
        }

        // Task List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredActivities) { task ->
                TaskListItem(task = task, onToggle = { onToggleHabit(task) })
            }
        }

        // Add Task Button
        Button(
            onClick = onAddTask,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Task", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
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
fun TaskListItem(task: ActivityItem, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyOutline)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyBackground.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (task.icon.length <= 2) task.icon else "📌", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Title
            Text(
                text = task.description,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Reward
            Text(
                text = "20 XP", // Hardcoded for now per mockup
                color = LavenderText,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Checkbox
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (task.isCompleted) SuccessGreen else Color.Transparent)
                    .border(
                        2.dp,
                        if (task.isCompleted) SuccessGreen else NavyOutline,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Re-using Surface from Material3
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
        ActivityItem(id = 1, icon = "📚", description = "Spanish Lesson", isCompleted = true),
        ActivityItem(id = 2, icon = "🧮", description = "Math Practice", isCompleted = true),
        ActivityItem(id = 3, icon = "🏋️", description = "Workout", isCompleted = false),
        ActivityItem(id = 4, icon = "📓", description = "Journal", isCompleted = false)
    )
    TasksContent(activities = mockTasks, onToggleHabit = {})
}
