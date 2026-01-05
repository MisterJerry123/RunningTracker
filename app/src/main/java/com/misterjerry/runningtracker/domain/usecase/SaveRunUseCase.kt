package com.misterjerry.runningtracker.domain.usecase

import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.repository.RunRepository

class SaveRunUseCase(private val repository: RunRepository) {
    suspend operator fun invoke(run: Run) {
        repository.insertRun(run)
    }
}
