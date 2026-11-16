package com.devpaul.features.templates.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.devpaul.features.templates.R
import com.devpaul.features.templates.pdf.ResumePdfFormat

@Composable
internal fun PdfFormatDialog(
    selectedFormat: ResumePdfFormat,
    onSelectFormat: (ResumePdfFormat) -> Unit,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pdf_format_title)) },
        text = {
            Column(Modifier.selectableGroup()) {
                ResumePdfFormat.entries.forEach { format ->
                    val isA4 = format == ResumePdfFormat.A4
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .selectable(
                                selected = selectedFormat == format,
                                role = Role.RadioButton,
                                onClick = { onSelectFormat(format) },
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = selectedFormat == format, onClick = null)
                        Column(Modifier.weight(1f).padding(start = 12.dp)) {
                            Text(
                                stringResource(if (isA4) R.string.pdf_format_a4 else R.string.pdf_format_continuous),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                stringResource(if (isA4) R.string.pdf_format_a4_description else R.string.pdf_format_continuous_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDownload) {
                Text(stringResource(R.string.ui_download_pdf_40863e))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.pdf_format_cancel)) }
        },
    )
}
