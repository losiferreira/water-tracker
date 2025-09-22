package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.data.repository.WaterEntryRepositoryImpl
import io.reactivex.rxjava3.core.Completable
import java.time.LocalDate

class DatabaseMaintenanceUseCase(
    private val repository: WaterEntryRepositoryImpl
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
}