package com.nightowlcrew.nudgie.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightowlcrew.nudgie.ui.theme.ElectricYellow
import com.nightowlcrew.nudgie.ui.theme.LavenderText
import com.nightowlcrew.nudgie.ui.theme.NavyBackground
import com.nightowlcrew.nudgie.ui.theme.NavyOutline
import com.nightowlcrew.nudgie.ui.theme.NavySurface
import com.nightowlcrew.nudgie.ui.theme.PressStart2P
import com.nightowlcrew.nudgie.ui.theme.SpaceAccent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.nightowlcrew.nudgie.ui.theme.nudgieCardShadow

/**
 * Simplified Settings screen containing global app configurations.
 */
@Composable
fun SettingsScreen(
    viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        currentTheme = uiState.currentTheme,
        overlayEnabled = uiState.overlayEnabled,
        isAnonymous = uiState.isAnonymous,
        onUpdateTheme = { theme -> viewModel.updateTheme(theme) },
        onUpdateOverlayEnabled = { viewModel.updateOverlayEnabled(it) },
    )
}

@Composable
fun SettingsContent(
    currentTheme: AppTheme,
    overlayEnabled: Boolean,
    isAnonymous: Boolean,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdateOverlayEnabled: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isThemeDropdownExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "APP SETTINGS",
            style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PressStart2P),
            color = LavenderText,
            fontWeight = FontWeight.Bold
        )

        AccountSettingsCard(isAnonymous = isAnonymous)

        ThemeSelectionCard(
            currentTheme = currentTheme,
            onUpdateTheme = onUpdateTheme,
            isExpanded = isThemeDropdownExpanded,
            onToggleExpand = { isThemeDropdownExpanded = !isThemeDropdownExpanded }
        )

        OverlaySettingsCard(
            overlayEnabled = overlayEnabled,
            onUpdateOverlayEnabled = onUpdateOverlayEnabled,
            currentTheme = currentTheme
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsCard(isAnonymous: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAnonymous) Icons.Default.Lock else Icons.Default.Sync,
                    contentDescription = null,
                    tint = if (isAnonymous) androidx.compose.ui.graphics.Color.Gray else ElectricYellow,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = if (isAnonymous) "ANONYMOUS ACCOUNT" else "SYNCED ACCOUNT",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = PressStart2P),
                    color = androidx.compose.ui.graphics.Color.White
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAnonymous) 
                    "Your data is only saved on this device. Link an account to sync across devices." 
                    else "Your data is backed up and synced to the cloud.",
                style = MaterialTheme.typography.bodySmall,
                color = LavenderText
            )
            if (isAnonymous) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Trigger Google/Email Linking */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SpaceAccent)
                ) {
                    Text("LINK ACCOUNT", style = MaterialTheme.typography.labelLarge.copy(fontFamily = PressStart2P))
                }
            }
        }
    }
}

@Composable
fun ThemeSelectionCard(
    currentTheme: AppTheme,
    onUpdateTheme: (AppTheme) -> Unit,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    CommonManagementCard(currentTheme = currentTheme) {
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

/**
 * UI Card to toggle the "Draw over other apps" overlay.
 * Handles permission checking and navigation to system settings if permission is missing.
 */
@Composable
fun OverlaySettingsCard(
    overlayEnabled: Boolean,
    onUpdateOverlayEnabled: (Boolean) -> Unit,
    currentTheme: AppTheme
) {
    val context = LocalContext.current
    CommonManagementCard(currentTheme = currentTheme) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nudgie Overlay",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Show Nudgie over other apps to help you stay focused.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = overlayEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        if (!Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            onUpdateOverlayEnabled(true)
                        }
                    } else {
                        onUpdateOverlayEnabled(false)
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsContentPreview() {
    MaterialTheme {
        Surface {
            SettingsContent(
                currentTheme = AppTheme.DEFAULT,
                overlayEnabled = false,
                isAnonymous = true,
                onUpdateTheme = { },
                onUpdateOverlayEnabled = { }
            )
        }
    }
}
