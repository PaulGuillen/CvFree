package com.devpaul.features.resume.ui.references

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.devpaul.core.domain.resume.ResumeReference
import com.devpaul.core.domain.resume.newResumeEntryId
import com.devpaul.features.resume.ui.components.BuilderAddButton
import com.devpaul.features.resume.ui.components.BuilderEditorActions
import com.devpaul.features.resume.ui.components.BuilderNewEntryCard
import com.devpaul.features.resume.ui.components.BuilderPage
import com.devpaul.features.resume.ui.components.BuilderSavedEntryCard
import com.devpaul.features.resume.ui.components.BuilderTextField
import com.devpaul.features.resume.ui.components.NewEntryId
import com.devpaul.features.resume.ui.components.isValidEmailOrBlank
import com.devpaul.features.resume.ui.components.replaceById

@Composable
fun ReferencesScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    step: Int,
    totalSteps: Int,
    actionText: String,
    modifier: Modifier = Modifier,
    references: List<ResumeReference> = emptyList(),
    onReferencesChange: (List<ResumeReference>) -> Unit = {},
) {
    var editingId by rememberSaveable { mutableStateOf(if (references.isEmpty()) NewEntryId else null) }
    var name by rememberSaveable { mutableStateOf("") }
    var company by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    fun resetEditor() {
        name = ""
        company = ""
        phone = ""
        email = ""
        showErrors = false
    }

    fun startNew() {
        resetEditor()
        editingId = NewEntryId
    }

    fun startEditing(item: ResumeReference) {
        name = item.name
        company = item.company
        phone = item.phone
        email = item.email
        showErrors = false
        editingId = item.id
    }

    fun hasPendingInput() = listOf(name, company, phone, email).any(String::isNotBlank)

    fun saveEditor(): Boolean {
        showErrors = true
        if (name.isBlank() || (phone.isBlank() && email.isBlank()) || !email.isValidEmailOrBlank()) return false

        val currentId = editingId
        val value = ResumeReference(
            name = name.trim(),
            company = company.trim(),
            phone = phone.trim(),
            email = email.trim(),
            id = currentId?.takeUnless { it == NewEntryId } ?: newResumeEntryId(),
        )
        onReferencesChange(
            if (currentId == NewEntryId) references + value
            else references.replaceById(value.id, ResumeReference::id, value),
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
        stepName = "Referencias",
        title = "Referencias profesionales",
        description = "Añade personas que puedan respaldar tu experiencia y desempeño profesional.",
        actionText = actionText,
        onContinue = ::continueFlow,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            references.forEach { item ->
                val expanded = editingId == item.id
                BuilderSavedEntryCard(
                    title = item.name,
                    subtitle = item.company,
                    supportingText = listOf(item.phone, item.email).filter(String::isNotBlank).joinToString(" · "),
                    expanded = expanded,
                    onEdit = { if (expanded) editingId = null else startEditing(item) },
                    onDelete = {
                        onReferencesChange(references.filterNot { it.id == item.id })
                        if (expanded) {
                            editingId = null
                            resetEditor()
                        }
                    },
                    editor = if (expanded) {
                        {
                            ReferenceEditor(
                                name = name,
                                onNameChange = { name = it },
                                company = company,
                                onCompanyChange = { company = it },
                                phone = phone,
                                onPhoneChange = { phone = it },
                                email = email,
                                onEmailChange = { email = it },
                                showErrors = showErrors,
                                onCancel = { editingId = null; resetEditor() },
                                onSave = ::saveEditor,
                            )
                        }
                    } else null,
                )
            }
            if (editingId == NewEntryId) {
                BuilderNewEntryCard(title = "Nueva referencia") {
                    Spacer(Modifier.height(16.dp))
                    ReferenceEditor(
                        name = name,
                        onNameChange = { name = it },
                        company = company,
                        onCompanyChange = { company = it },
                        phone = phone,
                        onPhoneChange = { phone = it },
                        email = email,
                        onEmailChange = { email = it },
                        showErrors = showErrors,
                        onCancel = { editingId = null; resetEditor() },
                        onSave = ::saveEditor,
                    )
                }
            } else if (editingId == null) {
                BuilderAddButton(text = "Añadir referencia", onClick = ::startNew)
            }
        }
    }
}

@Composable
private fun ReferenceEditor(
    name: String,
    onNameChange: (String) -> Unit,
    company: String,
    onCompanyChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    showErrors: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    BuilderTextField(
        value = name,
        onValueChange = onNameChange,
        label = "Nombre completo",
        placeholder = "Ej. Andrea Ramírez",
        isError = showErrors && name.isBlank(),
        supportingText = if (showErrors && name.isBlank()) "El nombre es obligatorio" else null,
    )
    Spacer(Modifier.height(16.dp))
    BuilderTextField(
        value = company,
        onValueChange = onCompanyChange,
        label = "Empresa y cargo",
        placeholder = "Ej. Tecnología Andina · Gerente de Ingeniería",
    )
    Spacer(Modifier.height(16.dp))
    val missingContact = showErrors && phone.isBlank() && email.isBlank()
    BuilderTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = "Teléfono",
        placeholder = "+51 999 999 999",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        isError = missingContact,
        supportingText = if (missingContact) "Agrega teléfono o correo" else null,
    )
    Spacer(Modifier.height(16.dp))
    val invalidEmail = showErrors && !email.isValidEmailOrBlank()
    BuilderTextField(
        value = email,
        onValueChange = onEmailChange,
        label = "Correo electrónico",
        placeholder = "nombre@correo.com",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = invalidEmail || missingContact,
        supportingText = when {
            invalidEmail -> "Ingresa un correo válido"
            missingContact -> "Agrega teléfono o correo"
            else -> null
        },
    )
    Spacer(Modifier.height(18.dp))
    BuilderEditorActions(onCancel = onCancel, onSave = onSave)
}

@Preview(name = "References Light", showBackground = true, heightDp = 920)
@Composable
private fun ReferencesLightPreview() {
    CvFreeTheme(darkTheme = false) { ReferencesScreen({}, {}, 8, 9, "Continuar →") }
}

@Preview(name = "References Dark", showBackground = true, heightDp = 920)
@Composable
private fun ReferencesDarkPreview() {
    CvFreeTheme(darkTheme = true) { ReferencesScreen({}, {}, 8, 9, "Continuar →") }
}
