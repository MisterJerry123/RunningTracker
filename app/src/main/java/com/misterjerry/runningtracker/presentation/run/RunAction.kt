package com.misterjerry.runningtracker.presentation.run

sealed interface RunAction {
    data object StartRun : RunAction
    data object PauseRun : RunAction
    data object StopRun : RunAction
}
