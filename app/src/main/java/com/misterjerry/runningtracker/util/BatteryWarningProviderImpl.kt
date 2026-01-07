package com.misterjerry.runningtracker.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.Toast

class DevBatteryWarningProvider : BatteryWarningProvider {
    override fun checkAndShowWarning(context: Context) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level != -1 && scale != -1) {
            level * 100 / scale.toFloat()
        } else {
            100f
        }

        if (batteryPct <= 30f) {
            Toast.makeText(context, "배터리 잔량이 30% 이하입니다. 충전이 필요할 수 있습니다.", Toast.LENGTH_LONG).show()
        }
    }
}

class ProdBatteryWarningProvider : BatteryWarningProvider {
    override fun checkAndShowWarning(context: Context) {
        // No-op in production
    }
}
