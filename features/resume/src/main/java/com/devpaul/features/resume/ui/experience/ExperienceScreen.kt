package com.devpaul.features.resume.ui.experience

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeExperience
import com.devpaul.core.domain.resume.ResumeTextFormatting
import com.devpaul.core.domain.resume.newResumeEntryId
import com.devpaul.features.resume.ui.components.BuilderAddButton
import com.devpaul.features.resume.ui.components.BuilderEditorActions
import com.devpaul.features.resume.ui.components.BuilderNewEntryCard
import com.devpaul.features.resume.ui.components.BuilderPage
import com.devpaul.features.resume.ui.components.BuilderSavedEntryCard
import com.devpaul.features.resume.ui.components.BuilderTextField
import com.devpaul.features.resume.ui.components.NewEntryId
import com.devpaul.features.resume.ui.components.isValidYearOrBlank
import com.devpaul.features.resume.ui.components.replaceById
import com.devpaul.features.resume.ui.summary.RichTextEditor
import com.devpaul.features.resume.ui.summary.RichTextEditorState

@Composable
fun ExperienceScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    totalSteps: Int = 5,
    experiences: List<ResumeExperience> = emptyList(),
    onExperiencesChange: (List<ResumeExperience>) -> Unit = {},
) {
    var editingId by rememberSaveable { mutableStateOf(if (experiences.isEmpty()) NewEntryId else null) }
    var position by rememberSaveable { mutableStateOf("") }
    var company by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var startYear by rememberSaveable { mutableStateOf("") }
    var endYear by rememberSaveable { mutableStateOf("") }
    var isCurrent by rememberSaveable { mutableStateOf(false) }
    var achievementsSeed by remember { mutableStateOf("") }
    var formattingSeed by remember { mutableStateOf(ResumeTextFormatting()) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    val achievementsEditor = remember(editingId, achievementsSeed, formattingSeed) {
        RichTextEditorState(achievementsSeed, formattingSeed)
    }

    fun resetEditor() {
        position = ""
        company = ""
        city = ""
        startYear = ""
        endYear = ""
        isCurrent = false
        achievementsSeed = ""
        formattingSeed = ResumeTextFormatting()
        showErrors = false
    }

    fun startNew() {
        resetEditor()
        editingId = NewEntryId
    }

    fun startEditing(item: ResumeExperience) {
        position = item.position
        company = item.company
        city = item.city
        startYear = item.startYear
        endYear = item.endYear.takeUnless { item.isOngoing }.orEmpty()
        isCurrent = item.isOngoing
        achievementsSeed = item.achievements
        formattingSeed = item.achievementsFormatting
        showErrors = false
        editingId = item.id
    }

    fun hasPendingInput() = listOf(
        position,
        company,
        city,
        startYear,
        endYear,
        achievementsEditor.value.text,
    ).any(String::isNotBlank) || isCurrent

    fun saveEditor(): Boolean {
        showErrors = true
        if (position.isBlank() || company.isBlank() ||
            !startYear.isValidYearOrBlank() || (!isCurrent && !endYear.isValidYearOrBlank())
        ) {
            return false
        }
        val currentId = editingId
        val value = ResumeExperience(
            position = position.trim(),
            company = company.trim(),
            city = city.trim(),
            startYear = startYear.trim(),
            endYear = endYear.trim().takeUnless { isCurrent }.orEmpty(),
            isCurrent = isCurrent,
            achievements = achievementsEditor.value.text.trim(),
            achievementsFormatting = achievementsEditor.formatting,
            id = currentId?.takeUnless { it == NewEntryId } ?: newResumeEntryId(),
        )
        onExperiencesChange(
            if (currentId == NewEntryId) listOf(value) + experiences
            else experiences.replaceById(value.id, ResumeExperience::id, value),
        )
        editingId = null
        resetEditor()
        return true
    }

    fun continueFlow() {
        when {
            editingId == null -> onContinue()
            editingId == NewEntryId && !hasPendingInput() -> {
                editingId = null
                onContinue()
            }
            saveEditor() -> onContinue()
        }
    }

    BuilderPage(
        onBack = onBack,
        step = 2,
        totalSteps = totalSteps,
        stepName = "Experiencia",
        title = "Cuéntanos sobre tu experiencia laboral",
        description = "Agrega tus empleos anteriores para mostrar tu trayectoria.",
        actionText = "Continuar a Educación →",
        onContinue = ::continueFlow,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (editingId == NewEntryId) {
                BuilderNewEntryCard(
                    title = "Nueva experiencia",
                    bringIntoView = experiences.isNotEmpty(),
                ) {
                    Spacer(Modifier.height(16.dp))
                    ExperienceEditor(
                        position = position,
                        onPositionChange = { position = it },
                        company = company,
                        onCompanyChange = { company = it },
                        city = city,
                        onCityChange = { city = it },
                        startYear = startYear,
                        onStartYearChange = { startYear = it.filter(Char::isDigit) },
                        endYear = endYear,
                        onEndYearChange = { endYear = it.filter(Char::isDigit) },
                        isCurrent = isCurrent,
                        onIsCurrentChange = { isCurrent = it },
                        achievementsEditor = achievementsEditor,
                        showErrors = showErrors,
                        onCancel = { editingId = null; resetEditor() },
                        onSave = ::saveEditor,
                    )
                }
            }

            experiences.forEach { item ->
                val expanded = editingId == item.id
                BuilderSavedEntryCard(
                    title = item.position,
                    subtitle = item.company,
                    supportingText = listOf(item.city, dateRange(item.startYear, item.displayEndYear))
                        .filter(String::isNotBlank)
                        .joinToString(" · "),
                    expanded = expanded,
                    onEdit = {
                        if (expanded) editingId = null else startEditing(item)
                    },
                    onDelete = {
                        onExperiencesChange(experiences.filterNot { it.id == item.id })
                        if (expanded) {
                            editingId = null
                            resetEditor()
                        }
                    },
                    editor = if (expanded) {
                        {
                            ExperienceEditor(
                                position = position,
                                onPositionChange = { position = it },
                                company = company,
                                onCompanyChange = { company = it },
                                city = city,
                                onCityChange = { city = it },
                                startYear = startYear,
                                onStartYearChange = { startYear = it.filter(Char::isDigit) },
                                endYear = endYear,
                                onEndYearChange = { endYear = it.filter(Char::isDigit) },
                                isCurrent = isCurrent,
                                onIsCurrentChange = { isCurrent = it },
                                achievementsEditor = achievementsEditor,
                                showErrors = showErrors,
                                onCancel = { editingId = null; resetEditor() },
                                onSave = ::saveEditor,
                            )
                        }
                    } else null,
                )
            }

            if (editingId == null) {
                BuilderAddButton(text = "Añadir empleo", onClick = ::startNew)
            }
        }
    }
}

@Composable
private fun ExperienceEditor(
    position: String,
    onPositionChange: (String) -> Unit,
    company: String,
    onCompanyChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    startYear: String,
    onStartYearChange: (String) -> Unit,
    endYear: String,
    onEndYearChange: (String) -> Unit,
    isCurrent: Boolean,
    onIsCurrentChange: (Boolean) -> Unit,
    achievementsEditor: RichTextEditorState,
    showErrors: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    BuilderTextField(
        value = position,
        onValueChange = onPositionChange,
        label = "Puesto",
        placeholder = "Ej. Desarrollador Android",
        isError = showErrors && position.isBlank(),
        supportingText = if (showErrors && position.isBlank()) "El puesto es obligatorio" else null,
    )
    Spacer(Modifier.height(16.dp))
    BuilderTextField(
        value = company,
        onValueChange = onCompanyChange,
        label = "Empresa",
        placeholder = "Nombre de la empresa",
        isError = showErrors && company.isBlank(),
        supportingText = if (showErrors && company.isBlank()) "La empresa es obligatoria" else null,
    )
    Spacer(Modifier.height(16.dp))
    BuilderTextField(city, onCityChange, "Ciudad", "Ciudad")
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BuilderTextField(
            value = startYear,
            onValueChange = onStartYearChange,
            label = "Desde",
            placeholder = "2021",
            modifier = Modifier.weight(1f),
            maxLength = 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && !startYear.isValidYearOrBlank(),
            supportingText = if (showErrors && !startYear.isValidYearOrBlank()) "Usa AAAA" else null,
        )
        BuilderTextField(
            value = if (isCurrent) "Actualidad" else endYear,
            onValueChange = onEndYearChange,
            label = "Hasta",
            placeholder = if (isCurrent) "Actualidad" else "2026",
            modifier = Modifier.weight(1f),
            maxLength = 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && !isCurrent && !endYear.isValidYearOrBlank(),
            supportingText = if (showErrors && !isCurrent && !endYear.isValidYearOrBlank()) "Usa AAAA" else null,
            enabled = !isCurrent,
        )
    }
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIsCurrentChange(!isCurrent) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isCurrent,
            onCheckedChange = null,
        )
        Text(modifier = Modifier.padding(horizontal = 6.dp), text = "Trabajo actualmente aquí")
    }
    Spacer(Modifier.height(16.dp))
    RichTextEditor(
        state = achievementsEditor,
        label = "Logros y funciones",
        placeholder = "Describe tus responsabilidades...",
        minLines = 3,
    )
    Spacer(Modifier.height(18.dp))
    BuilderEditorActions(onCancel = onCancel, onSave = onSave)
}

private fun dateRange(start: String, end: String): String =
    listOf(start, end).filter(String::isNotBlank).joinToString(" - ")

@Preview(name = "Experience Light", showBackground = true, heightDp = 900)
@Composable
private fun ExperienceLightPreview() {
    CvFreeTheme(darkTheme = false) { ExperienceScreen({}, {}) }
}

@Preview(name = "Experience Dark", showBackground = true, heightDp = 900)
@Composable
private fun ExperienceDarkPreview() {
    CvFreeTheme(darkTheme = true) { ExperienceScreen({}, {}) }
}
