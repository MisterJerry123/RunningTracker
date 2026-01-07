package com.misterjerry.runningtracker.presentation.Home

import android.content.Context
import android.location.LocationManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.misterjerry.runningtracker.BuildConfig
import com.misterjerry.runningtracker.domain.model.Run
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    state: HomeState,
    onStartRunClick: () -> Unit,
    onRunClick: (Int) -> Unit,
    onDeleteRunClick: (Run) -> Unit
) {
    val tabs = listOf("운동하기", "기록")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> ExerciseTab(onStartRunClick = onStartRunClick)
                    1 -> HistoryTab(
                        state = state,
                        onRunClick = onRunClick,
                        onDeleteRunClick = onDeleteRunClick
                    )
                }
            }
        }
        
        AndroidView(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface),
            factory = { context ->
                AdView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setAdSize(AdSize.BANNER)
                    adUnitId = BuildConfig.ADMOB_RUN_SCREEN_BOTTOM_BANNER_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Composable
fun ExerciseTab(onStartRunClick: () -> Unit) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                
                if (isGpsEnabled || isNetworkEnabled) {
                    onStartRunClick()
                } else {
                    Toast.makeText(context, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text(text = "운동 시작")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryTab(
    state: HomeState,
    onRunClick: (Int) -> Unit,
    onDeleteRunClick: (Run) -> Unit
) {
    val runs = state.runs
    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    var runToDelete by remember { mutableStateOf<Run?>(null) }

    if (runToDelete != null) {
        AlertDialog(
            onDismissRequest = { runToDelete = null },
            title = { Text("기록 삭제") },
            text = { Text("이 운동 기록을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        runToDelete?.let { onDeleteRunClick(it) }
                        runToDelete = null
                    }
                ) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { runToDelete = null }) {
                    Text("취소")
                }
            }
        )
    }

    if (runs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "운동 기록이 없습니다.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(runs) { run ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .combinedClickable(
                            onClick = { onRunClick(run.id) },
                            onLongClick = { runToDelete = run }
                        ),
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
}

fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    val hours = (ms / (1000 * 60 * 60))
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}