package com.misterjerry.runningtracker.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(
    onStartRunClick: () -> Unit,
    onRunClick: (Int) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeScreen(
        state = state,
        onStartRunClick = {
            viewModel.startRun()
            onStartRunClick()
        },
        onRunClick = onRunClick,
        onDeleteRunClick = { run ->
            viewModel.deleteRun(run)
        }
    )
}
