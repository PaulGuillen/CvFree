package com.devpaul.features.auth.ui.session

import androidx.lifecycle.ViewModel
import com.devpaul.features.auth.domain.usecase.GetCurrentUserUseCase
import com.devpaul.features.auth.domain.usecase.SignOutUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AuthSessionViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    fun isAuthenticated(): Boolean = getCurrentUserUseCase() != null

    fun logout() = signOutUseCase()
}
