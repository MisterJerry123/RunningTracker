package com.misterjerry.runningtracker.data.repository

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.misterjerry.runningtracker.data.RunDao
import com.misterjerry.runningtracker.data.mapper.toDomain
import com.misterjerry.runningtracker.data.mapper.toEntity
import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint

class RunRepositoryImpl(
    private val runDao: RunDao,
    private val sharedPreferences: SharedPreferences,
    private val fusedLocationProviderClient: FusedLocationProviderClient
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
    
    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): GeoPoint? {
        // 1. Try saved location from app prefs
        val lat = sharedPreferences.getFloat("KEY_LAST_LAT", Float.MIN_VALUE)
        val lon = sharedPreferences.getFloat("KEY_LAST_LON", Float.MIN_VALUE)
        
        if (lat != Float.MIN_VALUE && lon != Float.MIN_VALUE) {
            return GeoPoint(lat.toDouble(), lon.toDouble())
        }
        
        // 2. Fallback to system last known location
        return try {
            val location = fusedLocationProviderClient.lastLocation.await()
            location?.let { GeoPoint(it.latitude, it.longitude) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveLastKnownLocation(geoPoint: GeoPoint) {
        sharedPreferences.edit()
            .putFloat("KEY_LAST_LAT", geoPoint.latitude.toFloat())
            .putFloat("KEY_LAST_LON", geoPoint.longitude.toFloat())
            .apply()
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
