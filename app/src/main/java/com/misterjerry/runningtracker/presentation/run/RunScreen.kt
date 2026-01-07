package com.misterjerry.runningtracker.presentation.run

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.misterjerry.runningtracker.R
import com.misterjerry.runningtracker.presentation.home.formatTime
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun RunScreen(
    state: RunState,
    onStartRunClick: () -> Unit,
    onPauseRunClick: () -> Unit,
    onStopRunClick: (Context) -> Unit
) {
    val context = LocalContext.current

    // Initialize osmdroid configuration with explicit user agent
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    Configuration.getInstance().userAgentValue = context.packageName

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }
    }

    LaunchedEffect(state.initialLocation) {
        state.initialLocation?.let {
            if (state.pathPoints.isEmpty() || state.pathPoints.firstOrNull()?.isEmpty() == true) {
                 mapView.controller.setCenter(it)
            }
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

    val locationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableMyLocation()
            enableFollowLocation()
        }
    }

    LaunchedEffect(Unit) {
        mapView.overlays.add(locationOverlay)
    }

    LaunchedEffect(state.pathPoints) {
        if (state.pathPoints.isNotEmpty() && state.pathPoints.last().isNotEmpty()) {
            val lastPoint = state.pathPoints.last().last()
            mapView.controller.setCenter(GeoPoint(lastPoint.latitude, lastPoint.longitude))
        }

        // Update polylines
        mapView.overlays.clear()

        // 1. Add Polylines first (bottom layer)
        state.pathPoints.forEach { pointList ->
            if (pointList.size > 1) {
                val polyline = Polyline().apply {
                    outlinePaint.color = android.graphics.Color.GREEN
                    outlinePaint.strokeWidth = 10f
                    setPoints(pointList.map { GeoPoint(it.latitude, it.longitude) })
                }
                mapView.overlays.add(polyline)
            }
        }

        // 2. Add Location Overlay last (top layer)
        mapView.overlays.add(locationOverlay)

        mapView.invalidate()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
            
            FloatingActionButton(
                onClick = {
                    if (locationOverlay.myLocation != null) {
                        mapView.controller.animateTo(locationOverlay.myLocation)
                        locationOverlay.enableFollowLocation()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_my_location),
                    contentDescription = "현위치"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(state.timeInMillis),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = {
                        if (state.isTracking) {
                            onPauseRunClick()
                        } else {
                            onStartRunClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isTracking) Color.Yellow else Color.Green,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = if (state.isTracking) "일시정지" else "계속하기")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    onClick = {
                        onStopRunClick(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "운동 종료")
                }
            }
        }
    }
}
