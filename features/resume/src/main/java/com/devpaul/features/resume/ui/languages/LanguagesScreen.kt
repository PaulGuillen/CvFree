package com.devpaul.features.resume.ui.languages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeLanguage
import com.devpaul.core.domain.resume.newResumeEntryId
import com.devpaul.features.resume.ui.components.BuilderAddButton
import com.devpaul.features.resume.ui.components.BuilderEditorActions
import com.devpaul.features.resume.ui.components.BuilderNewEntryCard
import com.devpaul.features.resume.ui.components.BuilderPage
import com.devpaul.features.resume.ui.components.BuilderSavedEntryCard
import com.devpaul.features.resume.ui.components.NewEntryId
import com.devpaul.features.resume.ui.components.replaceById

private val availableLanguages = listOf("Español", "Inglés", "Portugués", "Francés", "Alemán")
private val availableLevels = listOf("Básico", "Intermedio", "Avanzado", "Nativo")

@Composable
fun LanguagesScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    step: Int,
    totalSteps: Int,
    actionText: String,
    modifier: Modifier = Modifier,
    selectedLanguages: List<ResumeLanguage> = emptyList(),
    onLanguagesChange: (List<ResumeLanguage>) -> Unit = {},
) {
    var editingId by rememberSaveable {
        mutableStateOf(if (selectedLanguages.isEmpty()) NewEntryId else null)
    }
    var selectedLanguage by rememberSaveable { mutableStateOf(availableLanguages.first()) }
    var selectedLevel by rememberSaveable { mutableStateOf(availableLevels.last()) }
    var editorTouched by rememberSaveable { mutableStateOf(false) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    fun resetEditor() {
        selectedLanguage = availableLanguages.first()
        selectedLevel = availableLevels.last()
        editorTouched = false
        showErrors = false
    }

    fun startNew() {
        resetEditor()
        editingId = NewEntryId
    }

    fun startEditing(item: ResumeLanguage) {
        selectedLanguage = item.language
        selectedLevel = item.level
        editorTouched = true
        showErrors = false
        editingId = item.id
    }

    fun saveEditor(): Boolean {
        showErrors = true
        val duplicate = selectedLanguages.any {
            it.id != editingId && it.language.equals(selectedLanguage, ignoreCase = true)
        }
        if (duplicate) return false

        val currentId = editingId
        val value = ResumeLanguage(
            language = selectedLanguage,
            level = selectedLevel,
            id = currentId?.takeUnless { it == NewEntryId } ?: newResumeEntryId(),
        )
        onLanguagesChange(
            if (currentId == NewEntryId) listOf(value) + selectedLanguages
            else selectedLanguages.replaceById(value.id, ResumeLanguage::id, value),
        )
        editingId = null
        resetEditor()
        return true
    }

    fun continueFlow() {
        when {
            editingId == null -> onContinue()
            editingId == NewEntryId && !editorTouched -> {
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
        stepName = "Idiomas",
        title = "Idiomas",
        description = "Indica los idiomas que dominas y tu nivel de competencia.",
        actionText = actionText,
        onContinue = ::continueFlow,
        modifier = modifier,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (editingId == NewEntryId) {
                BuilderNewEntryCard(
                    title = "Nuevo idioma",
                    bringIntoView = selectedLanguages.isNotEmpty(),
                ) {
                    Spacer(Modifier.height(16.dp))
                    LanguageEditor(
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = {
                            selectedLanguage = it
                            editorTouched = true
                        },
                        selectedLevel = selectedLevel,
                        onLevelChange = {
                            selectedLevel = it
                            editorTouched = true
                        },
                        duplicate = showErrors && selectedLanguages.any {
                            it.language.equals(selectedLanguage, ignoreCase = true)
                        },
                        onCancel = { editingId = null; resetEditor() },
                        onSave = ::saveEditor,
                    )
                }
            }

            selectedLanguages.forEach { item ->
                val expanded = editingId == item.id
                BuilderSavedEntryCard(
                    title = item.language,
                    subtitle = item.level,
                    expanded = expanded,
                    onEdit = { if (expanded) editingId = null else startEditing(item) },
                    onDelete = {
                        onLanguagesChange(selectedLanguages.filterNot { it.id == item.id })
                        if (expanded) {
                            editingId = null
                            resetEditor()
                        }
                    },
                    editor = if (expanded) {
                        {
                            LanguageEditor(
                                selectedLanguage = selectedLanguage,
                                onLanguageChange = {
                                    selectedLanguage = it
                                    editorTouched = true
                                },
                                selectedLevel = selectedLevel,
                                onLevelChange = {
                                    selectedLevel = it
                                    editorTouched = true
                                },
                                duplicate = showErrors && selectedLanguages.any {
                                    it.id != editingId && it.language.equals(selectedLanguage, ignoreCase = true)
                                },
                                onCancel = { editingId = null; resetEditor() },
                                onSave = ::saveEditor,
                            )
                        }
                    } else null,
                )
            }
            if (editingId == null) {
                BuilderAddButton(text = "Añadir idioma", onClick = ::startNew)
            }
        }
        Spacer(Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
        ) {
            Text(
                text = "Sé preciso con tu nivel. Algunas empresas pueden validarlo durante la entrevista.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun LanguageEditor(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    selectedLevel: String,
    onLevelChange: (String) -> Unit,
    duplicate: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Text("Idioma", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            onClick = { menuExpanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                if (duplicate) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
            ),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(selectedLanguage, modifier = Modifier.weight(1f))
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Seleccionar idioma")
            }
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            availableLanguages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onLanguageChange(language)
                        menuExpanded = false
                    },
                )
            }
        }
    }
    if (duplicate) {
        Text(
            text = "Este idioma ya fue agregado",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
    Spacer(Modifier.height(18.dp))
    Text("Nivel", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        availableLevels.chunked(2).forEach { rowLevels ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowLevels.forEach { level ->
                    val selected = level == selectedLevel
                    Surface(
                        onClick = { onLevelChange(level) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        ),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = level,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            )
                        }
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(18.dp))
    BuilderEditorActions(onCancel = onCancel, onSave = onSave)
}

@Preview(name = "Languages Light", showBackground = true, heightDp = 900)
@Composable
private fun LanguagesLightPreview() {
    CvFreeTheme(darkTheme = false) { LanguagesScreen({}, {}, 7, 9, "Continuar →") }
}

@Preview(name = "Languages Dark", showBackground = true, heightDp = 900)
@Composable
private fun LanguagesDarkPreview() {
    CvFreeTheme(darkTheme = true) { LanguagesScreen({}, {}, 7, 9, "Continuar →") }
}
