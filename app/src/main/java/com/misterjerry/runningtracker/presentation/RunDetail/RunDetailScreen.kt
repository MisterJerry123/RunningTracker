package com.misterjerry.runningtracker.presentation.RunDetail

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.misterjerry.runningtracker.BuildConfig
import com.misterjerry.runningtracker.presentation.Home.formatTime
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun RunDetailScreen(
    state: RunDetailState
) {
    val run = state.run
    val context = LocalContext.current

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
                        outlinePaint.color = Color.RED
                        outlinePaint.strokeWidth = 10f
                        setPoints(polylinePoints)
                    }
                    mapView.overlays.add(polyline)
                    allPoints.addAll(polylinePoints)
                }
            }
            
            if (allPoints.isNotEmpty()) {
                // Calculate bounds and zoom to fit
                val bounds = BoundingBox.fromGeoPoints(allPoints)
                // Animate to bounds with padding
                 mapView.zoomToBoundingBox(bounds, true, 100)
            }
            mapView.invalidate()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.surface)
                .zIndex(1f) // Ensure it sits on top if there's any overlap
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        setAdSize(AdSize.BANNER)
                        adUnitId = BuildConfig.ADMOB_RUN_DETAIL_SCREEN_TOP_BANNER_ID
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .zIndex(0f)
        ) {
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