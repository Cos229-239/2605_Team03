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


class NudgieOverlayService : Service()
{
    override fun onBind(intent: Intent?): IBinder?
    {
        return null
    }
}