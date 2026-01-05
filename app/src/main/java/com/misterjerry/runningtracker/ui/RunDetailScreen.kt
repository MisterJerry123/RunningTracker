package com.misterjerry.runningtracker.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.misterjerry.runningtracker.MainViewModel
import com.misterjerry.runningtracker.data.Run
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun RunDetailScreen(
    navController: NavController,
    viewModel: MainViewModel,
    runId: Int
) {
    var run by remember { mutableStateOf<Run?>(null) }
    val context = LocalContext.current

    LaunchedEffect(runId) {
        run = viewModel.getRunById(runId)
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    LaunchedEffect(run) {
        run?.let { r ->
            mapView.overlays.clear()
            
            val allPoints = mutableListOf<GeoPoint>()
            r.pathPoints.forEach { polylinePoints ->
                if (polylinePoints.isNotEmpty()) {
                    val polyline = Polyline().apply {
                        outlinePaint.color = android.graphics.Color.RED
                        outlinePaint.strokeWidth = 10f
                        setPoints(polylinePoints)
                    }
                    mapView.overlays.add(polyline)
                    allPoints.addAll(polylinePoints)
                }
            }
            
            if (allPoints.isNotEmpty()) {
                // Calculate bounds and zoom to fit
                val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(allPoints)
                // Animate to bounds with padding
                 mapView.zoomToBoundingBox(bounds, true, 100)
            }
            mapView.invalidate()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        }

        run?.let { r ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Distance: ${r.distanceInMeters}m",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Time: ${formatTime(r.timeInMillis)}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Avg Speed: ${String.format("%.2f", r.avgSpeedInKMH)} km/h",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
