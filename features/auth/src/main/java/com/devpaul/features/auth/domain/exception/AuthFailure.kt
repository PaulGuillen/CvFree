package com.devpaul.features.auth.domain.exception

data class AuthFailure(
    val error: AuthError,
    val type: AuthFailureType,
)

enum class AuthFailureType {
    VALIDATION,
    OPERATION,
}

internal fun Exception.toAuthFailure(): AuthFailure {
    val authError = (this as? AuthException)?.error ?: AuthError.UNKNOWN
    return AuthFailure(
        error = authError,
        type = authError.toFailureType(),
    )
}

private fun AuthError.toFailureType(): AuthFailureType = when (this) {
    AuthError.INVALID_EMAIL,
    AuthError.EMPTY_PASSWORD,
    AuthError.SHORT_PASSWORD,
    AuthError.INVALID_NAME,
    -> AuthFailureType.VALIDATION

    else -> AuthFailureType.OPERATION
}
