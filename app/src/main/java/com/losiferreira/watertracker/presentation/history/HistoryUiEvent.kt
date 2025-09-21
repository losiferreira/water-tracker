package com.losiferreira.watertracker.presentation.history

import java.time.YearMonth

sealed class HistoryUiEvent {
    object LoadAllEntries : HistoryUiEvent()
    data class FilterByMonth(val yearMonth: YearMonth) : HistoryUiEvent()
}