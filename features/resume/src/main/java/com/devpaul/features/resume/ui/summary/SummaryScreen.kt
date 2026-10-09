package com.devpaul.features.resume.ui.summary

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.domain.resume.ResumeTextFormatting
import com.devpaul.features.resume.ui.components.BuilderFormCard
import com.devpaul.features.resume.ui.components.BuilderPage

private const val SummaryMaxLength = 1_000

@Composable
fun SummaryScreen(
    onBack: () -> Unit,
    onContinue: (String, ResumeTextFormatting) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 5,
    totalSteps: Int = 5,
    initialSummary: String = "",
    initialFormatting: ResumeTextFormatting = ResumeTextFormatting(),
) {
    val editor = rememberRichTextEditorState(
        initialText = initialSummary,
        initialFormatting = initialFormatting,
        maxLength = SummaryMaxLength,
    )

    BuilderPage(
        onBack = onBack,
        step = step,
        totalSteps = totalSteps,
        stepName = "Resumen",
        title = "Resumen profesional",
        description = "Resume tu perfil, experiencia y fortalezas. Puedes usar listas para facilitar la lectura.",
        actionText = "Seleccionar plantilla →",
        onContinue = { onContinue(editor.value.text, editor.formatting) },
        modifier = modifier,
    ) {
        BuilderFormCard {
            RichTextEditor(
                state = editor,
                label = "Acerca de mí",
                placeholder = "Ej. Desarrollador Android sénior con experiencia en Kotlin, Compose y arquitectura limpia...",
                minLines = 5,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${editor.value.text.length}/$SummaryMaxLength caracteres",
                modifier = Modifier.fillMaxWidth().align(Alignment.End),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Preview(name = "Summary Light", showBackground = true, heightDp = 820)
@Composable
private fun SummaryLightPreview() {
    CvFreeTheme(darkTheme = false) { SummaryScreen({}, { _, _ -> }) }
}

@Preview(name = "Summary Dark", showBackground = true, heightDp = 820)
@Composable
private fun SummaryDarkPreview() {
    CvFreeTheme(darkTheme = true) { SummaryScreen({}, { _, _ -> }) }
}
