package com.devpaul.features.resume.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.component.CvBottomNavigation
import com.devpaul.core.designsystem.component.CvMainDestination
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.features.resume.ui.model.CvFilter
import com.devpaul.features.templates.ResumeTemplateCatalog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyCvsScreen(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCreateCv: () -> Unit,
    resumes: List<ResumeDocument> = emptyList(),
    onEditResume: (ResumeDocument) -> Unit = {},
    onViewResume: (ResumeDocument) -> Unit = {},
) {
    var selectedFilterName by rememberSaveable { mutableStateOf(CvFilter.All.name) }
    val selectedFilter = CvFilter.valueOf(selectedFilterName)
    val visibleResumes = resumes
        .let { filtered ->
            when (selectedFilter) {
                CvFilter.All -> filtered
                CvFilter.Recent -> filtered.sortedByDescending(ResumeDocument::updatedAt)
                CvFilter.Template -> filtered.sortedBy(ResumeDocument::templateName)
            }
        }
    val background = MaterialTheme.colorScheme.background

    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Scaffold(
        modifier = modifier,
        containerColor = background,
        bottomBar = {
            CvBottomNavigation(
                selectedDestination = CvMainDestination.MyCvs,
                onDestinationSelected = { destination ->
                    when (destination) {
                         CvMainDestination.Home -> onNavigateToHome()
                        CvMainDestination.MyCvs -> Unit
                        CvMainDestination.Settings -> onNavigateToSettings()
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateCv,
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Crear currículum",
                    modifier = Modifier.size(34.dp),
                )
            }
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
                bottom = 96.dp,
            ),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "My CVs",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = resumes.size.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CvFilter.entries.forEach { filter ->
                        FilterChip(
                            filter = filter,
                            selected = filter == selectedFilter,
                            onClick = { selectedFilterName = filter.name },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            if (visibleResumes.isEmpty()) {
                item {
                    EmptyCvsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                    )
                }
            } else {
                items(
                    items = visibleResumes,
                    key = ResumeDocument::id,
                ) { resume ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ResumeCard(
                        resume = resume,
                        onEdit = { onEditResume(resume) },
                        onView = { onViewResume(resume) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ResumeCard(
    resume: ResumeDocument,
    onEdit: () -> Unit,
    onView: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 4.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ResumeThumbnail(sidebar = resume.templateName.contains("Sidebar", ignoreCase = true))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = "CV",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = resume.displayName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = ResumeTemplateCatalog.find(resume.templateName).displayName,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "Actualizado ${formatResumeDate(resume.updatedAt)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar")
                }
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ver")
                }
            }
        }
    }
}

@Composable
private fun ResumeThumbnail(sidebar: Boolean) {
    Surface(
        modifier = Modifier.size(width = 96.dp, height = 124.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            if (sidebar) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(androidx.compose.ui.graphics.Color.White)
                    .padding(9.dp),
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (index == 0) 0.55f else 1f)
                            .height(if (index == 0) 8.dp else 4.dp)
                            .background(
                                if (index == 0) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFFCBD5E1),
                                CircleShape,
                            ),
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                }
            }
        }
    }
}

private fun formatResumeDate(value: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(value))

@Composable
private fun FilterChip(
    filter: CvFilter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = if (selected) null else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = filter.label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun EmptyCvsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            modifier = Modifier.size(68.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Aún no tienes currículums",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Crea tu primer CV profesional con el botón +.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "My CVs Light", showBackground = true, heightDp = 860)
@Composable
private fun MyCvsLightPreview() {
    CvFreeTheme(darkTheme = false) {
        MyCvsScreen(
            onNavigateToHome = {},
            onNavigateToSettings = {},
            onCreateCv = {},
        )
    }
}

@Preview(name = "My CVs Dark", showBackground = true, heightDp = 860)
@Composable
private fun MyCvsDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        MyCvsScreen(
            onNavigateToHome = {},
            onNavigateToSettings = {},
            onCreateCv = {},
        )
    }
}
