package com.misterjerry.runningtracker.domain.model

import org.osmdroid.util.GeoPoint

data class Run(
    val id: Int = 0,
    val img: ByteArray? = null,
    val timestamp: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val distanceInMeters: Int = 0,
    val timeInMillis: Long = 0L,
    val caloriesBurned: Int = 0,
    val pathPoints: List<List<GeoPoint>> = emptyList()
)
