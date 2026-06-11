package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nightowlcrew.nudgie.data.AccessoryCategory
import com.nightowlcrew.nudgie.data.AccessoryItem
import com.nightowlcrew.nudgie.ui.theme.BrandGold
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopDialog(
    currency: Int,
    accessories: List<AccessoryItem>,
    onBuy: (AccessoryItem) -> Unit,
    onEquip: (AccessoryItem) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Outfits", "Toys", "Food")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            color = NavyBackground,
            border = BorderStroke(2.dp, NavyOutline)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nudgie Mart", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💎 $currency", color = BrandGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(16.dp))
                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Custom Tab Row
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = NavySurface,
                    contentColor = BrandGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = BrandGold,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) BrandGold else Color.Gray,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Filter Items based on Tab
                val currentItems = when (selectedTabIndex) {
                    0 -> accessories.filter { it.category in listOf(AccessoryCategory.HAT, AccessoryCategory.GLASSES, AccessoryCategory.OUTFIT) }
                    1 -> accessories.filter { it.category == AccessoryCategory.TOY }
                    2 -> accessories.filter { it.category == AccessoryCategory.FOOD }
                    else -> emptyList()
                }

                // Shop Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(currentItems) { item ->
                        ShopItemCard(
                            item = item,
                            canAfford = currency >= item.cost,
                            onBuy = { onBuy(item) },
                            onEquip = { onEquip(item) },
                            isConsumable = item.category == AccessoryCategory.FOOD // Changes "Equip" to "Use"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(
    item: AccessoryItem,
    canAfford: Boolean,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    isConsumable: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (item.isEquipped) BrandGold else NavyOutline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyBackground.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = item.iconResId),
                    contentDescription = item.name,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = item.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.height(40.dp) // Ensures buttons align nicely
            )

            Spacer(Modifier.height(8.dp))

            if (!item.isPurchased) {
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (canAfford) "💎 ${item.cost}" else "Locked")
                }
            } else {
                Button(
                    onClick = onEquip,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.isEquipped) NavyOutline else BrandGold
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val actionText = if (isConsumable) "Use" else "Equip"
                    Text(
                        text = if (item.isEquipped) "Equipped" else actionText,
                        color = if (item.isEquipped) Color.White else NavyBackground
                    )
                }
            }
        }
    }
}