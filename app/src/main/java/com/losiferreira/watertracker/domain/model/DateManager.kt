package com.losiferreira.watertracker.domain.model

import java.time.LocalDate

class DateManager {
    fun getCurrentDate(): LocalDate = LocalDate.now()
    
    fun hasDateChanged(lastDate: LocalDate): Boolean {
        return getCurrentDate() != lastDate
    }
    
    fun getYesterday(): LocalDate = getCurrentDate().minusDays(1)
}