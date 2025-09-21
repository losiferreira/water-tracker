package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.domain.model.DateManager
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Completable

class RemoveWaterUseCase(
    private val repository: WaterEntryRepository,
    private val dateManager: DateManager
) {
    operator fun invoke(milliliters: Int): Completable {
        require(milliliters > 0) { "Water amount must be positive" }
        
        val today = dateManager.getCurrentDate()
        
        return repository.getEntryByDate(today)
            .flatMapCompletable { existingEntry ->
                if (milliliters >= existingEntry.milliliters) {
                    // Remove completely - create entry with 0ml instead of deleting
                    val zeroEntry = existingEntry.copy(milliliters = 0)
                    repository.saveEntry(zeroEntry)
                } else {
                    // Partial removal
                    val updatedEntry = existingEntry.removeWater(milliliters)
                    repository.saveEntry(updatedEntry)
                }
            }
            .onErrorComplete() // Complete silently if no entry exists
    }
}