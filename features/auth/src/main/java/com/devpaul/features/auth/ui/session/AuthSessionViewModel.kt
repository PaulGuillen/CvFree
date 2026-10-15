package com.devpaul.features.auth.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devpaul.core.domain.result.UseCaseResult
import com.devpaul.features.auth.domain.usecase.GetCurrentUserUseCase
import com.devpaul.features.auth.domain.usecase.GetCurrentUserProfileUseCase
import com.devpaul.features.auth.domain.usecase.SignOutUseCase
import com.devpaul.features.auth.domain.usecase.UpdateUserNameUseCase
import com.devpaul.features.auth.ui.mapper.toMessageResource
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
    private val updateUserNameUseCase: UpdateUserNameUseCase,
) : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()
    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

    private var loadUserJob: Job? = null

    init {
        refreshUser()
    }

    fun isAuthenticated(): Boolean = getCurrentUserUseCase() != null

    fun refreshUser() {
        loadUserJob?.cancel()
        loadUserJob = viewModelScope.launch {
            val user = try {
                getCurrentUserProfileUseCase()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                getCurrentUserUseCase()
            }
            _userName.value = user?.name.orEmpty()
            _userEmail.value = user?.email.orEmpty()
        }
    }

    suspend fun updateUserName(name: String): Int? {
        loadUserJob?.cancel()
        return when (val result = updateUserNameUseCase(name)) {
            is UseCaseResult.Success -> {
                _userName.value = result.value.name
                _userEmail.value = result.value.email
                null
            }
            is UseCaseResult.Error -> result.error.error.toMessageResource()
        }
    }

    fun logout() {
        loadUserJob?.cancel()
        _userName.value = ""
        _userEmail.value = ""
        signOutUseCase()
    }
}
