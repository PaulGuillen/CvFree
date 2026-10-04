package com.devpaul.features.resume.ui.links

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeLink
import com.devpaul.core.domain.resume.newResumeEntryId
import com.devpaul.features.resume.ui.components.BuilderAddButton
import com.devpaul.features.resume.ui.components.BuilderEditorActions
import com.devpaul.features.resume.ui.components.BuilderNewEntryCard
import com.devpaul.features.resume.ui.components.BuilderPage
import com.devpaul.features.resume.ui.components.BuilderSavedEntryCard
import com.devpaul.features.resume.ui.components.BuilderTextField
import com.devpaul.features.resume.ui.components.NewEntryId
import com.devpaul.features.resume.ui.components.isValidWebUrl
import com.devpaul.features.resume.ui.components.replaceById

private data class LinkPreset(val name: String, val url: String)

private val linkPresets = listOf(
    LinkPreset("LinkedIn", "https://linkedin.com/in/"),
    LinkPreset("GitHub", "https://github.com/"),
    LinkPreset("Portafolio", "https://"),
)

@Composable
fun LinksScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    step: Int,
    totalSteps: Int,
    actionText: String,
    modifier: Modifier = Modifier,
    links: List<ResumeLink> = emptyList(),
    onLinksChange: (List<ResumeLink>) -> Unit = {},
) {
    var editingId by rememberSaveable { mutableStateOf(if (links.isEmpty()) NewEntryId else null) }
    var linkName by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    fun resetEditor() {
        linkName = ""
        url = ""
        showErrors = false
    }

    fun startNew() {
        resetEditor()
        editingId = NewEntryId
    }

    fun startEditing(item: ResumeLink) {
        linkName = item.name
        url = item.url
        showErrors = false
        editingId = item.id
    }

    fun hasPendingInput() = linkName.isNotBlank() || url.isNotBlank()

    fun saveEditor(): Boolean {
        showErrors = true
        val duplicate = links.any { it.id != editingId && it.url.equals(url.trim(), ignoreCase = true) }
        if (linkName.isBlank() || !url.isValidWebUrl() || duplicate) return false

        val currentId = editingId
        val value = ResumeLink(
            name = linkName.trim(),
            url = url.trim(),
            id = currentId?.takeUnless { it == NewEntryId } ?: newResumeEntryId(),
        )
        onLinksChange(
            if (currentId == NewEntryId) links + value
            else links.replaceById(value.id, ResumeLink::id, value),
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
        stepName = "Enlaces",
        title = "Enlaces y redes sociales",
        description = "Agrega perfiles profesionales para mostrar mejor tu trabajo y experiencia.",
        actionText = actionText,
        onContinue = ::continueFlow,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            links.forEach { item ->
                val expanded = editingId == item.id
                BuilderSavedEntryCard(
                    title = item.name,
                    subtitle = item.url,
                    expanded = expanded,
                    onEdit = { if (expanded) editingId = null else startEditing(item) },
                    onDelete = {
                        onLinksChange(links.filterNot { it.id == item.id })
                        if (expanded) {
                            editingId = null
                            resetEditor()
                        }
                    },
                    editor = if (expanded) {
                        {
                            LinkEditor(
                                linkName = linkName,
                                onLinkNameChange = { linkName = it },
                                url = url,
                                onUrlChange = { url = it },
                                showErrors = showErrors,
                                duplicateUrl = links.any {
                                    it.id != editingId && it.url.equals(url.trim(), ignoreCase = true)
                                },
                                onPreset = { preset ->
                                    linkName = preset.name
                                    url = preset.url
                                },
                                onCancel = { editingId = null; resetEditor() },
                                onSave = ::saveEditor,
                            )
                        }
                    } else null,
                )
            }
            if (editingId == NewEntryId) {
                BuilderNewEntryCard(title = "Nuevo enlace") {
                    Spacer(Modifier.height(16.dp))
                    LinkEditor(
                        linkName = linkName,
                        onLinkNameChange = { linkName = it },
                        url = url,
                        onUrlChange = { url = it },
                        showErrors = showErrors,
                        duplicateUrl = links.any { it.url.equals(url.trim(), ignoreCase = true) },
                        onPreset = { preset ->
                            linkName = preset.name
                            url = preset.url
                        },
                        onCancel = { editingId = null; resetEditor() },
                        onSave = ::saveEditor,
                    )
                }
            } else if (editingId == null) {
                BuilderAddButton(text = "Añadir enlace", onClick = ::startNew)
            }
        }
    }
}

@Composable
private fun LinkEditor(
    linkName: String,
    onLinkNameChange: (String) -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    showErrors: Boolean,
    duplicateUrl: Boolean,
    onPreset: (LinkPreset) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        linkPresets.forEach { preset ->
            OutlinedButton(
                onClick = { onPreset(preset) },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            ) {
                Text(preset.name)
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    BuilderTextField(
        value = linkName,
        onValueChange = onLinkNameChange,
        label = "Nombre del enlace",
        placeholder = "Ej. LinkedIn",
        isError = showErrors && linkName.isBlank(),
        supportingText = if (showErrors && linkName.isBlank()) "El nombre es obligatorio" else null,
    )
    Spacer(Modifier.height(16.dp))
    val urlError = when {
        duplicateUrl -> "Este enlace ya fue agregado"
        url.isNotBlank() && !url.isValidWebUrl() -> "Incluye una URL completa con https://"
        showErrors && url.isBlank() -> "La URL es obligatoria"
        else -> null
    }
    BuilderTextField(
        value = url,
        onValueChange = onUrlChange,
        label = "URL",
        placeholder = "https://",
        isError = showErrors && urlError != null,
        supportingText = if (showErrors) urlError else null,
    )
    Spacer(Modifier.height(18.dp))
    BuilderEditorActions(onCancel = onCancel, onSave = onSave)
}

@Preview(name = "Links Light", showBackground = true, heightDp = 850)
@Composable
private fun LinksLightPreview() {
    CvFreeTheme(darkTheme = false) { LinksScreen({}, {}, 6, 9, "Continuar →") }
}

@Preview(name = "Links Dark", showBackground = true, heightDp = 850)
@Composable
private fun LinksDarkPreview() {
    CvFreeTheme(darkTheme = true) { LinksScreen({}, {}, 6, 9, "Continuar →") }
}
