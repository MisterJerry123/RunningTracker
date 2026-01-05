package com.misterjerry.runningtracker.domain.usecase

import com.misterjerry.runningtracker.domain.repository.RunRepository
import org.osmdroid.util.GeoPoint

class GetLastLocationUseCase(private val repository: RunRepository) {
    suspend operator fun invoke(): GeoPoint? {
        return repository.getLastKnownLocation()
    }
}
