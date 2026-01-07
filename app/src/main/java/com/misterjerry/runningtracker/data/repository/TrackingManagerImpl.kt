package com.misterjerry.runningtracker.data.repository

import android.content.Context
import android.content.Intent
import com.misterjerry.runningtracker.domain.repository.TrackingManager
import com.misterjerry.runningtracker.service.TrackingService
import com.misterjerry.runningtracker.util.Constants.ACTION_PAUSE_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.misterjerry.runningtracker.util.Constants.ACTION_STOP_SERVICE

class TrackingManagerImpl(
    private val context: Context
) : TrackingManager {

    override fun startResumeService() {
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
    }

    override fun pauseService() {
        sendCommandToService(ACTION_PAUSE_SERVICE)
    }

    override fun stopService() {
        sendCommandToService(ACTION_STOP_SERVICE)
    }

    private fun sendCommandToService(action: String) {
        Intent(context, TrackingService::class.java).also {
            it.action = action
            context.startService(it)
        }
    }
}