package com.devpaul.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

fun NavController.setNavigationResult(key: String, value: Any) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <Route : Any> NavController.setNavigationResult(
    targetRoute: Route,
    key: String,
    value: Any,
) {
    getBackStackEntry(targetRoute).savedStateHandle[key] = value
}

@Composable
inline fun <reified T : Any> NavigationResultEffect(
    backStackEntry: NavBackStackEntry,
    key: String,
    crossinline onResult: (T) -> Unit,
) {
    val resultFlow = remember(backStackEntry, key) {
        backStackEntry.savedStateHandle.getStateFlow<T?>(key, null)
    }
    val result by resultFlow.collectAsState()

    LaunchedEffect(result) {
        result?.let { value ->
            backStackEntry.savedStateHandle.remove<T>(key)
            onResult(value)
        }
    }
}
