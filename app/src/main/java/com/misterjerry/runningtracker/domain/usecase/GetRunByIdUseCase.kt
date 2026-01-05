package com.misterjerry.runningtracker.domain.usecase

import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.repository.RunRepository

class GetRunByIdUseCase(private val repository: RunRepository) {
    suspend operator fun invoke(id: Int): Run? {
        return repository.getRunById(id)
    }
}
