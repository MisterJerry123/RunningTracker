package com.misterjerry.runningtracker.domain.repository

interface TrackingManager {
    fun startResumeService()
    fun pauseService()
    fun stopService()
}