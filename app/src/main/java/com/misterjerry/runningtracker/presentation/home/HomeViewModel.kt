package com.misterjerry.runningtracker.presentation.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.usecase.DeleteRunUseCase
import com.misterjerry.runningtracker.domain.usecase.GetRunsUseCase
import com.misterjerry.runningtracker.service.TrackingService
import com.misterjerry.runningtracker.util.Constants.ACTION_START_OR_RESUME_SERVICE
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val context: Context,
    private val getRunsUseCase: GetRunsUseCase,
    private val deleteRunUseCase: DeleteRunUseCase
) : ViewModel() {

    val state = getRunsUseCase()
        .map { HomeState(runs = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeState()
        )

    fun startRun() {
        Intent(context, TrackingService::class.java).also {
            it.action = ACTION_START_OR_RESUME_SERVICE
            context.startService(it)
        }
    }

    fun deleteRun(run: Run) {
        viewModelScope.launch {
            deleteRunUseCase(run)
        }
    }
}