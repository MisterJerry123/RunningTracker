package com.misterjerry.runningtracker.presentation.run

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.misterjerry.runningtracker.BuildConfig
import org.koin.androidx.compose.koinViewModel

@Composable
fun RunRoot(
    onFinish: () -> Unit,
    viewModel: RunViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_RUN_FINISH_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            }
        )
    }

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            onFinish()
        }
    }

    RunScreen(
        state = state,
        onStartRunClick = { viewModel.onAction(RunAction.StartRun) },
        onPauseRunClick = { viewModel.onAction(RunAction.PauseRun) },
        onStopRunClick = {
            viewModel.onAction(RunAction.StopRun)

            if (interstitialAd != null) {
                interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        onFinish()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        onFinish()
                    }
                }

                val activity = context.findActivity()
                if (activity != null) {
                    interstitialAd?.show(activity)
                } else {
                    onFinish()
                }
            } else {
                onFinish()
            }
        }
    )
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}