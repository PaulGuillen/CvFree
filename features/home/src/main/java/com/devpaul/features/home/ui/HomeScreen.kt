package com.devpaul.features.home.ui

import com.devpaul.core.platform.analytics.trackedClick
import com.devpaul.core.platform.analytics.AppAnalytics
 import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.component.CvBottomNavigation
import com.devpaul.core.designsystem.component.CvMainDestination
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.profile.UserProfileUiState
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.core.platform.theme.LocalAppDarkTheme
import com.devpaul.features.home.R
import com.devpaul.features.home.ui.model.resumeProgress
import com.devpaul.features.home.ui.util.formatRecentEdit
import com.devpaul.features.home.ui.util.localizedDescription
import com.devpaul.features.home.ui.util.localizedMessage
import com.devpaul.features.home.ui.util.localizedName
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateDefinition
import com.devpaul.features.templates.ui.TemplateThumbnail

@Composable
fun HomeScreen(
    onNavigateToMyCvs: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onPreviewTemplate: (String) -> Unit,
    onContinueResume: (ResumeDocument) -> Unit,
    modifier: Modifier = Modifier,
    userProfile: UserProfileUiState = UserProfileUiState(isLoading = false),
    templates: List<ResumeTemplateDefinition> = ResumeTemplateCatalog.templates.take(3),
    recentResume: ResumeDocument? = null,
    onNavigateToNotifications: () -> Unit = {},
) {
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !LocalAppDarkTheme.current)

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
            item { HomeHeader(profileState = userProfile, onNotifications = onNavigateToNotifications) }
            item { AchievementBanner() }
            item {
                FeaturedTemplatesSection(
                    templates = templates,
                    onViewAll = onNavigateToTemplates,
                    onPreviewTemplate = onPreviewTemplate,
                )
            }
            item {
                RecentActivitySection(
                    resume = recentResume,
                    onContinue = onContinueResume,
                    onNavigateToMyCvs = onNavigateToMyCvs,
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(profileState: UserProfileUiState, onNotifications: () -> Unit) {
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
                    when {
                        profileState.isLoading -> ProfileSkeleton(Modifier.fillMaxSize())
                        profileState.initials.isNotEmpty() -> Text(
                            text = profileState.initials,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        else -> Icon(
                            Icons.Outlined.PersonOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CV Free",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = trackedClick("open_notifications", onNotifications)) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.home_notifications),
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        if (profileState.isLoading) {
            val loadingDescription = stringResource(R.string.home_profile_loading)
            ProfileSkeleton(
                Modifier.fillMaxWidth(0.8f).height(44.dp)
                    .semantics {
                        contentDescription = loadingDescription
                        progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
                    },
            )
        } else {
            Text(
                text = profileState.name.trim().takeIf(String::isNotEmpty)?.let {
                    stringResource(R.string.home_greeting_name, it)
                } ?: stringResource(R.string.home_greeting),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.home_question),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ProfileSkeleton(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "profile_loading")
    val alpha by transition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(850), RepeatMode.Reverse),
        label = "profile_loading_alpha",
    )
    Box(modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)))
}

@Preview(name = "Home Profile Loading", showBackground = true)
@Composable
private fun HomeProfileLoadingPreview() {
    CvFreeTheme {
        HomeScreen({}, {}, {}, {}, {}, userProfile = UserProfileUiState())
    }
}

@Preview(name = "Home Firebase Profile", showBackground = true)
@Composable
private fun HomeFirebaseProfilePreview() {
    CvFreeTheme {
        HomeScreen({}, {}, {}, {}, {}, userProfile = UserProfileUiState(isLoading = false, name = "Elena Vargas"))
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
                    text = stringResource(R.string.home_achievement_title),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.home_achievement_description),
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
private fun FeaturedTemplatesSection(
    templates: List<ResumeTemplateDefinition>,
    onViewAll: () -> Unit,
    onPreviewTemplate: (String) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_featured_templates),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = trackedClick("view_all_templates", onViewAll)) {
                Text(stringResource(R.string.home_view_all), style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(templates) { template ->
                TemplateCard(
                    template = template,
                    onClick = trackedClick("preview_template") {
                        AppAnalytics.selection("preview_template", template.id)
                        onPreviewTemplate(template.id)
                    },
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: ResumeTemplateDefinition,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(210.dp)
            .clickable(onClick = onClick),
    ) {
        TemplateThumbnail(
            template = template,
            modifier = Modifier
                .fillMaxWidth()
                .height(268.dp),
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 4.dp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = template.localizedName(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = template.localizedDescription(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun RecentActivitySection(
    resume: ResumeDocument?,
    onContinue: (ResumeDocument) -> Unit,
    onNavigateToMyCvs: () -> Unit,
) {
    Column {
        Text(
            text = stringResource(R.string.home_recent_activity),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (resume == null) {
            EmptyRecentActivityCard(onNavigateToMyCvs = onNavigateToMyCvs)
        } else {
            RecentResumeCard(
                resume = resume,
                onContinue = { onContinue(resume) },
                onNavigateToMyCvs = onNavigateToMyCvs,
            )
        }
    }
}

@Composable
private fun RecentResumeCard(
    resume: ResumeDocument,
    onContinue: () -> Unit,
    onNavigateToMyCvs: () -> Unit,
) {
    var showActions by remember { mutableStateOf(false) }
    val progress = resumeProgress(resume)
    val context = LocalContext.current

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
                        text = resume.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = formatRecentEdit(context, resume.updatedAt),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                    )
                }
                Box {
                    IconButton(onClick = trackedClick("open_recent_resume_menu") { showActions = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.home_more_options),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    DropdownMenu(
                        expanded = showActions,
                        onDismissRequest = { showActions = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.home_continue_editing)) },
                            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                            onClick = trackedClick("continue_recent_resume") {
                                showActions = false
                                onContinue()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.home_view_my_resumes)) },
                            leadingIcon = { Icon(Icons.Outlined.FolderOpen, contentDescription = null) },
                            onClick = trackedClick("view_recent_resume") {
                                showActions = false
                                onNavigateToMyCvs()
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Row {
                Text(
                    text = stringResource(R.string.home_profile_strength),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = "${progress.percent}%",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.percent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = progress.localizedMessage(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = trackedClick("continue_recent_resume", onContinue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Text(stringResource(R.string.home_continue_editing), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(10.dp))
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun EmptyRecentActivityCard(onNavigateToMyCvs: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.home_no_activity),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.home_no_activity_description),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(onClick = trackedClick("open_my_cvs", onNavigateToMyCvs)) {
                Text(stringResource(R.string.home_go_to_my_resumes))
            }
        }
    }
}

@Preview(name = "Home Light", showBackground = true, heightDp = 1200)
@Composable
private fun HomeLightPreview() {
    CvFreeTheme(darkTheme = false) {
        HomeScreen({}, {}, {}, {}, {})
    }
}

@Preview(name = "Home Dark", showBackground = true, heightDp = 1200)
@Composable
private fun HomeDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        HomeScreen({}, {}, {}, {}, {})
    }
}
