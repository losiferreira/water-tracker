package com.losiferreira.watertracker.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.losiferreira.watertracker.presentation.history.HistoryScreen
import com.losiferreira.watertracker.presentation.tracker.TrackerScreen

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Tracker.route,
        title = "Tracker",
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = Screen.History.route,
        title = "History",
        icon = Icons.Filled.DateRange
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackerApp() {
    val navController = rememberNavController()
    
    Scaffold(
        containerColor = Color.Black,
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Tracker.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Tracker.route) {
                TrackerScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
        }
    }
}

@Composable
fun BottomNavigation(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color(0xFF1A1A1A),
        contentColor = Color.White
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                icon = { 
                    Icon(
                        item.icon, 
                        contentDescription = item.title,
                        tint = if (isSelected) Color(0xFF00BCD4) else Color(0xFF666666)
                    ) 
                },
                label = { 
                    Text(
                        item.title,
                        color = if (isSelected) Color(0xFF00BCD4) else Color(0xFF666666)
                    ) 
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF00BCD4),
                    selectedTextColor = Color(0xFF00BCD4),
                    unselectedIconColor = Color(0xFF666666),
                    unselectedTextColor = Color(0xFF666666),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}