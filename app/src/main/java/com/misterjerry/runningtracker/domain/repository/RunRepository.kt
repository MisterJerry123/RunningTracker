package com.misterjerry.runningtracker.domain.repository

import com.misterjerry.runningtracker.domain.model.Run
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    suspend fun insertRun(run: Run)
    suspend fun deleteRun(run: Run)
    suspend fun getRunById(id: Int): Run?
    fun getAllRunsSortedByDate(): Flow<List<Run>>
    fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>>
    fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>>
    fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>>
    fun getAllRunsSortedByDistance(): Flow<List<Run>>
    fun getTotalTimeInMillis(): Flow<Long>
    fun getTotalCaloriesBurned(): Flow<Int>
    fun getTotalDistance(): Flow<Int>
    fun getTotalAvgSpeed(): Flow<Float>
}
