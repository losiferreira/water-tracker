package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.domain.model.DateManager
import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Completable
import java.time.LocalDate

class AddWaterUseCase(
    private val repository: WaterEntryRepository,
    private val dateManager: DateManager
) {
    operator fun invoke(milliliters: Int): Completable {
        require(milliliters > 0) { "Water amount must be positive" }
        
        val today = dateManager.getCurrentDate()
        
        return repository.getEntryByDate(today)
            .map { existingEntry ->
                existingEntry.addWater(milliliters)
            }
            .switchIfEmpty(
                io.reactivex.rxjava3.core.Single.just(
                    WaterEntry(date = today, milliliters = milliliters)
                )
            )
            .flatMapCompletable { updatedEntry ->
                repository.saveEntry(updatedEntry)
            }
    }
}