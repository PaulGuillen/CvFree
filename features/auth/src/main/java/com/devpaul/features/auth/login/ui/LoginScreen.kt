package com.devpaul.features.auth.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor
import com.devpaul.features.auth.components.AccountPrompt
import com.devpaul.features.auth.components.AuthBrandHeader
import com.devpaul.features.auth.components.AuthPanel
import com.devpaul.features.auth.components.AuthTextField
import com.devpaul.features.auth.components.GoogleSignInButton
import com.devpaul.features.auth.components.OrDivider
import com.devpaul.features.auth.components.PrimaryAuthButton

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val background = MaterialTheme.colorScheme.background

    SetStatusBarColor(color = background, darkIcons = !isSystemInDarkTheme())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 620.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AuthBrandHeader()
            Spacer(modifier = Modifier.height(24.dp))

            AuthPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                    Text(
                        text = "¡Bienvenido de nuevo!",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Correo electrónico",
                        placeholder = "ejemplo@correo.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Contraseña",
                        placeholder = "••••••••",
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryAuthButton(
                        text = "Iniciar sesión",
                        onClick = { onLogin(email.trim(), password) },
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    OrDivider()
                    Spacer(modifier = Modifier.height(20.dp))
                    GoogleSignInButton(onClick = onGoogleSignIn)
                    Spacer(modifier = Modifier.height(20.dp))
                    AccountPrompt(
                        message = "¿No tienes una cuenta?",
                        action = "Crear cuenta",
                        onClick = onNavigateToRegister,
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SecurityLabel(icon = Icons.Outlined.Lock, text = "Seguro y encriptado")
                SecurityLabel(icon = Icons.Outlined.Language, text = "Español")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SecurityLabel(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Preview(name = "Login Light", showBackground = true, heightDp = 940)
@Composable
private fun LoginLightPreview() {
    CvFreeTheme(darkTheme = false) {
        LoginScreen(
            onLogin = { _, _ -> },
            onGoogleSignIn = {},
            onForgotPassword = {},
            onNavigateToRegister = {},
        )
    }
}

@Preview(name = "Login Dark", showBackground = true, heightDp = 940)
@Composable
private fun LoginDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        LoginScreen(
            onLogin = { _, _ -> },
            onGoogleSignIn = {},
            onForgotPassword = {},
            onNavigateToRegister = {},
        )
    }
}
