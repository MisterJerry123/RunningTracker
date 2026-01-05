package com.misterjerry.runningtracker.domain.usecase

import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow

class GetRunsUseCase(private val repository: RunRepository) {
    operator fun invoke(): Flow<List<Run>> {
        // Default sort by date, logic can be expanded
        return repository.getAllRunsSortedByDate()
    }
}
