package com.losiferreira.watertracker.domain.usecase

import com.losiferreira.watertracker.domain.model.DateManager
import com.losiferreira.watertracker.domain.model.WaterEntry
import com.losiferreira.watertracker.domain.repository.WaterEntryRepository
import io.reactivex.rxjava3.core.Completable
import java.time.LocalDate

class DailyRolloverUseCase(
    private val repository: WaterEntryRepository,
    private val dateManager: DateManager
) {
    operator fun invoke(lastKnownDate: LocalDate?): Completable {
        val currentDate = dateManager.getCurrentDate()
        
        if (lastKnownDate == null || !dateManager.hasDateChanged(lastKnownDate)) {
            return Completable.complete()
        }

        return repository.getEntryByDate(currentDate)
            .isEmpty
            .flatMapCompletable { isTodayEmpty ->
                if (isTodayEmpty) {
                    Completable.complete()
                } else {
                    repository.getEntryByDate(currentDate)
                        .flatMapCompletable { todayEntry ->
                            val resetEntry = todayEntry.resetToZero()
                            repository.saveEntry(resetEntry)
                        }
                }
            }
    }

    fun checkAndPerformRollover(): Completable {
        val today = dateManager.getCurrentDate()
        val yesterday = dateManager.getYesterday()
        
        return repository.getEntryByDate(yesterday)
            .flatMapCompletable { yesterdayEntry ->
                if (yesterdayEntry.milliliters > 0) {
                    val todayEntry = WaterEntry(date = today, milliliters = 0)
                    repository.saveEntry(todayEntry)
                } else {
                    Completable.complete()
                }
            }
            .onErrorComplete()
    }
}