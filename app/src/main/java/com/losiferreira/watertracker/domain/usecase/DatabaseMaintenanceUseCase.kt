package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Completable
import java.time.LocalDate

class DatabaseMaintenanceUseCase(
    private val repository: WaterEntryRepository
) {
    fun cleanDuplicateZeroEntries(): Completable {
        return repository.removeDuplicateZeroEntries()
    }

    fun updateYesterdayEntry(milliliters: Int): Completable {
        val yesterday = LocalDate.now().minusDays(1)
        return repository.updateMillilitersByDate(yesterday, milliliters)
    }

    fun performMaintenance(yesterdayCorrectMilliliters: Int): Completable {
        return cleanDuplicateZeroEntries()
            .andThen(updateYesterdayEntry(yesterdayCorrectMilliliters))
    }

    fun restoreLostData(): Completable {
        val yesterday = LocalDate.now().minusDays(1)
        val today = LocalDate.now()
        
        val yesterdayEntry = WaterEntry(date = yesterday, milliliters = 2600)
        val todayEntry = WaterEntry(date = today, milliliters = 800)
        
        return repository.saveEntry(yesterdayEntry)
            .andThen(repository.saveEntry(todayEntry))
    }
}