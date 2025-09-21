package com.losiferreira.watertracker.domain.model

data class DailyGoal(
    val goalMilliliters: Int = DEFAULT_GOAL_ML
) {
    init {
        require(goalMilliliters > 0) { "Daily goal must be positive" }
    }

    fun getProgressPercentage(currentMilliliters: Int): Float {
        return (currentMilliliters.toFloat() / goalMilliliters.toFloat()).coerceIn(0f, 1f)
    }

    fun isGoalAchieved(currentMilliliters: Int): Boolean {
        return currentMilliliters >= goalMilliliters
    }

    fun getRemainingMilliliters(currentMilliliters: Int): Int {
        return (goalMilliliters - currentMilliliters).coerceAtLeast(0)
    }

    companion object {
        const val DEFAULT_GOAL_ML = 2500
    }
}