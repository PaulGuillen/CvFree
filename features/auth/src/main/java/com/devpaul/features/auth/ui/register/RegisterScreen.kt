package com.devpaul.features.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.features.auth.R
import com.devpaul.features.auth.ui.components.AccountPrompt
import com.devpaul.features.auth.ui.components.AuthErrorMessage
import com.devpaul.features.auth.ui.components.AuthPanel
import com.devpaul.features.auth.ui.components.AuthTextField
import com.devpaul.features.auth.ui.components.PrimaryAuthButton

@Composable
fun RegisterScreen(
    onRegister: (name: String, email: String, password: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    uiState: RegisterUiState = RegisterUiState(),
    onInputChanged: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
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
                .padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.ui_create_account_11bd80),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ui_get_started_for_free_and_create_your_first_professional_resu_873328),
                modifier = Modifier.widthIn(max = 480.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(28.dp))

            AuthPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                    AuthTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            onInputChanged()
                        },
                        label = stringResource(R.string.ui_full_name_cccece),
                        placeholder = "Elena Vargas",
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                        ),
                    )
                    Spacer(modifier = Modifier.height(18.dp))
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
                    AuthTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            onInputChanged()
                        },
                        label = stringResource(R.string.ui_password_5a6d1c),
                        placeholder = stringResource(R.string.ui_at_least_8_characters_53cf13),
                        isPassword = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                    uiState.errorMessage?.let { error ->
                        Spacer(modifier = Modifier.height(18.dp))
                        AuthErrorMessage(message = stringResource(error))
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    PrimaryAuthButton(
                        text = stringResource(R.string.ui_sign_up_be32fb),
                        onClick = { onRegister(name.trim(), email.trim(), password) },
                        isLoading = uiState.isLoading,
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    AccountPrompt(
                        message = stringResource(R.string.ui_already_have_an_account_65b418),
                        action = stringResource(R.string.ui_sign_in_9c18b1),
                        onClick = onNavigateToLogin,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(name = "Register Light", showBackground = true, heightDp = 850)
@Composable
private fun RegisterLightPreview() {
    CvFreeTheme(darkTheme = false) {
        RegisterScreen(
            onRegister = { _, _, _ -> },
            onNavigateToLogin = {},
        )
    }
}

@Preview(name = "Register Dark", showBackground = true, heightDp = 850)
@Composable
private fun RegisterDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        RegisterScreen(
            onRegister = { _, _, _ -> },
            onNavigateToLogin = {},
        )
    }
}
