package com.devpaul.features.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devpaul.features.settings.ui.SettingsScreen
import com.devpaul.navigation.route.SettingsRoute

fun NavGraphBuilder.settingsGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToMyCvs: () -> Unit,
    onLogout: () -> Unit,
) {
    navigation<SettingsRoute.Graph>(
        startDestination = SettingsRoute.Settings,
    ) {
        composable<SettingsRoute.Settings> {
            SettingsScreen(
                onNavigateToHome = onNavigateToHome,
                onNavigateToMyCvs = onNavigateToMyCvs,
                onLogout = onLogout,
            )
        }
    }
}
