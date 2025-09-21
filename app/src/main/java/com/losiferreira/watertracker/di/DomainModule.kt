package com.losiferreira.watertracker.di

import com.losiferreira.watertracker.domain.model.DateManager
import com.losiferreira.watertracker.domain.model.DailyGoal
import com.losiferreira.watertracker.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {
    single { DateManager() }
    single { DailyGoal() }
    
    factory { GetTodayWaterEntryUseCase(get(), get()) }
    factory { AddWaterUseCase(get(), get()) }
    factory { RemoveWaterUseCase(get(), get()) }
    factory { GetHistoryUseCase(get()) }
    factory { DailyRolloverUseCase(get(), get()) }
}