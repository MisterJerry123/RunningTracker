package com.misterjerry.runningtracker.presentation.runDetail

sealed interface RunDetailAction {
    data class LoadRun(val id: Int) : RunDetailAction
}
