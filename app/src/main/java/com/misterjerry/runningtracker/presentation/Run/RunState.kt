package com.misterjerry.runningtracker.presentation.Run

import org.osmdroid.util.GeoPoint

data class RunState(
    val isTracking: Boolean = false,
    val pathPoints: List<List<GeoPoint>> = emptyList(),
    val timeInMillis: Long = 0L,
    val initialLocation: GeoPoint? = null
)
