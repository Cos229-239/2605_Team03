package com.nightowlcrew.nudgie.services


import android.app.Service
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier




class NudgieOverlayService : Service()
{
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView


    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }

    override fun onCreate()
    {
        super.onCreate()

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
    }
}

