package com.devpaul.cvfree

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.devpaul.features.auth.authGraph
import com.devpaul.features.auth.ui.forgot.ForgotPasswordViewModel
import com.devpaul.features.auth.ui.forgot.ForgotPasswordUiIntent
import com.devpaul.features.auth.ui.login.LoginViewModel
import com.devpaul.features.auth.ui.login.LoginUiIntent
import com.devpaul.features.auth.ui.register.RegisterViewModel
import com.devpaul.features.auth.ui.register.RegisterUiIntent
import com.devpaul.features.auth.ui.session.AuthSessionViewModel
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
    authSessionViewModel: AuthSessionViewModel,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    forgotPasswordViewModel: ForgotPasswordViewModel,
) {
    val resumeDraftState: ResumeDraftState = viewModel()

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
            loginViewModel = loginViewModel,
            registerViewModel = registerViewModel,
            forgotPasswordViewModel = forgotPasswordViewModel,
            onAuthenticated = {
                navController.navigate(HomeRoute.Graph) {
                    popUpTo<AuthRoute.Graph> { inclusive = true }
                    launchSingleTop = true
                }
            },
            onForgotPassword = {
                loginViewModel.executeUiIntent(LoginUiIntent.ClearError)
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
                forgotPasswordViewModel.executeUiIntent(ForgotPasswordUiIntent.ClearError)
                navController.popBackStack()
            },
            onBackToLogin = {
                forgotPasswordViewModel.executeUiIntent(ForgotPasswordUiIntent.ClearError)
                navController.navigate(AuthRoute.Login) {
                    popUpTo<AuthRoute.Graph> { inclusive = false }
                    launchSingleTop = true
                }
            },
            onNavigateToRegister = {
                loginViewModel.executeUiIntent(LoginUiIntent.ClearError)
                navController.navigate(AuthRoute.Register) {
                    launchSingleTop = true
                }
            },
            onNavigateToLogin = {
                registerViewModel.executeUiIntent(RegisterUiIntent.ClearError)
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
