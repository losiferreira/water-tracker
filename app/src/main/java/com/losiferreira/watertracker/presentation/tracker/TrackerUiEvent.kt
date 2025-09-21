package com.losiferreira.watertracker.presentation.tracker

sealed class TrackerUiEvent {
    data class AddWater(val milliliters: Int) : TrackerUiEvent()
    data class RemoveWater(val milliliters: Int) : TrackerUiEvent()
    data class AddCustomWater(val milliliters: Int) : TrackerUiEvent()
    data class UpdateGoal(val goalMilliliters: Int) : TrackerUiEvent()
}