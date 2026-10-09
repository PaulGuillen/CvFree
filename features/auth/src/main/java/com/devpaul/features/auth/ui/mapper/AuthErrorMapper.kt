package com.devpaul.features.auth.ui.mapper

import com.devpaul.features.auth.domain.exception.AuthError

internal fun AuthError.toUserMessage(): String = when (this) {
    AuthError.INVALID_EMAIL -> "Escribe un correo electrónico válido."
    AuthError.EMPTY_PASSWORD -> "Escribe tu contraseña."
    AuthError.SHORT_PASSWORD -> "La contraseña debe tener al menos 8 caracteres."
    AuthError.INVALID_NAME -> "Escribe un nombre válido."
    AuthError.EMAIL_ALREADY_IN_USE -> "Ya existe una cuenta con este correo."
    AuthError.INVALID_CREDENTIALS -> "El correo o la contraseña son incorrectos."
    AuthError.USER_DISABLED -> "Esta cuenta está deshabilitada."
    AuthError.TOO_MANY_REQUESTS ->
        "Demasiados intentos. Espera unos minutos y vuelve a intentarlo."
    AuthError.NETWORK -> "No se pudo conectar. Revisa tu conexión e inténtalo de nuevo."
    AuthError.UNKNOWN -> "No se pudo completar la operación. Inténtalo de nuevo."
}
