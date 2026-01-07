package com.misterjerry.runningtracker

import android.app.Application
import com.misterjerry.runningtracker.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RunningTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@RunningTrackerApp)
            modules(appModule)
        }
    }
}
