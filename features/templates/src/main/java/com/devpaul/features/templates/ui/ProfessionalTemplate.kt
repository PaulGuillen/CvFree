package com.devpaul.features.templates.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.core.domain.resume.ResumeTextFormatting
import com.devpaul.features.templates.R
import com.devpaul.features.templates.ResumeTemplateRenderer

private val ProfessionalInk = Color(0xFF20242B)
private val ProfessionalMuted = Color(0xFF5E636D)

@Composable
internal fun ProfessionalTemplate(resume: ResumeDocument, style: ResumeTemplateRenderer) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 42.dp, top = 36.dp, end = 42.dp, bottom = 36.dp),
    ) {
        ProfessionalHeader(resume, style)

        ProfessionalContent(resume, style)
    }
}

@Composable
internal fun ProfessionalContent(resume: ResumeDocument, style: ResumeTemplateRenderer, showSectionTitles: Boolean = true) {
    Column {
        val summaryFirst = style == ResumeTemplateRenderer.ViennaEditorial
        if (summaryFirst && resume.summary.isNotBlank()) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_about_me_d15068))
            ProfessionalFormattedBody(resume.summary, resume.summaryFormatting)
        }
        if (style != ResumeTemplateRenderer.LimaProfessional && style != ResumeTemplateRenderer.CambridgeClassic) {
            ProfessionalLinksSection(resume, style, showSectionTitles)
        }

        if (!summaryFirst && resume.summary.isNotBlank()) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_about_me_d15068))
            ProfessionalFormattedBody(resume.summary, resume.summaryFormatting)
        }

        if (resume.experiences.isNotEmpty()) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_work_experience_1706e7))
            resume.experiences.forEachIndexed { index, experience ->
                ProfessionalEntry(
                    style = style,
                    title = experience.position,
                    organization = experience.company,
                    location = experience.city,
                    date = professionalDate(experience.startYear, experience.localizedEndYear()),
                    description = experience.achievements,
                    descriptionFormatting = experience.achievementsFormatting,
                )
                if (index != resume.experiences.lastIndex) Spacer(Modifier.height(11.dp))
            }
        }

        if (resume.education.isNotEmpty()) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_education_4e9a5f))
            resume.education.forEachIndexed { index, education ->
                ProfessionalEntry(
                    style = style,
                    title = education.degree,
                    organization = education.institution,
                    location = education.city,
                    date = professionalDate(education.startYear, education.endYear),
                )
                if (index != resume.education.lastIndex) Spacer(Modifier.height(9.dp))
            }
        }

        if (resume.skills.isNotEmpty()) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_skills_0794df))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                resume.skills.forEach { skill -> ProfessionalBulletLabel(skill) }
            }
        }

        val showLanguages = ResumeOptionalSection.Languages in resume.optionalSections && resume.languages.isNotEmpty()
        if (showLanguages) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_languages_c765ac))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                resume.languages.forEach { language ->
                    ProfessionalBulletLabel(
                        buildString {
                            append(language.language)
                            if (language.level.isNotBlank()) append(" (").append(language.level).append(")")
                        },
                    )
                }
            }
        }

        val showCourses = ResumeOptionalSection.Courses in resume.optionalSections && resume.courses.isNotEmpty()
        if (showCourses) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_courses_177a73))
            resume.courses.forEachIndexed { index, course ->
                ProfessionalEntry(
                    style = style,
                    title = course.name,
                    organization = course.institution,
                    location = "",
                    date = professionalDate(course.startDate, course.endDate),
                )
                if (index != resume.courses.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        if (style == ResumeTemplateRenderer.CambridgeClassic) {
            ProfessionalLinksSection(resume, style, showSectionTitles)
        }

        val showReferences = ResumeOptionalSection.References in resume.optionalSections && resume.references.isNotEmpty()
        if (showReferences) {
            if (showSectionTitles) ProfessionalSectionGap()
            if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_references_9e4676))
            resume.references.forEachIndexed { index, reference ->
                Text(
                    text = listOf(reference.name, reference.company, reference.phone, reference.email)
                        .filter(String::isNotBlank)
                        .joinToString("  |  "),
                    color = ProfessionalInk,
                    fontSize = 8.sp,
                    lineHeight = 11.sp,
                )
                if (index != resume.references.lastIndex) Spacer(Modifier.height(7.dp))
            }
        }
        if (style == ResumeTemplateRenderer.LimaProfessional) {
            ProfessionalLinksSection(resume, style, showSectionTitles)
        }

    }
}

@Composable
internal fun ProfessionalHeader(resume: ResumeDocument, style: ResumeTemplateRenderer) {
    val contact = resume.contact
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (style in setOf(ResumeTemplateRenderer.CambridgeClassic, ResumeTemplateRenderer.LimaProfessional)) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Text(
            text = listOf(contact.name, contact.lastName)
                .filter(String::isNotBlank)
                .joinToString(" ")
                .ifBlank { stringResource(R.string.ui_your_name_f308e7) }
                .uppercase(),
            color = ProfessionalInk,
            fontFamily = if (style == ResumeTemplateRenderer.CambridgeClassic) FontFamily.Serif else FontFamily.SansSerif,
            fontSize = if (style == ResumeTemplateRenderer.LimaProfessional) 18.sp else 25.sp,
            lineHeight = 26.sp,
            fontWeight = if (style in setOf(ResumeTemplateRenderer.BerlinStructured, ResumeTemplateRenderer.LimaProfessional)) FontWeight.Bold else FontWeight.Normal,
            textAlign = if (style in setOf(ResumeTemplateRenderer.CambridgeClassic, ResumeTemplateRenderer.LimaProfessional)) TextAlign.Center else TextAlign.Start,
        )
        if (contact.professionalTitle.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = contact.professionalTitle,
                color = ProfessionalInk,
                fontSize = 10.5.sp,
                lineHeight = 12.sp,
                textAlign = if (style in setOf(ResumeTemplateRenderer.CambridgeClassic, ResumeTemplateRenderer.LimaProfessional)) TextAlign.Center else TextAlign.Start,
            )
        }
        val address = listOf(contact.address, contact.city)
            .filter(String::isNotBlank)
            .distinct()
            .joinToString(", ")
        val contactLine = listOf(contact.phone, contact.email, address)
            .filter(String::isNotBlank)
            .joinToString("   |   ")
        if (contactLine.isNotBlank()) {
            Spacer(Modifier.height(9.dp))
            Text(
                text = contactLine,
                color = ProfessionalMuted,
                fontSize = 8.sp,
                lineHeight = 11.sp,
                textAlign = if (style in setOf(ResumeTemplateRenderer.CambridgeClassic, ResumeTemplateRenderer.LimaProfessional)) TextAlign.Center else TextAlign.Start,
            )
        }
    }
}

@Composable
private fun ProfessionalLinksSection(resume: ResumeDocument, style: ResumeTemplateRenderer, showSectionTitles: Boolean) {
    if (ResumeOptionalSection.Links in resume.optionalSections && resume.links.isNotEmpty()) {
        if (showSectionTitles) ProfessionalSectionGap()
        if (showSectionTitles) ProfessionalSectionTitle(style, stringResource(R.string.ui_links_d38518))
        ProfessionalLinks(resume)
    }
}

@Composable
private fun ProfessionalLinks(resume: ResumeDocument) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        resume.links.forEach { link ->
            Text(buildAnnotatedString {
                if (link.name.isNotBlank()) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(link.name)
                        if (link.url.isNotBlank()) append(':')
                    }
                    if (link.url.isNotBlank()) append(' ')
                }
                append(link.url)
            },
                color = ProfessionalMuted, fontSize = 8.sp, lineHeight = 11.sp)
        }
    }
}

@Composable
private fun ProfessionalSectionTitle(style: ResumeTemplateRenderer, text: String) {
    val lima = style == ResumeTemplateRenderer.LimaProfessional
    if (lima) {
        HorizontalDivider(color = ProfessionalInk, thickness = 0.6.dp)
        Spacer(Modifier.height(6.dp))
    }
    Text(text.uppercase(), modifier = Modifier.fillMaxWidth(), color = ProfessionalInk,
        fontSize = if (lima) 11.sp else 10.sp, fontWeight = FontWeight.Bold,
        lineHeight = if (lima) 14.sp else TextUnit.Unspecified,
        textAlign = if (lima) TextAlign.Center else TextAlign.Start,
        fontFamily = if (style == ResumeTemplateRenderer.CambridgeClassic) FontFamily.Serif else FontFamily.SansSerif)
    Spacer(Modifier.height(4.dp))
    if (!lima) HorizontalDivider(color = ProfessionalInk,
        thickness = if (style == ResumeTemplateRenderer.BerlinStructured) 1.dp else 0.6.dp)
    if (!lima) Spacer(Modifier.height(9.dp))
}

@Composable
private fun ProfessionalEntry(
    style: ResumeTemplateRenderer,
    title: String,
    organization: String,
    location: String,
    date: String,
    description: String = "",
    descriptionFormatting: ResumeTextFormatting = ResumeTextFormatting(),
) {
    if (style == ResumeTemplateRenderer.LimaProfessional) {
        Text(organization, color = ProfessionalInk, fontSize = 10.sp, lineHeight = 13.sp, fontWeight = FontWeight.Bold)
        Text(listOf(title, location, date).filter(String::isNotBlank).joinToString(" | "),
            color = ProfessionalInk, fontSize = 8.sp, lineHeight = 12.sp)
    } else if (style == ResumeTemplateRenderer.BerlinStructured) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                Text(title, color = ProfessionalInk, fontSize = 9.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
                Text(listOf(organization, location).filter(String::isNotBlank).joinToString(", "),
                    color = ProfessionalMuted, fontSize = 8.sp, lineHeight = 11.sp)
            }
            Text(date, modifier = Modifier.width(90.dp), color = ProfessionalMuted,
                fontSize = 8.sp, lineHeight = 11.sp, textAlign = TextAlign.End)
        }
    } else {
        Text(listOf(title, organization, location, date).filter(String::isNotBlank).joinToString("  |  "),
            color = ProfessionalInk, fontSize = 8.5.sp, lineHeight = 11.sp, fontWeight = FontWeight.Bold)
    }
    if (description.isNotBlank()) {
        Spacer(Modifier.height(4.dp))
        ExperienceDescription(
            text = description,
            formatting = descriptionFormatting,
            modifier = if (style == ResumeTemplateRenderer.LimaProfessional) {
                Modifier.fillMaxWidth().drawBehind {
                    drawLine(ProfessionalInk, Offset.Zero, Offset(0f, size.height), strokeWidth = 0.6.dp.toPx())
                }.padding(start = 9.dp)
            } else Modifier.fillMaxWidth(),
            color = ProfessionalInk,
            fontSize = 8.sp,
            lineHeight = 11.5.sp,
        )
    }
}

@Composable
private fun ProfessionalFormattedBody(text: String, formatting: ResumeTextFormatting) {
    Text(
        text = formattedText(text, formatting),
        modifier = Modifier.fillMaxWidth(),
        color = ProfessionalInk,
        fontSize = 8.sp,
        lineHeight = 11.5.sp,
    )
}

@Composable
private fun ProfessionalBulletLabel(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(3.5.dp)
                .background(ProfessionalInk, CircleShape),
        )
        Spacer(Modifier.width(4.dp))
        Text(text, color = ProfessionalInk, fontSize = 8.sp, lineHeight = 11.sp)
    }
}

@Composable
private fun ProfessionalSectionGap() {
    Spacer(Modifier.height(13.dp))
}

private fun professionalDate(start: String, end: String): String =
    listOf(start, end).filter(String::isNotBlank).joinToString(" - ")
