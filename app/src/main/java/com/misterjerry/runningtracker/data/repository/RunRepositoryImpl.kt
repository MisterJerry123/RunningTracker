package com.misterjerry.runningtracker.data.repository

import com.misterjerry.runningtracker.data.RunDao
import com.misterjerry.runningtracker.data.mapper.toDomain
import com.misterjerry.runningtracker.data.mapper.toEntity
import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RunRepositoryImpl(
    private val runDao: RunDao
) : RunRepository {

    override suspend fun insertRun(run: Run) {
        runDao.insertRun(run.toEntity())
    }

    override suspend fun deleteRun(run: Run) {
        runDao.deleteRun(run.toEntity())
    }

    override suspend fun getRunById(id: Int): Run? {
        return runDao.getRunById(id)?.toDomain()
    }

    override fun getAllRunsSortedByDate(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDate().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByTimeInMillis().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByCaloriesBurned().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByAvgSpeed().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllRunsSortedByDistance(): Flow<List<Run>> {
        return runDao.getAllRunsSortedByDistance().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTotalTimeInMillis(): Flow<Long> = runDao.getTotalTimeInMillis()

    override fun getTotalCaloriesBurned(): Flow<Int> = runDao.getTotalCaloriesBurned()

    override fun getTotalDistance(): Flow<Int> = runDao.getTotalDistance()

    override fun getTotalAvgSpeed(): Flow<Float> = runDao.getTotalAvgSpeed()
}
