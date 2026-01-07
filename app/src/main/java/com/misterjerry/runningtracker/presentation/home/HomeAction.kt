package com.misterjerry.runningtracker.presentation.home

import com.misterjerry.runningtracker.domain.model.Run

sealed interface HomeAction {
    data object StartRun : HomeAction
    data class DeleteRun(val run: Run) : HomeAction
}
