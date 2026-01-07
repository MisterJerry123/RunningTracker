package com.misterjerry.runningtracker.presentation.run

import android.graphics.Bitmap

sealed interface RunAction {
    data object StartRun : RunAction
    data object PauseRun : RunAction
    data class StopRun(val mapScreenshot: Bitmap?) : RunAction
}
