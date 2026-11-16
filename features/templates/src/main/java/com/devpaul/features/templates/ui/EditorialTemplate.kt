package com.devpaul.features.templates.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.features.templates.R
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateRenderer

private val EditorialInk = Color(0xFF202020)

// Each section is assigned to one column only; the original document remains unchanged.
internal fun editorialColumns(resume: ResumeDocument, oslo: Boolean): Pair<ResumeDocument, ResumeDocument> {
    val career = resume.copy(education = emptyList(), skills = emptyList(), languages = emptyList(), links = emptyList())
    val qualifications = resume.copy(summary = "", experiences = emptyList(), courses = emptyList(), references = emptyList())
    return if (oslo) {
        qualifications.copy(skills = emptyList()) to career.copy(skills = resume.skills)
    } else {
        career.copy(courses = emptyList()) to qualifications.copy(courses = resume.courses)
    }
}

@Composable
internal fun EditorialTemplate(resume: ResumeDocument, style: ResumeTemplateRenderer) {
    if (style == ResumeTemplateRenderer.BerlinStructured) {
        BerlinTemplate(resume)
        return
    }
    val oslo = style == ResumeTemplateRenderer.OsloCompact
    Column(Modifier.fillMaxWidth().background(Color.White).padding(30.dp)) {
        EditorialHeader(resume, oslo)
        Spacer(Modifier.height(15.dp))
        HorizontalDivider(color = EditorialInk, thickness = if (oslo) 1.5.dp else 0.7.dp)
        val (left, right) = editorialColumns(resume, oslo)
        // FlowRow content must be measured at its actual column width. Intrinsic
        // height can underestimate wrapped rows and clip the final languages.
        Row(Modifier.fillMaxWidth().drawBehind {
            val dividerX = size.width * if (oslo) 0.32f else 0.53f
            drawLine(Color(0xFFB7B7B7), Offset(dividerX, 0f), Offset(dividerX, size.height), 0.6.dp.toPx())
        }) {
            Column(Modifier.weight(if (oslo) 0.32f else 0.53f).padding(end = 13.dp)) {
                ProfessionalContent(left, style)
            }
            Column(Modifier.weight(if (oslo) 0.68f else 0.47f).padding(start = 13.dp)) {
                ProfessionalContent(right, style)
            }
        }
    }
}

@Composable
private fun EditorialHeader(resume: ResumeDocument, oslo: Boolean) {
    val contact = resume.contact
    val name = listOf(contact.name, contact.lastName).filter(String::isNotBlank).joinToString(" ")
        .ifBlank { stringResource(R.string.ui_your_name_f308e7) }
    val contacts = listOf(contact.email, contact.phone,
        listOf(contact.address, contact.city).filter(String::isNotBlank).distinct().joinToString(", "))
        .filter(String::isNotBlank)
    if (oslo) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, color = EditorialInk, fontSize = 30.sp, lineHeight = 34.sp,
                fontWeight = FontWeight.Light, textAlign = TextAlign.Center)
            if (contact.professionalTitle.isNotBlank()) {
                Text(contact.professionalTitle.uppercase(), color = EditorialInk,
                    fontSize = 9.sp, lineHeight = 13.sp, letterSpacing = 1.3.sp, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(8.dp))
            Text(contacts.joinToString(" | "), color = EditorialInk,
                fontSize = 7.5.sp, lineHeight = 11.sp, textAlign = TextAlign.Center)
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            Column(Modifier.weight(0.6f)) {
                Text(name.uppercase(), color = EditorialInk, fontFamily = FontFamily.Serif,
                    fontSize = 23.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold)
                if (contact.professionalTitle.isNotBlank()) {
                    Spacer(Modifier.height(5.dp))
                    Text(contact.professionalTitle, color = EditorialInk, fontSize = 10.sp, lineHeight = 14.sp)
                }
            }
            Column(Modifier.weight(0.4f), horizontalAlignment = Alignment.End) {
                contacts.forEach { Text(it, color = EditorialInk, fontSize = 8.sp, lineHeight = 12.sp,
                    textAlign = TextAlign.End) }
            }
        }
    }
}

@Preview(name = "Berlin", widthDp = 490, showBackground = true)
@Composable
private fun BerlinPreview() {
    ResumeTemplateDocument(previewDocument().copy(templateName = ResumeTemplateCatalog.BERLIN_STRUCTURED_ID))
}

@Preview(name = "Vienna", widthDp = 490, showBackground = true)
@Composable
private fun ViennaPreview() {
    ResumeTemplateDocument(previewDocument().copy(templateName = ResumeTemplateCatalog.VIENNA_EDITORIAL_ID))
}

@Preview(name = "Oslo", widthDp = 490, showBackground = true)
@Composable
private fun OsloPreview() {
    ResumeTemplateDocument(previewDocument().copy(templateName = ResumeTemplateCatalog.OSLO_COMPACT_ID))
}
