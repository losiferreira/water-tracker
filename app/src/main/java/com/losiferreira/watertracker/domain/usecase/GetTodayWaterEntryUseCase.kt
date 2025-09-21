package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.domain.model.DateManager
import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import java.time.LocalDate

class GetTodayWaterEntryUseCase(
    private val repository: WaterEntryRepository,
    private val dateManager: DateManager
) {
    operator fun invoke(): Flowable<WaterEntry> {
        val today = dateManager.getCurrentDate()
        return repository.observeEntryByDate(today)
    }

    fun getSingleEntry(): Single<WaterEntry> {
        val today = dateManager.getCurrentDate()
        return repository.getEntryByDate(today)
            .map { it }
            .switchIfEmpty(Single.just(WaterEntry(date = today, milliliters = 0)))
    }
}