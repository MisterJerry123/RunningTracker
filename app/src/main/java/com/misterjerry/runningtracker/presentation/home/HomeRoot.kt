package com.misterjerry.runningtracker.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.misterjerry.runningtracker.util.BatteryWarningProvider
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeRoot(
    onStartRunClick: () -> Unit,
    onRunClick: (Int) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    batteryWarningProvider: BatteryWarningProvider = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    HomeScreen(
        state = state,
        onStartRunClick = {
            batteryWarningProvider.checkAndShowWarning(context)
            viewModel.onAction(HomeAction.StartRun)
            onStartRunClick()
        },
        onRunClick = onRunClick,
        onDeleteRunClick = { run ->
            viewModel.onAction(HomeAction.DeleteRun(run))
        }
    )
}
