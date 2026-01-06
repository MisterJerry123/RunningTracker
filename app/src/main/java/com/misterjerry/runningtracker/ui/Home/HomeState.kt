package com.misterjerry.runningtracker.ui.Home

import com.misterjerry.runningtracker.domain.model.Run

data class HomeState(
    val runs: List<Run> = emptyList()
)