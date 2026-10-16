package com.devpaul.cvfree

import androidx.compose.ui.platform.LocalResources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.devpaul.core.domain.resume.ResumeRepository
import com.devpaul.core.platform.locale.AppLanguage
import com.devpaul.core.platform.theme.AppAppearance
import com.devpaul.cvfree.access.FeatureAccessController
import com.devpaul.features.auth.authGraph
import com.devpaul.features.auth.ui.session.AuthSessionViewModel
import com.devpaul.features.home.homeGraph
import com.devpaul.features.onboarding.onboardingGraph
import com.devpaul.features.resume.ResumeDraftState
import com.devpaul.features.resume.importing.ResumeImportPolicy
import com.devpaul.features.resume.resumeGraph
import com.devpaul.features.settings.settingsGraph
import com.devpaul.features.splash.splashGraph
import com.devpaul.navigation.route.AuthRoute
import com.devpaul.navigation.route.HomeRoute
import com.devpaul.navigation.route.OnboardingRoute
import com.devpaul.navigation.route.ResumeRoute
import com.devpaul.navigation.route.SettingsRoute
import com.devpaul.navigation.route.SplashRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MainGraph(
    navController: NavHostController,
    appLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    appAppearance: AppAppearance,
    onAppearanceSelected: (AppAppearance) -> Unit,
) {
    val resources = LocalResources.current
    val resumeRepository: ResumeRepository = koinInject()
    val authSessionViewModel: AuthSessionViewModel = koinViewModel()
    val featureAccessController: FeatureAccessController = koinInject()
    val featureAccess by featureAccessController.state.collectAsStateWithLifecycle()
    val resumeDraftState: ResumeDraftState = viewModel()
    val resumeImportPolicy = ResumeImportPolicy(
        aiEnhancementEnabled = featureAccess.aiResumeImportEnabled,
        aiModelName = featureAccess.aiModelName,
    )

    NavHost(
        navController = navController,
        startDestination = SplashRoute.Graph,
    ) {
        splashGraph(
            onFinished = {
                val destination = if (authSessionViewModel.isAuthenticated()) {
                    HomeRoute.Graph
                } else {
                    OnboardingRoute.Graph
                }
                navController.navigate(destination) {
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
            onAuthenticated = {
                authSessionViewModel.refreshUser()
                navController.navigate(HomeRoute.Graph) {
                    popUpTo<AuthRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onForgotPassword = {
                navController.navigate(AuthRoute.ForgotPassword) {
                    launchSingleTop = true
                }
            },
            onPasswordResetSent = { email ->
                navController.navigate(AuthRoute.PasswordResetSent(email)) {
                    popUpTo<AuthRoute.ForgotPassword> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onBackFromForgotPassword = {
                navController.popBackStack()
            },
            onBackToLogin = {
                navController.navigate(AuthRoute.Login) {
                    popUpTo<AuthRoute.Graph> { inclusive = false }
                    launchSingleTop = true
                }
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
            userName = authSessionViewModel.userName,
            onNavigateToMyCvs = {
                navController.navigateToMainDestination(ResumeRoute.Graph)
            },
            onNavigateToSettings = {
                navController.navigateToMainDestination(SettingsRoute.Graph)
            },
            onNavigateToTemplates = {
                navController.navigate(HomeRoute.Templates)
            },
            onPreviewTemplate = { templateId ->
                navController.navigate(HomeRoute.TemplatePreview(templateId))
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
            importPolicy = resumeImportPolicy,
            onNavigateToHome = {
                navController.navigateToMainDestination(HomeRoute.Graph)
            },
            onNavigateToSettings = {
                navController.navigateToMainDestination(SettingsRoute.Graph)
            },
        )
        settingsGraph(
            appAppearance = appAppearance,
            onAppearanceSelected = onAppearanceSelected,
            appLanguage = appLanguage,
            onLanguageSelected = onLanguageSelected,
            userName = authSessionViewModel.userName,
            userEmail = authSessionViewModel.userEmail,
            onUpdateName = { name -> authSessionViewModel.updateUserName(name)?.let(resources::getString) },
            onNavigateToHome = {
                navController.navigateToMainDestination(HomeRoute.Graph)
            },
            onNavigateToMyCvs = {
                navController.navigateToMainDestination(ResumeRoute.Graph)
            },
            onLogout = {
                authSessionViewModel.logout()
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
