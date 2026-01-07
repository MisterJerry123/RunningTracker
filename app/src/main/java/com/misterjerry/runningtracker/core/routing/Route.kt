package com.misterjerry.runningtracker.core.routing

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Route: NavKey {
    @Serializable
    data object Home : Route

    @Serializable
    data object Run : Route

    @Serializable
    data class RunDetail(val runId : Int) : Route


}