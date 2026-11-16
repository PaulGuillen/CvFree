package com.devpaul.features.templates.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.features.templates.R
import com.devpaul.features.templates.ResumeTemplateRenderer

internal data class BerlinBlock(val title: Int, val document: ResumeDocument)

internal fun berlinBlocks(resume: ResumeDocument): List<BerlinBlock> {
    val blank = resume.copy(summary = "", experiences = emptyList(), education = emptyList(),
        skills = emptyList(), courses = emptyList(), languages = emptyList(), links = emptyList(), references = emptyList())
    return buildList {
        if (resume.summary.isNotBlank()) add(BerlinBlock(R.string.ui_about_me_d15068, blank.copy(summary = resume.summary)))
        if (resume.experiences.isNotEmpty()) add(BerlinBlock(R.string.ui_work_experience_1706e7, blank.copy(experiences = resume.experiences)))
        if (resume.education.isNotEmpty()) add(BerlinBlock(R.string.ui_education_4e9a5f, blank.copy(education = resume.education)))
        if (resume.skills.isNotEmpty()) add(BerlinBlock(R.string.ui_skills_0794df, blank.copy(skills = resume.skills)))
        if (ResumeOptionalSection.Courses in resume.optionalSections && resume.courses.isNotEmpty()) {
            add(BerlinBlock(R.string.ui_courses_177a73, blank.copy(courses = resume.courses)))
        }
        if (ResumeOptionalSection.Languages in resume.optionalSections && resume.languages.isNotEmpty()) {
            add(BerlinBlock(R.string.ui_languages_c765ac, blank.copy(languages = resume.languages)))
        }
        if (ResumeOptionalSection.Links in resume.optionalSections && resume.links.isNotEmpty()) {
            add(BerlinBlock(R.string.ui_links_d38518, blank.copy(links = resume.links)))
        }
        if (ResumeOptionalSection.References in resume.optionalSections && resume.references.isNotEmpty()) {
            add(BerlinBlock(R.string.ui_references_9e4676, blank.copy(references = resume.references)))
        }
    }
}

@Composable
internal fun BerlinTemplate(resume: ResumeDocument) {
    val ink = Color(0xFF202020)
    val contact = resume.contact
    Column(Modifier.fillMaxWidth().background(Color.White).padding(30.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text(listOf(contact.name, contact.lastName).filter(String::isNotBlank).joinToString("\n")
                    .ifBlank { stringResource(R.string.ui_your_name_f308e7) }.uppercase(),
                    color = ink, fontSize = 26.sp, lineHeight = 28.sp, fontWeight = FontWeight.Black)
                if (contact.professionalTitle.isNotBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Text(contact.professionalTitle, color = ink, fontSize = 10.sp, lineHeight = 13.sp)
                }
            }
            Column(Modifier.width(148.dp)) {
                listOf(contact.email, contact.phone,
                    listOf(contact.address, contact.city).filter(String::isNotBlank).distinct().joinToString(", "))
                    .filter(String::isNotBlank).forEach {
                        Text(it, modifier = Modifier.fillMaxWidth(), color = ink, fontSize = 8.sp,
                            lineHeight = 12.sp, textAlign = TextAlign.End)
                    }
            }
        }
        Spacer(Modifier.height(18.dp))
        HorizontalDivider(color = ink, thickness = 2.dp)
        berlinBlocks(resume).forEach { block ->
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(vertical = 12.dp)) {
                Text(stringResource(block.title).uppercase(), modifier = Modifier.width(94.dp).padding(end = 12.dp),
                    color = ink, fontSize = 8.5.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                VerticalDivider(Modifier.fillMaxHeight(), color = Color(0xFFB6B6B6), thickness = 0.6.dp)
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    ProfessionalContent(block.document, ResumeTemplateRenderer.BerlinStructured, showSectionTitles = false)
                }
            }
            HorizontalDivider(color = Color(0xFFD4D4D4), thickness = 0.5.dp)
        }
    }
}
