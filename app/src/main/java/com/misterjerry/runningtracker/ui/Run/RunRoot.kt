package com.misterjerry.runningtracker.ui.Run

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunRoot(
    onFinish: () -> Unit,
    viewModel: RunViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    RunScreen(
        state = state,
        onStartRunClick = viewModel::startRun,
        onPauseRunClick = viewModel::pauseRun,
        onStopRunClick = { context ->
            viewModel.stopRun(context, null)
            onFinish()
        }
    )
}