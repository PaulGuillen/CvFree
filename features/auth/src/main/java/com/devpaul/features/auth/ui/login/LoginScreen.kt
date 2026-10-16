package com.devpaul.features.auth.ui.login

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.core.platform.theme.LocalAppDarkTheme
import com.devpaul.features.auth.R
import com.devpaul.features.auth.ui.components.AccountPrompt
import com.devpaul.features.auth.ui.components.AuthBrandHeader
import com.devpaul.features.auth.ui.components.AuthErrorMessage
import com.devpaul.features.auth.ui.components.AuthPanel
import com.devpaul.features.auth.ui.components.AuthTextField
import com.devpaul.features.auth.ui.components.PrimaryAuthButton

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onForgotPassword: () -> Unit,
    onNavigateToRegister: () -> Unit,
    uiState: LoginUiState = LoginUiState(),
    onInputChanged: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val background = MaterialTheme.colorScheme.background

    SetStatusBarColor(color = background, darkIcons = !LocalAppDarkTheme.current)

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
                        text = stringResource(R.string.ui_welcome_back_246d60),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            onInputChanged()
                        },
                        label = stringResource(R.string.ui_email_59a167),
                        placeholder = "email@example.com",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = onForgotPassword) {
                            Text(
                                text = stringResource(R.string.ui_forgot_your_password_28ca3f),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                    AuthTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onInputChanged()
                        },
                        label = stringResource(R.string.ui_password_5a6d1c),
                        placeholder = "••••••••",
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                    uiState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(18.dp))
                        AuthErrorMessage(message = stringResource(error))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    PrimaryAuthButton(
                        text = stringResource(R.string.ui_sign_in_9c18b1),
                        onClick = { onLogin(email.trim(), password) },
                        isLoading = uiState.isLoading,
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    AccountPrompt(
                        message = stringResource(R.string.ui_don_t_have_an_account_0cb9ce),
                        action = stringResource(R.string.ui_create_account_11bd80),
                        onClick = onNavigateToRegister,
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SecurityLabel(icon = Icons.Outlined.Lock, text = stringResource(R.string.ui_secure_and_encrypted_ffc3eb))
                SecurityLabel(icon = Icons.Outlined.Language, text = stringResource(R.string.ui_spanish_2001ca))
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
            onForgotPassword = {},
            onNavigateToRegister = {},
        )
    }
}
