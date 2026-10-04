package com.devpaul.features.resume.ui.education

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeEducation
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

@Composable
fun EducationScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    totalSteps: Int = 5,
    education: List<ResumeEducation> = emptyList(),
    onEducationChange: (List<ResumeEducation>) -> Unit = {},
) {
    var editingId by rememberSaveable { mutableStateOf(if (education.isEmpty()) NewEntryId else null) }
    var institution by rememberSaveable { mutableStateOf("") }
    var degree by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var startYear by rememberSaveable { mutableStateOf("") }
    var endYear by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    fun resetEditor() {
        institution = ""
        degree = ""
        city = ""
        startYear = ""
        endYear = ""
        showErrors = false
    }

    fun startNew() {
        resetEditor()
        editingId = NewEntryId
    }

    fun startEditing(item: ResumeEducation) {
        institution = item.institution
        degree = item.degree
        city = item.city
        startYear = item.startYear
        endYear = item.endYear
        showErrors = false
        editingId = item.id
    }

    fun hasPendingInput() = listOf(institution, degree, city, startYear, endYear).any(String::isNotBlank)

    fun saveEditor(): Boolean {
        showErrors = true
        if (institution.isBlank() || degree.isBlank() ||
            !startYear.isValidYearOrBlank() || !endYear.isValidYearOrBlank()
        ) {
            return false
        }
        val currentId = editingId
        val value = ResumeEducation(
            institution = institution.trim(),
            degree = degree.trim(),
            city = city.trim(),
            startYear = startYear.trim(),
            endYear = endYear.trim(),
            id = currentId?.takeUnless { it == NewEntryId } ?: newResumeEntryId(),
        )
        onEducationChange(
            if (currentId == NewEntryId) education + value
            else education.replaceById(value.id, ResumeEducation::id, value),
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
        step = 3,
        totalSteps = totalSteps,
        stepName = "Educación",
        title = "Agrega tu educación",
        description = "Incluye estudios, programas o certificaciones relevantes.",
        actionText = "Continuar a Habilidades →",
        onContinue = ::continueFlow,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            education.forEach { item ->
                val expanded = editingId == item.id
                BuilderSavedEntryCard(
                    title = item.degree,
                    subtitle = item.institution,
                    supportingText = listOf(item.city, dateRange(item.startYear, item.endYear))
                        .filter(String::isNotBlank)
                        .joinToString(" · "),
                    expanded = expanded,
                    onEdit = { if (expanded) editingId = null else startEditing(item) },
                    onDelete = {
                        onEducationChange(education.filterNot { it.id == item.id })
                        if (expanded) {
                            editingId = null
                            resetEditor()
                        }
                    },
                    editor = if (expanded) {
                        {
                            EducationEditor(
                                institution = institution,
                                onInstitutionChange = { institution = it },
                                degree = degree,
                                onDegreeChange = { degree = it },
                                city = city,
                                onCityChange = { city = it },
                                startYear = startYear,
                                onStartYearChange = { startYear = it.filter(Char::isDigit) },
                                endYear = endYear,
                                onEndYearChange = { endYear = it.filter(Char::isDigit) },
                                showErrors = showErrors,
                                onCancel = { editingId = null; resetEditor() },
                                onSave = ::saveEditor,
                            )
                        }
                    } else null,
                )
            }

            if (editingId == NewEntryId) {
                BuilderNewEntryCard(title = "Nueva educación") {
                    Spacer(Modifier.height(16.dp))
                    EducationEditor(
                        institution = institution,
                        onInstitutionChange = { institution = it },
                        degree = degree,
                        onDegreeChange = { degree = it },
                        city = city,
                        onCityChange = { city = it },
                        startYear = startYear,
                        onStartYearChange = { startYear = it.filter(Char::isDigit) },
                        endYear = endYear,
                        onEndYearChange = { endYear = it.filter(Char::isDigit) },
                        showErrors = showErrors,
                        onCancel = { editingId = null; resetEditor() },
                        onSave = ::saveEditor,
                    )
                }
            } else if (editingId == null) {
                BuilderAddButton(text = "Añadir educación", onClick = ::startNew)
            }
        }
    }
}

@Composable
private fun EducationEditor(
    institution: String,
    onInstitutionChange: (String) -> Unit,
    degree: String,
    onDegreeChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    startYear: String,
    onStartYearChange: (String) -> Unit,
    endYear: String,
    onEndYearChange: (String) -> Unit,
    showErrors: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    BuilderTextField(
        value = institution,
        onValueChange = onInstitutionChange,
        label = "Institución",
        placeholder = "Universidad / Instituto",
        isError = showErrors && institution.isBlank(),
        supportingText = if (showErrors && institution.isBlank()) "La institución es obligatoria" else null,
    )
    Spacer(Modifier.height(16.dp))
    BuilderTextField(
        value = degree,
        onValueChange = onDegreeChange,
        label = "Grado o carrera",
        placeholder = "Ingeniería de Sistemas",
        isError = showErrors && degree.isBlank(),
        supportingText = if (showErrors && degree.isBlank()) "El grado o carrera es obligatorio" else null,
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
            placeholder = "2017",
            modifier = Modifier.weight(1f),
            maxLength = 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && !startYear.isValidYearOrBlank(),
            supportingText = if (showErrors && !startYear.isValidYearOrBlank()) "Usa AAAA" else null,
        )
        BuilderTextField(
            value = endYear,
            onValueChange = onEndYearChange,
            label = "Hasta",
            placeholder = "2022",
            modifier = Modifier.weight(1f),
            maxLength = 4,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = showErrors && !endYear.isValidYearOrBlank(),
            supportingText = if (showErrors && !endYear.isValidYearOrBlank()) "Usa AAAA" else null,
        )
    }
    Spacer(Modifier.height(18.dp))
    BuilderEditorActions(onCancel = onCancel, onSave = onSave)
}

private fun dateRange(start: String, end: String): String =
    listOf(start, end).filter(String::isNotBlank).joinToString(" - ")

@Preview(name = "Education Light", showBackground = true, heightDp = 900)
@Composable
private fun EducationLightPreview() {
    CvFreeTheme(darkTheme = false) { EducationScreen({}, {}) }
}

@Preview(name = "Education Dark", showBackground = true, heightDp = 900)
@Composable
private fun EducationDarkPreview() {
    CvFreeTheme(darkTheme = true) { EducationScreen({}, {}) }
}
