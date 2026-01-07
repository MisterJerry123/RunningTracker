package com.misterjerry.runningtracker.domain.usecase

import com.misterjerry.runningtracker.domain.repository.TrackingManager

class StartRunUseCase(
    private val trackingManager: TrackingManager
) {
    operator fun invoke() {
        trackingManager.startResumeService()
    }
}
