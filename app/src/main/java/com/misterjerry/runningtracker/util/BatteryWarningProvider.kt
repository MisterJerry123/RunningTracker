package com.misterjerry.runningtracker.util

import android.content.Context

interface BatteryWarningProvider {
    fun checkAndShowWarning(context: Context)
}
