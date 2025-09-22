package com.losiferreira.watertracker.domain.usecase

import android.content.Context
import io.reactivex.rxjava3.core.Completable

class OneTimeDataRestorationUseCase(
    private val context: Context,
    private val databaseMaintenanceUseCase: DatabaseMaintenanceUseCase
) {
    private val prefs = context.getSharedPreferences("water_tracker_maintenance", Context.MODE_PRIVATE)
    private val DATA_RESTORED_KEY = "data_restored_v3"

    fun restoreDataIfNeeded(): Completable {
        return if (!prefs.getBoolean(DATA_RESTORED_KEY, false)) {
            // Data not restored yet, do it now
            databaseMaintenanceUseCase.restoreDataOneTime()
                .doOnComplete {
                    // Mark as completed so it doesn't run again
                    prefs.edit()
                        .putBoolean(DATA_RESTORED_KEY, true)
                        .apply()
                }
        } else {
            // Already restored, do nothing
            Completable.complete()
        }
    }
}