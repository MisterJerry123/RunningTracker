package com.misterjerry.runningtracker.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.misterjerry.runningtracker.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun RunScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val isTracking by viewModel.isTracking.collectAsState()
    val pathPoints by viewModel.pathPoints.collectAsState()
    val timeInMillis by viewModel.timeRunInMillis.collectAsState()
    val context = LocalContext.current
    
    // Initialize osmdroid configuration
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }
    }
    
    // Lifecycle observer for MapView
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(pathPoints) {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            val lastPoint = pathPoints.last().last()
            mapView.controller.animateTo(GeoPoint(lastPoint.latitude, lastPoint.longitude))
        }
        
        // Update polylines
        mapView.overlays.clear()
        pathPoints.forEach { pointList ->
            if (pointList.size > 1) {
                val polyline = Polyline().apply {
                    outlinePaint.color = android.graphics.Color.GREEN
                    outlinePaint.strokeWidth = 10f
                    setPoints(pointList.map { GeoPoint(it.latitude, it.longitude) })
                }
                mapView.overlays.add(polyline)
            }
        }
        mapView.invalidate()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(timeInMillis),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        if (isTracking) {
                            viewModel.pauseRun()
                        } else {
                            viewModel.startRun()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isTracking) Color.Yellow else Color.Green,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = if (isTracking) "일시정지" else "계속하기")
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = {
                        viewModel.stopRun(navController.context, null)
                        navController.navigate("home_screen") {
                            popUpTo("home_screen") { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "운동 종료")
                }
            }
        }
    }
}