package com.devpaul.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.devpaul.core.designsystem.component.mvi.BaseScreenWithState
import com.devpaul.features.auth.ui.forgot.ForgotPasswordScreen
import com.devpaul.features.auth.ui.forgot.ForgotPasswordUiEvent
import com.devpaul.features.auth.ui.forgot.ForgotPasswordUiIntent
import com.devpaul.features.auth.ui.forgot.ForgotPasswordViewModel
import com.devpaul.features.auth.ui.forgot.PasswordResetSentScreen
import com.devpaul.features.auth.ui.login.LoginScreen
import com.devpaul.features.auth.ui.login.LoginUiEvent
import com.devpaul.features.auth.ui.login.LoginUiIntent
import com.devpaul.features.auth.ui.login.LoginViewModel
import com.devpaul.features.auth.ui.register.RegisterScreen
import com.devpaul.features.auth.ui.register.RegisterUiEvent
import com.devpaul.features.auth.ui.register.RegisterUiIntent
import com.devpaul.features.auth.ui.register.RegisterViewModel
import com.devpaul.navigation.route.AuthRoute

fun NavGraphBuilder.authGraph(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    forgotPasswordViewModel: ForgotPasswordViewModel,
    onAuthenticated: () -> Unit,
    onForgotPassword: () -> Unit,
    onPasswordResetSent: (email: String) -> Unit,
    onBackFromForgotPassword: () -> Unit,
    onBackToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    navigation<AuthRoute.Graph>(
        startDestination = AuthRoute.Login,
    ) {
        composable<AuthRoute.Login> {
            BaseScreenWithState(
                viewModel = loginViewModel,
                onUiEvent = { event, showSnackbar ->
                    when (event) {
                        LoginUiEvent.Authenticated -> onAuthenticated()
                        is LoginUiEvent.ShowSnackbar -> showSnackbar(event.message)
                    }
                },
            ) { uiState, onIntent ->
                LoginScreen(
                    onLogin = { email, password ->
                        onIntent(LoginUiIntent.Login(email, password))
                    },
                    onForgotPassword = onForgotPassword,
                    onNavigateToRegister = onNavigateToRegister,
                    uiState = uiState,
                    onInputChanged = { onIntent(LoginUiIntent.ClearError) },
                )
            }
        }
        composable<AuthRoute.Register> {
            BaseScreenWithState(
                viewModel = registerViewModel,
                onUiEvent = { event, showSnackbar ->
                    when (event) {
                        RegisterUiEvent.Authenticated -> onAuthenticated()
                        is RegisterUiEvent.ShowSnackbar -> showSnackbar(event.message)
                    }
                },
            ) { uiState, onIntent ->
                RegisterScreen(
                    onRegister = { name, email, password ->
                        onIntent(RegisterUiIntent.Register(name, email, password))
                    },
                    onNavigateToLogin = onNavigateToLogin,
                    uiState = uiState,
                    onInputChanged = { onIntent(RegisterUiIntent.ClearError) },
                )
            }
        }
        composable<AuthRoute.ForgotPassword> {
            BaseScreenWithState(
                viewModel = forgotPasswordViewModel,
                onUiEvent = { event, showSnackbar ->
                    when (event) {
                        is ForgotPasswordUiEvent.PasswordResetSent ->
                            onPasswordResetSent(event.email)
                        is ForgotPasswordUiEvent.ShowSnackbar -> showSnackbar(event.message)
                    }
                },
            ) { uiState, onIntent ->
                ForgotPasswordScreen(
                    uiState = uiState,
                    onSend = { email -> onIntent(ForgotPasswordUiIntent.Send(email)) },
                    onInputChanged = { onIntent(ForgotPasswordUiIntent.ClearError) },
                    onNavigateBack = onBackFromForgotPassword,
                )
            }
        }
        composable<AuthRoute.PasswordResetSent> { backStackEntry ->
            val route = backStackEntry.toRoute<AuthRoute.PasswordResetSent>()
            PasswordResetSentScreen(
                email = route.email,
                onBackToLogin = onBackToLogin,
            )
        }
    }
}
