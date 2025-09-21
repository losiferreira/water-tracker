package com.losiferreira.watertracker.presentation.history

import com.losiferreira.watertracker.domain.model.WaterEntry
import java.time.YearMonth

data class HistoryUiState(
    val entries: List<WaterEntry> = emptyList(),
    val selectedMonth: YearMonth? = null,
    val availableMonths: List<YearMonth> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)