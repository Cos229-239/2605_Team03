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
import com.nightowlcrew.nudgie.utils.WalkProgress
import com.nightowlcrew.nudgie.utils.WalkProgressManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class WalkTrackingService : Service(), SensorEventListener {

    companion object {
        const val CHANNEL_ID = "WalkTrackingChannel"
        const val NOTIFICATION_ID = 2
        const val COMPLETION_NOTIFICATION_ID = 3
        const val ACTION_START = "ACTION_START_WALK"
        const val ACTION_STOP = "ACTION_STOP_WALK"
        const val EXTRA_TRACKING_MODE = "EXTRA_TRACKING_MODE"
        const val EXTRA_TARGET_STEPS = "EXTRA_TARGET_STEPS"
        const val EXTRA_TARGET_DISTANCE = "EXTRA_TARGET_DISTANCE"
    }

    private var trackingMode: String = "BOTH"
    private var targetSteps: Int = 0
    private var targetDistance: Float = 0f
    private lateinit var walkProgressManager: WalkProgressManager

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
        walkProgressManager = WalkProgressManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                trackingMode = intent.getStringExtra(EXTRA_TRACKING_MODE) ?: "BOTH"
                targetSteps = intent.getIntExtra(EXTRA_TARGET_STEPS, 0)
                targetDistance = intent.getFloatExtra(EXTRA_TARGET_DISTANCE, 0f)

                // Load existing progress if valid for today
                val savedProgress = walkProgressManager.loadProgressIfValid()
                if (savedProgress != null) {
                    currentSessionSteps = savedProgress.currentSteps
                    totalDistanceMeters = savedProgress.currentDistance
                    // If target was already set today, we might want to respect the new intent's target
                    // but for now, let's just ensure we have values.
                }

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
            if (::locationCallback.isInitialized) {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
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
                saveCurrentProgress()
                updateNotification()
                checkCompletion()
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

            if (initialStepCount == -1) {
                initialStepCount = totalStepsSinceReboot
                // If we loaded saved progress, we need to adjust our session baseline
                // to continue from where we left off relative to totalStepsSinceReboot.
                initialStepCount -= currentSessionSteps
            }

            currentSessionSteps = totalStepsSinceReboot - initialStepCount
            saveCurrentProgress()
            updateNotification()
            checkCompletion()
        }
    }

    private fun saveCurrentProgress() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        walkProgressManager.saveProgress(
            WalkProgress(
                currentSteps = currentSessionSteps,
                targetSteps = targetSteps,
                currentDistance = totalDistanceMeters,
                targetDistance = targetDistance,
                lastWalkDate = today
            )
        )
    }

    private fun checkCompletion() {
        val stepsDone = if (targetSteps > 0) currentSessionSteps >= targetSteps else false
        val distanceDone = if (targetDistance > 0) totalDistanceMeters >= targetDistance else false

        val isFinished = when (trackingMode) {
            "DISTANCE" -> distanceDone
            "STEPS" -> stepsDone
            "BOTH" -> stepsDone && distanceDone
            else -> false
        }

        if (isFinished) {
            onWalkCompleted()
        }
    }

    private fun onWalkCompleted() {
        // Stop tracking
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        sensorManager.unregisterListener(this)

        // Clear data
        walkProgressManager.clearProgress()

        // Push completion notification
        val manager = getSystemService(NotificationManager::class.java)
        val completionNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Walk Completed!")
            .setContentText("Nudgie is happy! 🐾 You've reached your goal.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .build()
        manager.notify(COMPLETION_NOTIFICATION_ID, completionNotification)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
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
            val distTargetText = if (targetDistance > 0) "/${targetDistance.roundToInt()}m" else "m"
            statusText += "\nDistance: ${totalDistanceMeters.roundToInt()}$distTargetText"
        }
        if (trackingMode == "STEPS" || trackingMode == "BOTH") {
            val stepsTargetText = if (targetSteps > 0) "/$targetSteps" else ""
            statusText += "\nSteps: $currentSessionSteps$stepsTargetText"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Nudgie Walk Tracker")
            .setContentText(statusText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(statusText))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
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
