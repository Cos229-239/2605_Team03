package com.nightowlcrew.nudgie

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightowlcrew.nudgie.ui.dashboard.AppIconTheme
import com.nightowlcrew.nudgie.ui.dashboard.NudgieDashboard
import com.nightowlcrew.nudgie.ui.dashboard.NudgieViewModel
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme
<<<<<<< HEAD
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.nightowlcrew.nudgie.services.NudgieOverlayService
=======
import com.nightowlcrew.nudgie.utils.IconSwitcherManager
import com.nightowlcrew.nudgie.utils.NudgieIcon
>>>>>>> bb070ed7b0b4e0510b4b7ff027ecc2666aaa5669

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            val viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val appIconTheme by viewModel.appIconTheme.collectAsStateWithLifecycle()

            // Handle App Icon switching reactively in Activity scope to prevent memory leaks in ViewModel
            LaunchedEffect(appIconTheme) {
                val targetIcon = when (appIconTheme) {
                    AppIconTheme.BLUE -> NudgieIcon.BLUE
                    AppIconTheme.FOX -> NudgieIcon.FOX
                    AppIconTheme.AXOLOTL -> NudgieIcon.AXOLOTL
                    AppIconTheme.DRAGON -> NudgieIcon.DRAGON
                }
                IconSwitcherManager.switchToIcon(this@MainActivity, targetIcon)
            }

            // 1. Create the permission launcher for Android 13+
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                // Handle permission result if needed
            }

            // 2. Check and request the permission on launch
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val isGranted = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!isGranted) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    val overlayIntent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivity(overlayIntent)
                } else {
                    startService(Intent(this@MainActivity, NudgieOverlayService::class.java))
                }
            }



            NudgieTheme(appTheme = uiState.currentTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NudgieDashboard(viewModel = viewModel)
                }
            }
        }
    }
}
