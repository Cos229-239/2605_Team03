package com.nightowlcrew.nudgie.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.nightowlcrew.nudgie.MainActivity
import com.nightowlcrew.nudgie.R
import kotlin.math.roundToInt

class WalkTrackingService : Service(), SensorEventListener {

    companion object {
        const val CHANNEL_ID = "WalkTrackingChannel"
        const val NOTIFICATION_ID = 2
        const val ACTION_START = "ACTION_START_WALK"
        const val ACTION_STOP = "ACTION_STOP_WALK"
        const val EXTRA_TRACKING_MODE = "EXTRA_TRACKING_MODE"
    }

    private var trackingMode: String = "BOTH"

    // GPS Variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var totalDistanceMeters = 0f

    // Pedometer Variables
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var initialStepCount = -1
    private var currentSessionSteps = 0

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                trackingMode = intent.getStringExtra(EXTRA_TRACKING_MODE) ?: "BOTH"
                startWalkTracking()
            }
            ACTION_STOP -> stopWalkTracking()
        }
        return START_STICKY
    }

    private fun startWalkTracking() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        if (trackingMode == "DISTANCE" || trackingMode == "BOTH") {
            startGpsTracking()
        }
        if (trackingMode == "STEPS" || trackingMode == "BOTH") {
            startStepTracking()
        }
    }

    private fun stopWalkTracking() {
        if (trackingMode == "DISTANCE" || trackingMode == "BOTH") {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        if (trackingMode == "STEPS" || trackingMode == "BOTH") {
            sensorManager.unregisterListener(this)
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startGpsTracking() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .setMinUpdateIntervalMillis(2000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (lastLocation != null) {
                        totalDistanceMeters += lastLocation!!.distanceTo(location)
                    }
                    lastLocation = location
                }
                updateNotification()
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun startStepTracking() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceReboot = event.values[0].toInt()

            // Set the baseline on the very first step detected
            if (initialStepCount == -1) {
                initialStepCount = totalStepsSinceReboot
            }

            currentSessionSteps = totalStepsSinceReboot - initialStepCount
            updateNotification()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification())
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        var statusText = "Walking Nudgie! 🐾"
        if (trackingMode == "DISTANCE" || trackingMode == "BOTH") {
            statusText += "\nDistance: ${totalDistanceMeters.roundToInt()} meters"
        }
        if (trackingMode == "STEPS" || trackingMode == "BOTH") {
            statusText += "\nSteps: $currentSessionSteps"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Nudgie Walk Tracker")
            .setContentText(statusText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(statusText))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Walk Tracking", NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}