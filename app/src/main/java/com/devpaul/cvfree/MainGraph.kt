package com.devpaul.cvfree

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.devpaul.features.auth.authGraph
import com.devpaul.features.home.homeGraph
import com.devpaul.features.onboarding.onboardingGraph
import com.devpaul.features.resume.resumeGraph
import com.devpaul.features.resume.ResumeDraftState
import com.devpaul.core.domain.resume.ResumeRepository
import com.devpaul.features.settings.settingsGraph
import com.devpaul.features.splash.splashGraph
import com.devpaul.navigation.route.AuthRoute
import com.devpaul.navigation.route.HomeRoute
import com.devpaul.navigation.route.OnboardingRoute
import com.devpaul.navigation.route.ResumeRoute
import com.devpaul.navigation.route.SettingsRoute
import com.devpaul.navigation.route.SplashRoute

@Composable
fun MainGraph(
    navController: NavHostController,
    resumeRepository: ResumeRepository,
) {
    val resumeDraftState: ResumeDraftState = viewModel()

    NavHost(
        navController = navController,
        startDestination = SplashRoute.Graph,
    ) {
        splashGraph(
            onFinished = {
                navController.navigate(OnboardingRoute.Graph) {
                    popUpTo<SplashRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        onboardingGraph(
            onFinished = {
                navController.navigate(AuthRoute.Graph) {
                    popUpTo<OnboardingRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
        authGraph(
            onLogin = { _, _ ->
                navController.navigate(HomeRoute.Graph) {
                    popUpTo<AuthRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onRegister = { _, _, _ ->
                navController.navigate(HomeRoute.Graph) {
                    popUpTo<AuthRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onGoogleSignIn = {
                navController.navigate(HomeRoute.Graph) {
                    popUpTo<AuthRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onForgotPassword = {
                // Password recovery will be connected to the auth ViewModel.
            },
            onNavigateToRegister = {
                navController.navigate(AuthRoute.Register) {
                    launchSingleTop = true
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            },
        )
        homeGraph(
            repository = resumeRepository,
            onNavigateToMyCvs = {
                navController.navigateToMainDestination(ResumeRoute.Graph)
            },
            onNavigateToSettings = {
                navController.navigateToMainDestination(SettingsRoute.Graph)
            },
            onNavigateToTemplates = {
                navController.navigate(HomeRoute.Templates)
            },
            onNavigateBack = {
                navController.popBackStack()
            },
            onContinueResume = { resume ->
                resumeDraftState.load(resume)
                navController.navigate(ResumeRoute.SectionSelection)
            },
        )
        resumeGraph(
            navController = navController,
            repository = resumeRepository,
            draftState = resumeDraftState,
            onNavigateToHome = {
                navController.navigateToMainDestination(HomeRoute.Graph)
            },
            onNavigateToSettings = {
                navController.navigateToMainDestination(SettingsRoute.Graph)
            },
        )
        settingsGraph(
            onNavigateToHome = {
                navController.navigateToMainDestination(HomeRoute.Graph)
            },
            onNavigateToMyCvs = {
                navController.navigateToMainDestination(ResumeRoute.Graph)
            },
            onLogout = {
                navController.navigate(AuthRoute.Graph) {
                    popUpTo<HomeRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
        )
    }
}

private fun NavHostController.navigateToMainDestination(route: Any) {
    navigate(route) {
        popUpTo<HomeRoute.Graph> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
