package com.losiferreira.watertracker.presentation.tracker

import com.losiferreira.watertracker.domain.model.DailyGoal

data class TrackerUiState(
    val currentMilliliters: Int = 0,
    val dailyGoal: DailyGoal = DailyGoal(),
    val progressPercentage: Float = 0f,
    val remainingMilliliters: Int = DailyGoal.DEFAULT_GOAL_ML,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isGoalAchieved: Boolean
        get() = dailyGoal.isGoalAchieved(currentMilliliters)
}