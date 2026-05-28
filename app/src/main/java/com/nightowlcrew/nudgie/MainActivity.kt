package com.nightowlcrew.nudgie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nightowlcrew.nudgie.ui.dashboard.NudgieDashboard
import com.nightowlcrew.nudgie.ui.dashboard.NudgieViewModel
import com.nightowlcrew.nudgie.ui.theme.NudgieTheme

class  MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NudgieViewModel = viewModel(factory = NudgieViewModel.Factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            NudgieTheme(appTheme = uiState.currentTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // custom dashboard
                        NudgieDashboard(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
