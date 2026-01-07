package com.misterjerry.runningtracker.core.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.misterjerry.runningtracker.data.RunDatabase
import com.misterjerry.runningtracker.data.repository.RunRepositoryImpl
import com.misterjerry.runningtracker.data.repository.TrackingManagerImpl
import com.misterjerry.runningtracker.domain.repository.RunRepository
import com.misterjerry.runningtracker.domain.repository.TrackingManager
import com.misterjerry.runningtracker.domain.usecase.DeleteRunUseCase
import com.misterjerry.runningtracker.domain.usecase.GetLastLocationUseCase
import com.misterjerry.runningtracker.domain.usecase.GetRunByIdUseCase
import com.misterjerry.runningtracker.domain.usecase.GetRunsUseCase
import com.misterjerry.runningtracker.domain.usecase.PauseRunUseCase
import com.misterjerry.runningtracker.domain.usecase.SaveLastLocationUseCase
import com.misterjerry.runningtracker.domain.usecase.SaveRunUseCase
import com.misterjerry.runningtracker.domain.usecase.StartRunUseCase
import com.misterjerry.runningtracker.domain.usecase.StopRunUseCase
import com.misterjerry.runningtracker.presentation.home.HomeViewModel
import com.misterjerry.runningtracker.presentation.run.RunViewModel
import com.misterjerry.runningtracker.presentation.runDetail.RunDetailViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RunDatabase.getDatabase(androidApplication()).getRunDao() }
    single { androidContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE) }
    single { LocationServices.getFusedLocationProviderClient(androidApplication()) }
    
    single<RunRepository> { 
        RunRepositoryImpl(get(), get(), get()) 
    }

    single<TrackingManager> {
        TrackingManagerImpl(androidContext())
    }

    factory { GetRunsUseCase(get()) }
    factory { SaveRunUseCase(get()) }
    factory { DeleteRunUseCase(get()) }
    factory { GetRunByIdUseCase(get()) }
    factory { GetLastLocationUseCase(get()) }
    factory { SaveLastLocationUseCase(get()) }
    factory { StartRunUseCase(get()) }
    factory { PauseRunUseCase(get()) }
    factory { StopRunUseCase(get()) }

//    viewModel {
//        HomeViewModel(
//            androidApplication(),
//            get(),
//            get()
//        )
//    }
    viewModel<HomeViewModel> {
        HomeViewModel(
            get(),
            get(),
            get(),
        )
    }

    viewModel {
        RunViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        RunDetailViewModel(
            get()
        )
    }
}
