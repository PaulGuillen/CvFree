package com.devpaul.features.auth.data.datasource.mapper

import com.devpaul.features.auth.domain.entity.AuthUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(fallbackName: String = "") = AuthUser(
    userID = uid,
    name = displayName.orEmpty().ifBlank { fallbackName },
    email = email.orEmpty(),
)
