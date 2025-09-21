package com.losiferreira.watertracker

import android.app.Application
import com.losiferreira.watertracker.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WaterTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@WaterTrackerApplication)
            modules(
                databaseModule,
                repositoryModule,
                domainModule,
                presentationModule
            )
        }
    }
}