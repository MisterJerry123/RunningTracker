package com.misterjerry.runningtracker

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misterjerry.runningtracker.data.RunDatabase
import com.misterjerry.runningtracker.data.repository.RunRepositoryImpl
import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.usecase.DeleteRunUseCase
import com.misterjerry.runningtracker.domain.usecase.GetLastLocationUseCase
import com.misterjerry.runningtracker.domain.usecase.GetRunByIdUseCase
import com.misterjerry.runningtracker.domain.usecase.GetRunsUseCase
import com.misterjerry.runningtracker.domain.usecase.SaveLastLocationUseCase
import com.misterjerry.runningtracker.domain.usecase.SaveRunUseCase
import com.misterjerry.runningtracker.service.TrackingService
import com.misterjerry.runningtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_STOP_SERVICE
import org.osmdroid.util.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import com.google.android.gms.location.LocationServices

class MainViewModel(
    application: Application,
    private val getRunsUseCase: GetRunsUseCase,
    private val saveRunUseCase: SaveRunUseCase,
    private val deleteRunUseCase: DeleteRunUseCase,
    private val getRunByIdUseCase: GetRunByIdUseCase,
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val saveLastLocationUseCase: SaveLastLocationUseCase
) : AndroidViewModel(application) {

    val runs = getRunsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isTracking = TrackingService.isTracking

    val pathPoints = TrackingService.pathPoints.onEach { polylines ->
        if (polylines.isNotEmpty() && polylines.last().isNotEmpty()) {
            val lastPoint = polylines.last().last()
            saveLastLocationUseCase(lastPoint)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val timeRunInMillis = TrackingService.timeRunInMillis

    private val _initialLocation = MutableStateFlow<GeoPoint?>(null)
    val initialLocation = _initialLocation.asStateFlow()

    init {
        viewModelScope.launch {
            _initialLocation.value = getLastLocationUseCase()
        }
    }

    fun startRun() {
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    fun pauseRun() {
        sendCommandToService(ACTION_PAUSE_SERVICE)
    }

    fun stopRun(context: Context, mapScreenshot: Bitmap?) {
        // Capture current state before stopping service
        val currentPathPoints = pathPoints.value
        val lastPolyline = currentPathPoints.lastOrNull() ?: emptyList()
        val timestamp = Calendar.getInstance().timeInMillis
        val distanceInMeters = calculatePolylineLength(lastPolyline).toInt()
        val totalDistance =
            currentPathPoints.sumOf { calculatePolylineLength(it).toDouble() }.toInt()
        val curTimeInMillis = timeRunInMillis.value
        val avgSpeed = if (curTimeInMillis > 0) {
            (totalDistance / 1000f) / (curTimeInMillis / 1000f / 60 / 60) // km/h
        } else {
            0f
        }

        // Save last known location explicitly
        if (lastPolyline.isNotEmpty()) {
            val lastPoint = lastPolyline.last()
            viewModelScope.launch {
                saveLastLocationUseCase(lastPoint)
            }
        }

        val run = Run(
            img = null, // Can process bitmap here if needed (mapScreenshot)
            timestamp = timestamp,
            avgSpeedInKMH = avgSpeed,
            distanceInMeters = totalDistance,
            timeInMillis = curTimeInMillis,
            caloriesBurned = 0, // Calculator needed
            pathPoints = currentPathPoints
        )

        viewModelScope.launch {
            saveRunUseCase(run)
        }

        // Stop service after capturing data
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    suspend fun getRunById(id: Int): Run? {
        return getRunByIdUseCase(id)
    }

    fun deleteRun(run: Run) {
        viewModelScope.launch {
            deleteRunUseCase(run)
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
        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]
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
            val db = RunDatabase.getDatabase(application)
            val sharedPrefs = application.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
            val repository = RunRepositoryImpl(db.getRunDao(), sharedPrefs, fusedLocationClient)
            val getRunsUseCase = GetRunsUseCase(repository)
            val saveRunUseCase = SaveRunUseCase(repository)
            val deleteRunUseCase = DeleteRunUseCase(repository)
            val getRunByIdUseCase = GetRunByIdUseCase(repository)
            val getLastLocationUseCase = GetLastLocationUseCase(repository)
            val saveLastLocationUseCase = SaveLastLocationUseCase(repository)
            
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                application,
                getRunsUseCase,
                saveRunUseCase,
                deleteRunUseCase,
                getRunByIdUseCase,
                getLastLocationUseCase,
                saveLastLocationUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
