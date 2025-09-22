package com.losiferreira.watertracker.di

import com.losiferreira.watertracker.presentation.tracker.TrackerViewModel
import com.losiferreira.watertracker.presentation.history.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { TrackerViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { HistoryViewModel(get()) }
}