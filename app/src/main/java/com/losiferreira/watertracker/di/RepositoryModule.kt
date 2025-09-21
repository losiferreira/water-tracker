package com.losiferreira.watertracker.di

import com.losiferreira.watertracker.data.repository.WaterEntryRepositoryImpl
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<WaterEntryRepository> { WaterEntryRepositoryImpl(get()) }
}