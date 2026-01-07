package com.misterjerry.runningtracker.presentation.runDetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunDetailRoot(
    runId: Int,
    viewModel: RunDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(runId) {
        viewModel.onAction(RunDetailAction.LoadRun(runId))
    }

    RunDetailScreen(
        state = state
    )
}