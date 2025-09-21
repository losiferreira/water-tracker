package com.losiferreira.watertracker.presentation.navigation

sealed class Screen(val route: String) {
    object Tracker : Screen("tracker")
    object History : Screen("history")
}