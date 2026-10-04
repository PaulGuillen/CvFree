package com.devpaul.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.component.CvBottomNavigation
import com.devpaul.core.designsystem.component.CvMainDestination
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor
import com.devpaul.features.home.ui.data.featuredTemplates
import com.devpaul.features.home.ui.model.CvTemplateUiModel

@Composable
fun HomeScreen(
    onNavigateToMyCvs: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String = "Paul",
    templates: List<CvTemplateUiModel> = featuredTemplates,
) {
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Scaffold(
        modifier = modifier,
        containerColor = background,
        bottomBar = {
            CvBottomNavigation(
                selectedDestination = CvMainDestination.Home,
                onDestinationSelected = { destination ->
                    when (destination) {
                        CvMainDestination.Home -> Unit
                        CvMainDestination.MyCvs -> onNavigateToMyCvs()
                        CvMainDestination.Settings -> onNavigateToSettings()
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .background(background),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp,
                top = 18.dp,
                end = 20.dp,
                bottom = 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { HomeHeader(userName = userName) }
            item { AchievementBanner() }
            item { FeaturedTemplatesSection(templates = templates) }
            item { RecentActivityCard() }
        }
    }
}

@Composable
private fun HomeHeader(userName: String) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.surface,
                ),
                shadowElevation = 3.dp,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "PG",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CV Free",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Hola, $userName.",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "¿Qué quieres lograr hoy?",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun AchievementBanner() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cuantifica tus logros",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Optimiza tus textos y destaca frente a los reclutadores en segundos.",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.55f),
            )
        }
    }
}

@Composable
private fun FeaturedTemplatesSection(templates: List<CvTemplateUiModel>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Plantillas destacadas",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {}) {
                Text("Ver todas", style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(templates) { template ->
                TemplateCard(template = template)
            }
        }
    }
}

@Composable
private fun TemplateCard(template: CvTemplateUiModel) {
    Column(modifier = Modifier.width(210.dp)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(268.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 4.dp,
        ) {
            ResumeTemplatePreview(template = template)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = template.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = template.description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ResumeTemplatePreview(template: CvTemplateUiModel) {
    Row(modifier = Modifier.padding(16.dp)) {
        if (template.sidebar) {
            Box(
                modifier = Modifier
                    .width(62.dp)
                    .height(236.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(template.accent),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(34.dp)
                        .background(Color.White, CircleShape),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            repeat(3) { section ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (section == 0) 0.62f else 0.48f)
                        .height(7.dp)
                        .background(template.accent, CircleShape),
                )
                Spacer(modifier = Modifier.height(8.dp))
                repeat(3) { line ->
                    Box(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth(if (line == 2) 0.68f else 1f)
                            .height(4.dp)
                            .background(Color(0xFFD5DDE8), CircleShape),
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun RecentActivityCard() {
    Column {
        Text(
            text = "Actividad reciente",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 3.dp,
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                    text = "CV de desarrollador sénior",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Editado hace 2 horas",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "Más opciones",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                Row {
                    Text(
                        text = "Fuerza del perfil",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = "80%",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { 0.8f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Añade “Idiomas” para llegar al 100%",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text("Continuar editando", style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Preview(name = "Home Light", showBackground = true, heightDp = 1200)
@Composable
private fun HomeLightPreview() {
    CvFreeTheme(darkTheme = false) {
        HomeScreen({}, {})
    }
}

@Preview(name = "Home Dark", showBackground = true, heightDp = 1200)
@Composable
private fun HomeDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        HomeScreen({}, {})
    }
}
