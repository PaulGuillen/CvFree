package com.devpaul.features.resume.ui.sections

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.features.resume.ui.components.BuilderTopBar

@Composable
fun SectionSelectionScreen(
    onBack: () -> Unit,
    onConfirm: (Set<ResumeOptionalSection>) -> Unit,
    modifier: Modifier = Modifier,
    initialSections: Set<ResumeOptionalSection> = emptySet(),
) {
    var courses by rememberSaveable(initialSections) {
        mutableStateOf(ResumeOptionalSection.Courses in initialSections)
    }
    var links by rememberSaveable(initialSections) {
        mutableStateOf(ResumeOptionalSection.Links in initialSections)
    }
    var languages by rememberSaveable(initialSections) {
        mutableStateOf(ResumeOptionalSection.Languages in initialSections)
    }
    var references by rememberSaveable(initialSections) {
        mutableStateOf(ResumeOptionalSection.References in initialSections)
    }
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        BuilderTopBar(onBack = onBack, title = "¿Qué secciones quieres incluir?")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Secciones principales")
            Spacer(modifier = Modifier.height(12.dp))
            SectionRow(Icons.Outlined.Contacts, "Información de contacto", true, {}, true)
            SectionRow(Icons.Outlined.WorkOutline, "Experiencia laboral", true, {}, true)
            SectionRow(Icons.Outlined.School, "Educación", true, {}, true)
            SectionRow(Icons.Outlined.Star, "Habilidades", true, {}, true)
            SectionRow(Icons.Outlined.EditNote, "Resumen profesional", true, {}, true)
            Spacer(modifier = Modifier.height(28.dp))
            SectionTitle("Secciones opcionales")
            Spacer(modifier = Modifier.height(12.dp))
            SectionRow(Icons.AutoMirrored.Outlined.MenuBook, "Cursos", courses, { courses = it })
            SectionRow(Icons.Outlined.Link, "Enlaces", links, { links = it })
            SectionRow(Icons.Outlined.Language, "Idiomas", languages, { languages = it })
            SectionRow(Icons.Outlined.Contacts, "Referencias", references, { references = it })
            Spacer(modifier = Modifier.height(20.dp))
        }
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = {
                    courses = ResumeOptionalSection.Courses in initialSections
                    links = ResumeOptionalSection.Links in initialSections
                    languages = ResumeOptionalSection.Languages in initialSections
                    references = ResumeOptionalSection.References in initialSections
                },
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text("Restaurar", style = MaterialTheme.typography.labelLarge)
            }
            Button(
                onClick = {
                    onConfirm(
                        buildSet {
                            if (courses) add(ResumeOptionalSection.Courses)
                            if (links) add(ResumeOptionalSection.Links)
                            if (languages) add(ResumeOptionalSection.Languages)
                            if (references) add(ResumeOptionalSection.References)
                        },
                    )
                },
                modifier = Modifier
                    .weight(1.35f)
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Text("Confirmar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
    )
}

@Composable
private fun SectionRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    required: Boolean = false,
) {
    Surface(
        onClick = { if (!required) onCheckedChange(!checked) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.5.dp,
            if (checked) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.outline,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Checkbox(
                checked = checked,
                onCheckedChange = if (required) null else onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Preview(name = "Sections Light", showBackground = true, heightDp = 1200)
@Composable
private fun SectionsLightPreview() {
    CvFreeTheme(darkTheme = false) { SectionSelectionScreen({}, {}) }
}

@Preview(name = "Sections Dark", showBackground = true, heightDp = 1200)
@Composable
private fun SectionsDarkPreview() {
    CvFreeTheme(darkTheme = true) { SectionSelectionScreen({}, {}) }
}
