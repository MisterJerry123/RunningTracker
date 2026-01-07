package com.misterjerry.runningtracker.presentation.home

import com.misterjerry.runningtracker.domain.usecase.StartRunUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misterjerry.runningtracker.domain.model.Run
import com.misterjerry.runningtracker.domain.usecase.DeleteRunUseCase
import com.misterjerry.runningtracker.domain.usecase.GetRunsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val startRunUseCase: StartRunUseCase,
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
        startRunUseCase()
    }

    fun deleteRun(run: Run) {
        viewModelScope.launch {
            deleteRunUseCase(run)
        }
    }
}