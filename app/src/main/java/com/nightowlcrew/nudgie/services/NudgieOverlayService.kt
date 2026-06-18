package com.nightowlcrew.nudgie.services

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

/**
 * Service that displays a floating overlay using Jetpack Compose.
 * Inherits from LifecycleService to provide a LifecycleOwner for Compose.
 * Implements SavedStateRegistryOwner and ViewModelStoreOwner to satisfy ComposeView's requirements.
 */
class NudgieOverlayService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView

    // ViewModelStore and SavedStateRegistry are required for Compose views
    override val viewModelStore: ViewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize SavedStateRegistry
        savedStateRegistryController.performRestore(null)

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        composeView = ComposeView(this).apply {
            // Set the owners required for Compose to function
            setViewTreeLifecycleOwner(this@NudgieOverlayService)
            setViewTreeSavedStateRegistryOwner(this@NudgieOverlayService)
            setViewTreeViewModelStoreOwner(this@NudgieOverlayService)
            
            setContent {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Cyan, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🐣", style = TextStyle(fontSize = 40.sp))
                }
            }
        }

        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
        viewModelStore.clear()
    }
}
