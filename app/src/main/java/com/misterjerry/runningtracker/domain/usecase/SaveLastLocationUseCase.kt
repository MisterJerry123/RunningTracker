package com.misterjerry.runningtracker.domain.usecase

import com.misterjerry.runningtracker.domain.repository.RunRepository
import org.osmdroid.util.GeoPoint

class SaveLastLocationUseCase(private val repository: RunRepository) {
    suspend operator fun invoke(geoPoint: GeoPoint) {
        repository.saveLastKnownLocation(geoPoint)
    }
}
