package com.misterjerry.runningtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_table")
data class Run(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var img: ByteArray? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0L,
    var caloriesBurned: Int = 0,
    var pathPoints: String = "" // Consider storing as a JSON string or simplified format for now
)
