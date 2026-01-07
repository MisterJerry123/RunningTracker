package com.misterjerry.runningtracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.misterjerry.runningtracker.MainActivity
import com.misterjerry.runningtracker.R
import com.misterjerry.runningtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.misterjerry.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_STOP_SERVICE
import com.misterjerry.runningtracker.util.Constants.LOCATION_UPDATE_INTERVAL
import com.misterjerry.runningtracker.util.Constants.NOTIFICATION_CHANNEL_ID
import com.misterjerry.runningtracker.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.misterjerry.runningtracker.util.Constants.NOTIFICATION_ID
import com.misterjerry.runningtracker.util.Constants.TIMER_UPDATE_INTERVAL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

typealias Polyline = MutableList<GeoPoint>
typealias Polylines = MutableList<Polyline>

class TrackingService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private var isFirstRun = true
    private var serviceKilled = false

    private val timeRunInSeconds = MutableStateFlow(0L)

    companion object {
        val timeRunInMillis = MutableStateFlow(0L)
        val isTracking = MutableStateFlow(false)
        val pathPoints = MutableStateFlow<Polylines>(mutableListOf())
    }

    private fun postInitialValues() {
        isTracking.value = false
        pathPoints.value = mutableListOf()
        timeRunInSeconds.value = 0L
        timeRunInMillis.value = 0L
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(this)
        )

        isTracking.onEach {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        }.launchIn(serviceScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        // Prevent starting multiple timers if already tracking
                        if (!isTracking.value) {
                            startTimer()
                        }
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }

                ACTION_STOP_SERVICE -> {
                    killService()
                }
            }
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.value = true

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                baseNotificationBuilder.build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        }

        timeRunInSeconds.onEach {
            if (!serviceKilled) {
                val notification = curNotificationBuilder
                    .setContentText(getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }.launchIn(serviceScope)
    }

    private fun startTimer() {
        addEmptyPolyline()
        isTracking.value = true

        serviceScope.launch {
            while (isTracking.value) {
                val loopStart = System.currentTimeMillis()
                timeRunInMillis.value += TIMER_UPDATE_INTERVAL

                if (timeRunInMillis.value >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.value += 1
                    lastSecondTimestamp += 1000L
                }
                val delayTime = TIMER_UPDATE_INTERVAL - (System.currentTimeMillis() - loopStart)
                if (delayTime > 0) delay(delayTime)
            }
        }
    }

    private var lastSecondTimestamp = 0L

    private fun pauseService() {
        isTracking.value = false
    }

    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private var locationJob: kotlinx.coroutines.Job? = null

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            // Cancel previous job if exists to avoid duplicates
            locationJob?.cancel()
            locationJob = locationClient.getLocationUpdates(LOCATION_UPDATE_INTERVAL)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    addPathPoint(location)
                }
                .launchIn(serviceScope)
        } else {
            locationJob?.cancel()
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = GeoPoint(it.latitude, it.longitude)
            pathPoints.value.apply {
                last().add(pos)
                val newPathPoints = mutableListOf<Polyline>()
                newPathPoints.addAll(this)
                pathPoints.value = newPathPoints
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        val newPathPoints = mutableListOf<Polyline>()
        newPathPoints.addAll(this)
        pathPoints.value = newPathPoints
    } ?: pathPoints.value.let {
        pathPoints.value = mutableListOf(mutableListOf())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private val baseNotificationBuilder by lazy {
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher) // Make sure to use valid icon
            .setContentTitle("Running Tracker")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())
    }

    private lateinit var curNotificationBuilder: NotificationCompat.Builder

    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Remove previous actions before adding new one
        curNotificationBuilder = baseNotificationBuilder // Refresh builder state if needed
            .setContentTitle(if (isTracking) "Running..." else "Paused")
            .clearActions()
            .addAction(R.drawable.ic_launcher_foreground, notificationActionText, pendingIntent)

        if (!serviceKilled) {
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    // Helper for formatting time
    private fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hours = milliseconds / 1000 / 60 / 60
        milliseconds -= hours * 1000 * 60 * 60
        val minutes = milliseconds / 1000 / 60
        milliseconds -= minutes * 1000 * 60
        val seconds = milliseconds / 1000
        milliseconds -= seconds * 1000
        milliseconds /= 10
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds" +
                if (includeMillis) ":${if (milliseconds < 10) "0" else ""}$milliseconds" else ""
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
