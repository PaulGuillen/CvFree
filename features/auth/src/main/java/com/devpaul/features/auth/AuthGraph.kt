package com.devpaul.features.auth

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.devpaul.features.auth.login.ui.LoginScreen
import com.devpaul.features.auth.register.ui.RegisterScreen
import com.devpaul.navigation.route.AuthRoute

fun NavGraphBuilder.authGraph(
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (name: String, email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    navigation<AuthRoute.Graph>(
        startDestination = AuthRoute.Login,
    ) {
        composable<AuthRoute.Login> {
            LoginScreen(
                onLogin = onLogin,
                onGoogleSignIn = onGoogleSignIn,
                onForgotPassword = onForgotPassword,
                onNavigateToRegister = onNavigateToRegister,
            )
        }
        composable<AuthRoute.Register> {
            RegisterScreen(
                onRegister = onRegister,
                onNavigateToLogin = onNavigateToLogin,
            )
        }
    }
}
