package com.nightowlcrew.nudgie.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.nightowlcrew.nudgie.data.AccessoryItem
import com.nightowlcrew.nudgie.ui.theme.*

@Composable
fun ShopDialog(
    currency: Int,
    accessories: List<AccessoryItem>,
    onBuy: (AccessoryItem) -> Unit,
    onEquip: (AccessoryItem) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = NavySurface,
            modifier = Modifier.fillMaxWidth().height(500.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Nudgie Shop", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, fontFamily = VT323)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💎", fontSize = 20.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("$currency", color = BrandGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items List
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(accessories) { item ->
                        ShopItemRow(item, currentCurrency = currency, onBuy = { onBuy(item) }, onEquip = { onEquip(item) })
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyOutline)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ShopItemRow(
    item: AccessoryItem,
    currentCurrency: Int,
    onBuy: () -> Unit,
    onEquip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NavyBackground)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Temporary Generic Icons until you add your real transparent PNGs
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(id = item.iconResId), contentDescription = item.name, modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (!item.isPurchased) {
                Text("Cost: ${item.cost} 💎", color = if (currentCurrency >= item.cost) SuccessGreen else Color.Red, fontSize = 14.sp)
            } else {
                Text("Owned", color = BrandGold, fontSize = 14.sp)
            }
        }

        // Action Button
        if (!item.isPurchased) {
            Button(
                onClick = onBuy,
                enabled = currentCurrency >= item.cost,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text("Buy")
            }
        } else {
            Button(
                onClick = onEquip,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (item.isEquipped) NavyOutline else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (item.isEquipped) "Equipped" else "Equip")
            }
        }
    }
}