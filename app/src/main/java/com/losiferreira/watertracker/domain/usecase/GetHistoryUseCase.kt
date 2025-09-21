package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class GetHistoryUseCase(
    private val repository: WaterEntryRepository
) {
    operator fun invoke(): Single<List<WaterEntry>> {
        return repository.getAllEntries()
    }

    fun observeAllEntries(): Flowable<List<WaterEntry>> {
        return repository.observeAllEntries()
    }

    fun getByMonth(yearMonth: String): Single<List<WaterEntry>> {
        return repository.getEntriesByMonth(yearMonth)
    }

    fun observeByMonth(yearMonth: String): Flowable<List<WaterEntry>> {
        return repository.observeEntriesByMonth(yearMonth)
    }
}