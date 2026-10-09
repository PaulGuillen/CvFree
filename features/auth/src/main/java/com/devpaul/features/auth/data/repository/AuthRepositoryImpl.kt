package com.devpaul.features.auth.data.repository

import com.devpaul.features.auth.data.datasource.mapper.toDomain
import com.devpaul.features.auth.data.datasource.remote.FirebaseAuthDataSource
import com.devpaul.features.auth.domain.entity.AuthUser
import com.devpaul.features.auth.domain.exception.AuthError
import com.devpaul.features.auth.domain.exception.AuthException
import com.devpaul.features.auth.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Factory

@Factory([AuthRepository::class])
class AuthRepositoryImpl(
    private val dataSource: FirebaseAuthDataSource,
) : AuthRepository {

    override fun currentUser(): AuthUser? = dataSource.currentUser()?.toDomain()

    override suspend fun signIn(email: String, password: String): AuthUser = firebaseCall {
        dataSource.signIn(email, password).toDomain()
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
    ): AuthUser = firebaseCall {
        dataSource.register(name, email, password).toDomain(fallbackName = name)
    }

    override suspend fun sendPasswordReset(email: String) {
        firebaseCall {
            dataSource.sendPasswordReset(email)
            Unit
        }
    }

    override fun signOut() = dataSource.signOut()

    private suspend fun <T> firebaseCall(operation: suspend () -> T): T = try {
        operation()
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        throw error.toDomainException()
    }

    private fun Exception.toDomainException(): AuthException = when {
        this is FirebaseNetworkException -> AuthException(AuthError.NETWORK, this)
        this is FirebaseAuthException -> AuthException(
            error = when (errorCode) {
                "ERROR_INVALID_EMAIL" -> AuthError.INVALID_EMAIL
                "ERROR_EMAIL_ALREADY_IN_USE" -> AuthError.EMAIL_ALREADY_IN_USE
                "ERROR_WEAK_PASSWORD" -> AuthError.SHORT_PASSWORD
                "ERROR_USER_DISABLED" -> AuthError.USER_DISABLED
                "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL" ->
                    AuthError.INVALID_CREDENTIALS
                "ERROR_TOO_MANY_REQUESTS" -> AuthError.TOO_MANY_REQUESTS
                else -> AuthError.UNKNOWN
            },
            cause = this,
        )
        else -> AuthException(AuthError.UNKNOWN, this)
    }
}
