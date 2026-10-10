package com.devpaul.features.auth.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devpaul.features.auth.domain.usecase.GetCurrentUserUseCase
import com.devpaul.features.auth.domain.usecase.GetCurrentUserProfileUseCase
import com.devpaul.features.auth.domain.usecase.SignOutUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AuthSessionViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCurrentUserProfileUseCase: GetCurrentUserProfileUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private var loadUserJob: Job? = null

    init {
        refreshUser()
    }

    fun isAuthenticated(): Boolean = getCurrentUserUseCase() != null

    fun refreshUser() {
        loadUserJob?.cancel()
        loadUserJob = viewModelScope.launch {
            _userName.value = try {
                getCurrentUserProfileUseCase()?.name.orEmpty()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                getCurrentUserUseCase()?.name.orEmpty()
            }
        }
    }

    fun logout() {
        loadUserJob?.cancel()
        _userName.value = ""
        signOutUseCase()
    }
}
