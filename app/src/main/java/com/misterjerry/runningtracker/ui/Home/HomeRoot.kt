package com.misterjerry.runningtracker.ui.Home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel
import com.misterjerry.runningtracker.domain.model.Run

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
