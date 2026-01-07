package com.misterjerry.runningtracker.presentation.run

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.usecase.GetLastLocationUseCase
import com.misterjerry.runningtracker.domain.usecase.PauseRunUseCase
import com.misterjerry.runningtracker.domain.usecase.SaveLastLocationUseCase
import com.misterjerry.runningtracker.domain.usecase.SaveRunUseCase
import com.misterjerry.runningtracker.domain.usecase.StartRunUseCase
import com.misterjerry.runningtracker.domain.usecase.StopRunUseCase
import com.misterjerry.runningtracker.service.TrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.Calendar

class RunViewModel(
    private val startRunUseCase: StartRunUseCase,
    private val pauseRunUseCase: PauseRunUseCase,
    private val stopRunUseCase: StopRunUseCase,
    private val saveRunUseCase: SaveRunUseCase,
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val saveLastLocationUseCase: SaveLastLocationUseCase
) : ViewModel() {

    private val _initialLocation = MutableStateFlow<GeoPoint?>(null)

    val state = combine(
        TrackingService.isTracking,
        TrackingService.pathPoints,
        TrackingService.timeRunInMillis,
        _initialLocation
    ) { isTracking, pathPoints, timeInMillis, initialLocation ->
        RunState(
            isTracking = isTracking,
            pathPoints = pathPoints,
            timeInMillis = timeInMillis,
            initialLocation = initialLocation
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RunState()
    )

    init {
        viewModelScope.launch {
            _initialLocation.value = getLastLocationUseCase()
        }
        
        // Save last location whenever path points update
        viewModelScope.launch {
            TrackingService.pathPoints.collect { polylines ->
                if (polylines.isNotEmpty() && polylines.last().isNotEmpty()) {
                    val lastPoint = polylines.last().last()
                    saveLastLocationUseCase(lastPoint)
                }
            }
        }
    }

    fun onAction(action: RunAction) {
        when(action) {
            RunAction.StartRun -> startRunUseCase()
            RunAction.PauseRun -> pauseRunUseCase()
            RunAction.StopRun -> stopRun()
        }
    }

    private fun stopRun() {
        val currentState = state.value
        val currentPathPoints = currentState.pathPoints
        val lastPolyline = currentPathPoints.lastOrNull() ?: emptyList()
        val timestamp = Calendar.getInstance().timeInMillis
        val totalDistance =
            currentPathPoints.sumOf { calculatePolylineLength(it).toDouble() }.toInt()
        val curTimeInMillis = currentState.timeInMillis
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
            img = null, 
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
        stopRunUseCase()
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
