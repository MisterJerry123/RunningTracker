package com.misterjerry.runningtracker.data.repository

import android.content.Context
import android.content.Intent
import com.misterjerry.runningtracker.domain.repository.TrackingManager
import com.misterjerry.runningtracker.service.TrackingService
import com.misterjerry.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE

class TrackingManagerImpl(
    private val context: Context
) : TrackingManager {

    override fun startResumeService() {
        Intent(context, TrackingService::class.java).also {
            it.action = ACTION_START_OR_RESUME_SERVICE
            context.startService(it)
        }
    }
}
