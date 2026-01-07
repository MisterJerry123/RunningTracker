package com.misterjerry.runningtracker.presentation.Home

import com.misterjerry.runningtracker.domain.model.Run

data class HomeState(
    val runs: List<Run> = emptyList()
)