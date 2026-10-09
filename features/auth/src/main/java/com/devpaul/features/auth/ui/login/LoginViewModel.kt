package com.devpaul.features.auth.ui.login

import com.devpaul.core.domain.result.UseCaseResult
import com.devpaul.core.platform.lifecycle.StatefulViewModel
import com.devpaul.features.auth.domain.exception.AuthFailure
import com.devpaul.features.auth.domain.exception.AuthFailureType
import com.devpaul.features.auth.domain.usecase.SignInUseCase
import com.devpaul.features.auth.ui.mapper.toUserMessage
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class LoginViewModel(
    private val signInUseCase: SignInUseCase,
) : StatefulViewModel<LoginUiState, LoginUiIntent, LoginUiEvent>(LoginUiState()) {

    override suspend fun onUiIntent(intent: LoginUiIntent) {
        when (intent) {
            is LoginUiIntent.Login -> login(intent.email, intent.password)
            LoginUiIntent.ClearError -> clearError()
        }
    }

    private suspend fun login(email: String, password: String) {
        if (uiState.value.isLoading) return

        updateUiState { it.copy(isLoading = true, errorMessage = null) }
        when (val result = signInUseCase(email, password)) {
            is UseCaseResult.Success -> {
                updateUiState { LoginUiState() }
                emitUiEvent(LoginUiEvent.Authenticated)
            }

            is UseCaseResult.Error -> handleError(result.error)
        }
    }

    private suspend fun handleError(failure: AuthFailure) {
        val message = failure.error.toUserMessage()
        updateUiState {
            it.copy(
                isLoading = false,
                errorMessage = message.takeIf { failure.type == AuthFailureType.VALIDATION },
            )
        }
        if (failure.type == AuthFailureType.OPERATION) {
            emitUiEvent(LoginUiEvent.ShowSnackbar(message))
        }
    }

    private fun clearError() {
        updateUiState { it.copy(errorMessage = null) }
    }
}
