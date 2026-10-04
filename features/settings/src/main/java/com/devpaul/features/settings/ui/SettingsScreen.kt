package com.devpaul.features.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.component.CvBottomNavigation
import com.devpaul.core.designsystem.component.CvMainDestination
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor

@Composable
fun SettingsScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToMyCvs: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }
    var autoSaveEnabled by rememberSaveable { mutableStateOf(true) }
    val background = MaterialTheme.colorScheme.background

    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Scaffold(
        modifier = modifier,
        containerColor = background,
        bottomBar = {
            CvBottomNavigation(
                selectedDestination = CvMainDestination.Settings,
                onDestinationSelected = { destination ->
                    when (destination) {
                        CvMainDestination.Home -> onNavigateToHome()
                        CvMainDestination.MyCvs -> onNavigateToMyCvs()
                        CvMainDestination.Settings -> Unit
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 24.dp,
                end = 20.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Text(
                    text = "Ajustes",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineLarge,
                )
            }
            item { ProfileCard() }
            item {
                SettingsSectionTitle(text = "Preferencias")
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                ) {
                    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                        SettingsLinkRow(
                            icon = Icons.Outlined.Language,
                            title = "Idioma",
                            subtitle = "Idioma de la aplicación",
                            value = "Español",
                            onClick = {},
                        )
                        SettingsDivider()
                        SettingsLinkRow(
                            icon = Icons.Outlined.DarkMode,
                            title = "Apariencia",
                            subtitle = "Usar el tema del sistema",
                            value = "Sistema",
                            onClick = {},
                        )
                        SettingsDivider()
                        SettingsSwitchRow(
                            icon = Icons.Outlined.Notifications,
                            title = "Notificaciones",
                            subtitle = "Consejos y recordatorios",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                        )
                        SettingsDivider()
                        SettingsSwitchRow(
                            icon = Icons.Outlined.Done,
                            title = "Guardado automático",
                            subtitle = "Conserva los cambios del CV",
                            checked = autoSaveEnabled,
                            onCheckedChange = { autoSaveEnabled = it },
                        )
                    }
                }
            }
            item {
                SettingsSectionTitle(text = "Cuenta y soporte")
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp,
                ) {
                    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                        SettingsLinkRow(
                            icon = Icons.AutoMirrored.Outlined.HelpOutline,
                            title = "Centro de ayuda",
                            subtitle = "Preguntas frecuentes y soporte",
                            onClick = {},
                        )
                        SettingsDivider()
                        SettingsLinkRow(
                            icon = Icons.Outlined.StarOutline,
                            title = "Calificar CV Free",
                            subtitle = "Cuéntanos qué te parece",
                            onClick = {},
                        )
                        SettingsDivider()
                        SettingsLinkRow(
                            icon = Icons.Outlined.PrivacyTip,
                            title = "Privacidad",
                            subtitle = "Política y tratamiento de datos",
                            onClick = {},
                        )
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.65f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", style = MaterialTheme.typography.labelLarge)
                }
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "CV Free · Versión 1.0.0",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ProfileCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "PG",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Paul Guillen",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "paul@ejemplo.com",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            OutlinedButton(
                onClick = {},
                contentPadding = PaddingValues(horizontal = 14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Text("Editar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
    )
}

@Composable
private fun SettingsLinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    value: String? = null,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsIcon(icon)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (value != null) {
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsIcon(icon)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
private fun SettingsIcon(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 62.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f),
    )
}

@Preview(name = "Settings Light", showBackground = true, heightDp = 900)
@Composable
private fun SettingsLightPreview() {
    CvFreeTheme(darkTheme = false) {
        SettingsScreen({}, {}, {})
    }
}

@Preview(name = "Settings Dark", showBackground = true, heightDp = 900)
@Composable
private fun SettingsDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        SettingsScreen({}, {}, {})
    }
}
