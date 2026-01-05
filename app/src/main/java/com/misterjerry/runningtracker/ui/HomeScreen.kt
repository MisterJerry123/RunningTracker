package com.misterjerry.runningtracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.misterjerry.runningtracker.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("운동하기", "기록")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> ExerciseTab(navController, viewModel)
            1 -> HistoryTab(viewModel)
        }
    }
}

@Composable
fun ExerciseTab(navController: NavController, viewModel: MainViewModel) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewModel.startRun()
                navController.navigate("run_screen")
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = "운동 시작")
        }
    }
}

@Composable
fun HistoryTab(viewModel: MainViewModel) {
    val runs by viewModel.runs.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(runs) { run ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "날짜: ${dateFormat.format(Date(run.timestamp))}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "시간: ${formatTime(run.timeInMillis)}")
                    Text(text = "거리: ${run.distanceInMeters}m")
                    Text(text = "평균 속도: ${String.format("%.1f", run.avgSpeedInKMH)} km/h")
                }
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    val hours = (ms / (1000 * 60 * 60))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
