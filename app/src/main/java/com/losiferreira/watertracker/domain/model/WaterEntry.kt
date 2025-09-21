package com.losiferreira.watertracker.domain.model

import java.time.LocalDate

data class WaterEntry(
    val id: Long = 0,
    val date: LocalDate,
    val milliliters: Int,
    val goalMilliliters: Int = 2500
) {
    init {
        require(milliliters >= 0) { "Milliliters cannot be negative" }
    }

    fun addWater(amount: Int, currentGoal: Int = goalMilliliters): WaterEntry {
        require(amount >= 0) { "Water amount to add cannot be negative" }
        return copy(
            milliliters = milliliters + amount,
            goalMilliliters = currentGoal
        )
    }

    fun removeWater(amount: Int, currentGoal: Int = goalMilliliters): WaterEntry {
        require(amount >= 0) { "Water amount to remove cannot be negative" }
        val newAmount = (milliliters - amount).coerceAtLeast(0)
        return copy(
            milliliters = newAmount,
            goalMilliliters = currentGoal
        )
    }

    fun resetToZero(currentGoal: Int = goalMilliliters): WaterEntry = copy(
        milliliters = 0,
        goalMilliliters = currentGoal
    )

    fun getProgressPercentage(): Float {
        return if (goalMilliliters > 0) {
            (milliliters.toFloat() / goalMilliliters.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    fun isGoalAchieved(): Boolean {
        return milliliters >= goalMilliliters
    }
}