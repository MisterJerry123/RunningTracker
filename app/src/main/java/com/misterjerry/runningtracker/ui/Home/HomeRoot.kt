package com.misterjerry.runningtracker.ui.Home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    HomeScreen(
        state = state,
        onStartRunClick = {
            viewModel.startRun()
            navController.navigate("run_screen")
        },
        onRunClick = { id ->
            navController.navigate("run_detail_screen/$id")
        },
        onDeleteRunClick = { run ->
            viewModel.deleteRun(run)
        }
    )
}