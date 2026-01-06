package com.misterjerry.runningtracker

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.misterjerry.runningtracker.ui.HomeScreen
import com.misterjerry.runningtracker.ui.RunDetailScreen
import com.misterjerry.runningtracker.ui.RunScreen
import com.misterjerry.runningtracker.ui.theme.RunningTrackerTheme
import com.misterjerry.runningtracker.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.misterjerry.runningtracker.util.PermissionUtils

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) {}
        requestPermissions()
        navigateToTrackingFragmentIfNeeded(intent)

        setContent {
            RunningTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = "home_screen") {
                            composable("home_screen") {
                                HomeScreen(navController = navController, viewModel = viewModel)
                            }
                                                    composable("run_screen") {
                                                        RunScreen(navController = navController, viewModel = viewModel)
                                                    }
                                                    composable(
                                                        route = "run_detail_screen/{runId}",
                                                        arguments = listOf(navArgument("runId") { type = NavType.IntType })
                                                    ) { backStackEntry ->
                                                        val runId = backStackEntry.arguments?.getInt("runId") ?: 0
                                                        RunDetailScreen(navController = navController, viewModel = viewModel, runId = runId)
                                                    }
                                                }                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent) // Correctly calling super.onNewIntent
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_TRACKING_FRAGMENT) {
             // Logic to navigate can be handled via Intent flags or a global channel
             // But for now, since we remount UI on create/newIntent,
             // we can rely on state restoration or simpler flows.
             // In Composable properly, we might want to pass this down.
        }
    }

    private fun requestPermissions() {
        if (PermissionUtils.hasLocationPermissions(this)) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            )
        }
    }
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle explicit logic if needed
    }
}