package com.losiferreira.watertracker.di

import androidx.room.Room
import com.losiferreira.watertracker.data.local.database.WaterTrackerDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<WaterTrackerDatabase> {
        Room.databaseBuilder(
            androidContext(),
            WaterTrackerDatabase::class.java,
            WaterTrackerDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    single { get<WaterTrackerDatabase>().waterEntryDao() }
}