package com.misterjerry.runningtracker

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misterjerry.runningtracker.data.Run
import com.misterjerry.runningtracker.data.RunDatabase
import com.misterjerry.runningtracker.service.TrackingService
import com.misterjerry.runningtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_STOP_SERVICE
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val runDao = RunDatabase.getDatabase(application).getRunDao()

    val runs = runDao.getAllRunsSortedByDate()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isTracking = TrackingService.isTracking
    val pathPoints = TrackingService.pathPoints
    val timeRunInMillis = TrackingService.timeRunInMillis

    fun startRun() {
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    fun pauseRun() {
        sendCommandToService(ACTION_PAUSE_SERVICE)
    }

    fun stopRun(context: Context, mapScreenshot: Bitmap?) {
        sendCommandToService(ACTION_STOP_SERVICE)
        // Save Run here using the last known state
        val timestamp = Calendar.getInstance().timeInMillis
        val distanceInMeters = calculatePolylineLength(pathPoints.value.lastOrNull() ?: emptyList()).toInt() // Simplified for last segment or sum up all
        val totalDistance = pathPoints.value.sumOf { calculatePolylineLength(it).toDouble() }.toInt()
        val curTimeInMillis = timeRunInMillis.value
        val avgSpeed = if (curTimeInMillis > 0) {
            (totalDistance / 1000f) / (curTimeInMillis / 1000f / 60 / 60) // km/h
        } else {
            0f
        }

        val run = Run(
            img = null, // Can process bitmap here if needed (mapScreenshot)
            timestamp = timestamp,
            avgSpeedInKMH = avgSpeed,
            distanceInMeters = totalDistance,
            timeInMillis = curTimeInMillis,
            caloriesBurned = 0 // Calculator needed
        )
        
        viewModelScope.launch {
            runDao.insertRun(run)
        }
    }

    private fun sendCommandToService(action: String) {
        Intent(getApplication(), TrackingService::class.java).also {
            it.action = action
            getApplication<Application>().startService(it)
        }
    }
    
    private fun calculatePolylineLength(polyline: List<GeoPoint>): Float {
        var distance = 0f
        for(i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i+1]
            val result = FloatArray(1)
            android.location.Location.distanceBetween(
                pos1.latitude, pos1.longitude,
                pos2.latitude, pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }
}

class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
