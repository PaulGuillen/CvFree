package com.devpaul.features.settings

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devpaul.features.settings.ui.SettingsScreen
import com.devpaul.core.platform.locale.AppLanguage
import com.devpaul.core.platform.theme.AppAppearance
import com.devpaul.navigation.route.SettingsRoute
import kotlinx.coroutines.flow.StateFlow

fun NavGraphBuilder.settingsGraph(
    onNavigateToHome: () -> Unit,
    onNavigateToMyCvs: () -> Unit,
    onLogout: () -> Unit,
    userName: StateFlow<String>,
    userEmail: StateFlow<String>,
    onUpdateName: suspend (String) -> String?,
    appLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    appAppearance: AppAppearance,
    onAppearanceSelected: (AppAppearance) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsEnabledChange: (Boolean) -> Unit,
) {
    navigation<SettingsRoute.Graph>(
        startDestination = SettingsRoute.Settings,
    ) {
        composable<SettingsRoute.Settings> {
            val currentUserName by userName.collectAsState()
            val currentUserEmail by userEmail.collectAsState()
            SettingsScreen(
                onNavigateToHome = onNavigateToHome,
                onNavigateToMyCvs = onNavigateToMyCvs,
                onLogout = onLogout,
                userName = currentUserName,
                userEmail = currentUserEmail,
                onUpdateName = onUpdateName,
                appLanguage = appLanguage,
                appAppearance = appAppearance,
                onAppearanceSelected = onAppearanceSelected,
                notificationsEnabled = notificationsEnabled,
                onNotificationsEnabledChange = onNotificationsEnabledChange,
                onLanguageSelected = onLanguageSelected,
            )
        }
    }
}
