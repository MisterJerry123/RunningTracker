package com.misterjerry.runningtracker.data.mapper

import com.misterjerry.runningtracker.data.local.entity.RunEntity
import com.misterjerry.runningtracker.domain.model.Run

fun RunEntity.toDomain(): Run {
    return Run(
        id = id,
        img = img,
        timestamp = timestamp,
        avgSpeedInKMH = avgSpeedInKMH,
        distanceInMeters = distanceInMeters,
        timeInMillis = timeInMillis,
        caloriesBurned = caloriesBurned,
        pathPoints = pathPoints
    )
}

fun Run.toEntity(): RunEntity {
    return RunEntity(
        id = id,
        img = img,
        timestamp = timestamp,
        avgSpeedInKMH = avgSpeedInKMH,
        distanceInMeters = distanceInMeters,
        timeInMillis = timeInMillis,
        caloriesBurned = caloriesBurned,
        pathPoints = pathPoints
    )
}
