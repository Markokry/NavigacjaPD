package com.example.mapnavigationapp.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mapnavigationapp.view.screens.MapScreenContent
import com.example.mapnavigationapp.view.screens.MarkerManagementScreen
import com.example.mapnavigationapp.viewmodel.MapViewModel

@Composable
fun AppNavigation(viewModel: MapViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "map") {
        composable("map") { MapScreenContent(viewModel, navController) }
        composable(
            "manage_marker/{markerId}",
            arguments = listOf(navArgument("markerId") { defaultValue = -1 })
        ) { backStackEntry ->
            val markerId = backStackEntry.arguments?.getInt("markerId")
            MarkerManagementScreen(
                navController = navController,
                viewModel = viewModel,
                markerId = if (markerId != -1) markerId else null
            )
        }
    }
}
