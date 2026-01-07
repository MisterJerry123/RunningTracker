package com.misterjerry.runningtracker.presentation.runDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misterjerry.runningtracker.domain.usecase.GetRunByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RunDetailViewModel(
    private val getRunByIdUseCase: GetRunByIdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RunDetailState())
    val state = _state.asStateFlow()

    fun loadRun(id: Int) {
        viewModelScope.launch {
            val run = getRunByIdUseCase(id)
            _state.update { it.copy(run = run) }
        }
    }
}
