package com.misterjerry.runningtracker.ui.RunDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunDetailRoot(
    navController: NavController,
    runId: Int,
    viewModel: RunDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(runId) {
        viewModel.loadRun(runId)
    }

    RunDetailScreen(
        state = state
    )
}
