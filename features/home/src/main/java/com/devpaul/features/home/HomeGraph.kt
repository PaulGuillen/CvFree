package com.devpaul.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devpaul.features.home.ui.HomeScreen
import com.devpaul.navigation.route.HomeRoute

fun NavGraphBuilder.homeGraph(
    onNavigateToMyCvs: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    navigation<HomeRoute.Graph>(
        startDestination = HomeRoute.Home,
    ) {
        composable<HomeRoute.Home> {
            HomeScreen(
                onNavigateToMyCvs = onNavigateToMyCvs,
                onNavigateToSettings = onNavigateToSettings,
            )
        }
    }
}
