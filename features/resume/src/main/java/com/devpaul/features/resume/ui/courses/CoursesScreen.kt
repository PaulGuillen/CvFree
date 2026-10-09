package com.devpaul.features.resume.ui.courses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeCourse
import com.devpaul.core.domain.resume.newResumeEntryId
import com.devpaul.features.resume.ui.components.BuilderAddButton
import com.devpaul.features.resume.ui.components.BuilderEditorActions
import com.devpaul.features.resume.ui.components.BuilderNewEntryCard
import com.devpaul.features.resume.ui.components.BuilderPage
import com.devpaul.features.resume.ui.components.BuilderSavedEntryCard
import com.devpaul.features.resume.ui.components.BuilderTextField
import com.devpaul.features.resume.ui.components.NewEntryId
import com.devpaul.features.resume.ui.components.isValidMonthYearOrBlank
import com.devpaul.features.resume.ui.components.replaceById

@Composable
fun CoursesScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    step: Int,
    totalSteps: Int,
    actionText: String,
    modifier: Modifier = Modifier,
    courses: List<ResumeCourse> = emptyList(),
    onCoursesChange: (List<ResumeCourse>) -> Unit = {},
) {
    var editingId by rememberSaveable { mutableStateOf(if (courses.isEmpty()) NewEntryId else null) }
    var courseName by rememberSaveable { mutableStateOf("") }
    var institution by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    fun resetEditor() {
        courseName = ""
        institution = ""
        startDate = ""
        endDate = ""
        showErrors = false
    }

    fun startNew() {
        resetEditor()
        editingId = NewEntryId
    }

    fun startEditing(item: ResumeCourse) {
        courseName = item.name
        institution = item.institution
        startDate = item.startDate
        endDate = item.endDate
        showErrors = false
        editingId = item.id
    }

    fun hasPendingInput() = listOf(courseName, institution, startDate, endDate).any(String::isNotBlank)

    fun saveEditor(): Boolean {
        showErrors = true
        if (courseName.isBlank() || !startDate.isValidMonthYearOrBlank() || !endDate.isValidMonthYearOrBlank()) {
            return false
        }
        val currentId = editingId
        val value = ResumeCourse(
            name = courseName.trim(),
            institution = institution.trim(),
            startDate = startDate.trim(),
            endDate = endDate.trim(),
            id = currentId?.takeUnless { it == NewEntryId } ?: newResumeEntryId(),
        )
        onCoursesChange(
            if (currentId == NewEntryId) listOf(value) + courses
            else courses.replaceById(value.id, ResumeCourse::id, value),
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
        step = step,
        totalSteps = totalSteps,
        stepName = "Cursos",
        title = "Certificaciones y cursos",
        description = "Incluye cursos relevantes que respalden tus conocimientos y habilidades.",
        actionText = actionText,
        onContinue = ::continueFlow,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (editingId == NewEntryId) {
                BuilderNewEntryCard(
                    title = "Nuevo curso o certificación",
                    bringIntoView = courses.isNotEmpty(),
                ) {
                    Spacer(Modifier.height(16.dp))
                    CourseEditor(
                        courseName = courseName,
                        onCourseNameChange = { courseName = it },
                        institution = institution,
                        onInstitutionChange = { institution = it },
                        startDate = startDate,
                        onStartDateChange = { startDate = it },
                        endDate = endDate,
                        onEndDateChange = { endDate = it },
                        showErrors = showErrors,
                        onCancel = { editingId = null; resetEditor() },
                        onSave = ::saveEditor,
                    )
                }
            }

            courses.forEach { item ->
                val expanded = editingId == item.id
                BuilderSavedEntryCard(
                    title = item.name,
                    subtitle = item.institution,
                    supportingText = dateRange(item.startDate, item.endDate),
                    expanded = expanded,
                    onEdit = { if (expanded) editingId = null else startEditing(item) },
                    onDelete = {
                        onCoursesChange(courses.filterNot { it.id == item.id })
                        if (expanded) {
                            editingId = null
                            resetEditor()
                        }
                    },
                    editor = if (expanded) {
                        {
                            CourseEditor(
                                courseName = courseName,
                                onCourseNameChange = { courseName = it },
                                institution = institution,
                                onInstitutionChange = { institution = it },
                                startDate = startDate,
                                onStartDateChange = { startDate = it },
                                endDate = endDate,
                                onEndDateChange = { endDate = it },
                                showErrors = showErrors,
                                onCancel = { editingId = null; resetEditor() },
                                onSave = ::saveEditor,
                            )
                        }
                    } else null,
                )
            }
            if (editingId == null) {
                BuilderAddButton(text = "Añadir curso", onClick = ::startNew)
            }
        }
    }
}

@Composable
private fun CourseEditor(
    courseName: String,
    onCourseNameChange: (String) -> Unit,
    institution: String,
    onInstitutionChange: (String) -> Unit,
    startDate: String,
    onStartDateChange: (String) -> Unit,
    endDate: String,
    onEndDateChange: (String) -> Unit,
    showErrors: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    BuilderTextField(
        value = courseName,
        onValueChange = onCourseNameChange,
        label = "Nombre del curso o certificación",
        placeholder = "Ej. Android con Jetpack Compose",
        isError = showErrors && courseName.isBlank(),
        supportingText = if (showErrors && courseName.isBlank()) "El nombre es obligatorio" else null,
    )
    Spacer(Modifier.height(16.dp))
    BuilderTextField(
        value = institution,
        onValueChange = onInstitutionChange,
        label = "Institución u organizador",
        placeholder = "Ej. Google, Coursera, Platzi",
    )
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BuilderTextField(
            value = startDate,
            onValueChange = onStartDateChange,
            label = "Inicio",
            placeholder = "MM/AAAA",
            modifier = Modifier.weight(1f),
            maxLength = 7,
            isError = showErrors && !startDate.isValidMonthYearOrBlank(),
            supportingText = if (showErrors && !startDate.isValidMonthYearOrBlank()) "MM/AAAA" else null,
        )
        BuilderTextField(
            value = endDate,
            onValueChange = onEndDateChange,
            label = "Fin",
            placeholder = "MM/AAAA",
            modifier = Modifier.weight(1f),
            maxLength = 7,
            isError = showErrors && !endDate.isValidMonthYearOrBlank(),
            supportingText = if (showErrors && !endDate.isValidMonthYearOrBlank()) "MM/AAAA" else null,
        )
    }
    Spacer(Modifier.height(18.dp))
    BuilderEditorActions(onCancel = onCancel, onSave = onSave)
}

private fun dateRange(start: String, end: String): String =
    listOf(start, end).filter(String::isNotBlank).joinToString(" - ")

@Preview(name = "Courses Light", showBackground = true, heightDp = 880)
@Composable
private fun CoursesLightPreview() {
    CvFreeTheme(darkTheme = false) { CoursesScreen({}, {}, 5, 9, "Continuar →") }
}

@Preview(name = "Courses Dark", showBackground = true, heightDp = 880)
@Composable
private fun CoursesDarkPreview() {
    CvFreeTheme(darkTheme = true) { CoursesScreen({}, {}, 5, 9, "Continuar →") }
}
