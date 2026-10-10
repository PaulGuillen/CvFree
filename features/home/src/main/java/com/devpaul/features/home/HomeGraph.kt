package com.devpaul.features.home

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeRepository
import com.devpaul.features.home.ui.AllTemplatesScreen
import com.devpaul.features.home.ui.HomeScreen
import com.devpaul.navigation.route.HomeRoute
import kotlinx.coroutines.flow.StateFlow

fun NavGraphBuilder.homeGraph(
    repository: ResumeRepository,
    userName: StateFlow<String>,
    onNavigateToMyCvs: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateBack: () -> Unit,
    onContinueResume: (ResumeDocument) -> Unit,
) {
    navigation<HomeRoute.Graph>(
        startDestination = HomeRoute.Home,
    ) {
        composable<HomeRoute.Home> {
            val resumes by repository.resumes.collectAsState(initial = emptyList())
            val currentUserName by userName.collectAsState()
            HomeScreen(
                onNavigateToMyCvs = onNavigateToMyCvs,
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToTemplates = onNavigateToTemplates,
                userName = currentUserName,
                recentResume = resumes.maxByOrNull(ResumeDocument::updatedAt),
                onContinueResume = onContinueResume,
            )
        }
        composable<HomeRoute.Templates> {
            AllTemplatesScreen(onBack = onNavigateBack)
        }
    }
}
