package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightowlcrew.nudgie.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NudgieDashboard() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Pet", "Learn", "Stats", "Settings")
    val icons = listOf(
        Icons.Filled.Home,
        Icons.Filled.AutoAwesome, // Pet equivalent
        Icons.Filled.MenuBook, // Learn
        Icons.Filled.ShowChart, // Stats
        Icons.Filled.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3F4F6),
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item, fontSize = 10.sp) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.Black,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = DarkBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // HEADER
                Text(
                    text = "BUDDY",
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 24.sp,
                    color = Color.Black
                )
                Text(
                    text = "Level 5 • Baby Stage",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // TOP HAPPINESS BAR
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "HAPPINESS",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                "85%",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.85f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),
                            color = Color.Gray,
                            trackColor = Color(0xFF374151)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // PET SCREEN
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Color(0xFFD1D5DB), RoundedCornerShape(24.dp))
                        .border(4.dp, DarkBackground, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Placeholder for Pixel Art Pet
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("(◕‿◕)", fontSize = 60.sp, color = DarkBackground)
                        // In a real app, this would be a pixel art Image
                    }

                    // Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .background(DarkBackground, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.EmojiEvents,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("1", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DASHBOARD SECTION
                Text(
                    text = "DASHBOARD",
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                // STATS GRID
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NewStatCard(
                            label = "HAPPINESS",
                            value = "85%",
                            icon = Icons.Filled.Favorite,
                            iconColor = Color.Red,
                            bgColor = CardRedBg,
                            borderColor = HeartRed,
                            modifier = Modifier.weight(1f)
                        )
                        NewStatCard(
                            label = "ENERGY",
                            value = "62%",
                            icon = Icons.Filled.FlashOn,
                            iconColor = Color(0xFFFFC107),
                            bgColor = CardYellowBg,
                            borderColor = ElectricYellow,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        NewStatCard(
                            label = "LEVEL",
                            value = "5",
                            icon = Icons.Filled.Star,
                            iconColor = Color.Blue,
                            bgColor = CardBlueBg,
                            borderColor = LevelUpBlue,
                            modifier = Modifier.weight(1f)
                        )
                        NewStatCard(
                            label = "AGE",
                            value = "5 Days",
                            icon = Icons.Filled.CalendarToday,
                            iconColor = Color.Green,
                            bgColor = CardGreenBg,
                            borderColor = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // TODAY'S ACTIVITY
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TODAY'S ACTIVITY",
                        style = MaterialTheme.typography.labelMedium,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for scroll
            }
        }
    }
}

@Composable
fun NewStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}
