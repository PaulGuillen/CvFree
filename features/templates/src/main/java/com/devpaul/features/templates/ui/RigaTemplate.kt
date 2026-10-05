package com.devpaul.features.templates.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.core.domain.resume.ResumeTextFormatting
import com.devpaul.features.templates.ResumeTemplateCatalog

private val EditorialInk = Color(0xFF202020)
private val EditorialMuted = Color(0xFF777A80)
private val EditorialTrack = Color(0xFFC7C7C7)

@Composable
internal fun RigaTemplate(resume: ResumeDocument) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 40.dp, end = 40.dp, bottom = 34.dp),
    ) {
        RigaHeader(resume)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            EditorialSidebar(
                resume = resume,
                modifier = Modifier
                    .weight(0.33f)
                    .padding(end = 20.dp),
            )
            Box(
                modifier = Modifier
                    .width(0.8.dp)
                    .fillMaxHeight()
                    .background(EditorialInk),
            )
            EditorialMainContent(
                resume = resume,
                modifier = Modifier
                    .weight(0.67f)
                    .padding(start = 25.dp),
            )
        }
    }
}

@Composable
private fun RigaHeader(resume: ResumeDocument) {
    val contact = resume.contact
    Column(modifier = Modifier.fillMaxWidth()) {
        RigaHeaderVerticalLine(height = 36.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(0.72f)
                    .padding(end = 24.dp),
            ) {
                Text(
                    text = listOf(contact.name, contact.lastName)
                        .filter(String::isNotBlank)
                        .joinToString(" ")
                        .ifBlank { "TU NOMBRE" }
                        .uppercase(),
                    color = EditorialInk,
                    fontSize = 24.sp,
                    lineHeight = 27.sp,
                    fontWeight = FontWeight.Light,
                )
                if (contact.professionalTitle.isNotBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Text(
                        text = contact.professionalTitle.uppercase(),
                        color = EditorialMuted,
                        fontSize = 8.5.sp,
                        lineHeight = 10.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }
            Box(
                modifier = Modifier.weight(0.28f),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, EditorialInk),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = editorialInitials(contact.name, contact.lastName),
                            color = EditorialMuted,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Light,
                        )
                    }
                }
            }
        }
        RigaHeaderVerticalLine(height = 30.dp)
    }
}

@Composable
private fun RigaHeaderVerticalLine(height: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Spacer(Modifier.weight(0.33f))
        Box(
            modifier = Modifier
                .width(0.8.dp)
                .fillMaxHeight()
                .background(EditorialInk),
        )
        Spacer(Modifier.weight(0.67f))
    }
}

@Composable
private fun EditorialSidebar(
    resume: ResumeDocument,
    modifier: Modifier = Modifier,
) {
    val contact = resume.contact
    val showLinks = ResumeOptionalSection.Links in resume.optionalSections && resume.links.isNotEmpty()
    val showLanguages = ResumeOptionalSection.Languages in resume.optionalSections && resume.languages.isNotEmpty()
    val showReferences = ResumeOptionalSection.References in resume.optionalSections && resume.references.isNotEmpty()

    Column(modifier = modifier) {
        EditorialSideTitle("CONTACTO")
        EditorialContactValue(contact.phone)
        EditorialContactValue(contact.email)
        EditorialContactValue(
            listOf(contact.address, contact.city)
                .filter(String::isNotBlank)
                .joinToString(", "),
        )

        if (resume.summary.isNotBlank()) {
            EditorialSideDivider()
            EditorialSideTitle("SOBRE MI")
            Text(
                text = formattedText(resume.summary, resume.summaryFormatting),
                color = EditorialMuted,
                fontSize = 7.2.sp,
                lineHeight = 10.sp,
            )
        }

        if (showLinks) {
            EditorialSideDivider()
            EditorialSideTitle("ENLACES")
            resume.links.forEachIndexed { index, link ->
                Text(
                    text = link.name.replaceFirstChar { it.uppercase() } + ":",
                    color = EditorialInk,
                    fontSize = 7.2.sp,
                    lineHeight = 9.sp,
                )
                Text(
                    text = link.url,
                    color = EditorialInk,
                    fontSize = 6.6.sp,
                    lineHeight = 9.sp,
                    textDecoration = TextDecoration.Underline,
                )
                if (index != resume.links.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        if (showLanguages) {
            EditorialSideDivider()
            EditorialSideTitle("IDIOMAS")
            resume.languages.forEachIndexed { index, language ->
                EditorialProgressBar(
                    label = language.language.uppercase(),
                    progress = editorialLanguageProgress(language.level),
                )
                if (index != resume.languages.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        if (showReferences) {
            EditorialSideDivider()
            EditorialSideTitle("REFERENCIAS")
            resume.references.forEachIndexed { index, reference ->
                Text(
                    text = reference.name.uppercase(),
                    color = EditorialInk,
                    fontSize = 7.1.sp,
                    lineHeight = 8.8.sp,
                    fontWeight = FontWeight.Bold,
                )
                EditorialSmallText(reference.company)
                EditorialSmallText(reference.phone, "T:")
                EditorialSmallText(reference.email, "C:")
                if (index != resume.references.lastIndex) Spacer(Modifier.height(11.dp))
            }
        }
    }
}

@Composable
private fun EditorialMainContent(
    resume: ResumeDocument,
    modifier: Modifier = Modifier,
) {
    val showCourses = ResumeOptionalSection.Courses in resume.optionalSections && resume.courses.isNotEmpty()

    Column(modifier = modifier) {
        if (resume.experiences.isNotEmpty()) {
            EditorialMainTitle("EXPERIENCIA LABORAL")
            resume.experiences.forEachIndexed { index, experience ->
                EditorialEntry(
                    title = experience.position,
                    organization = experience.company,
                    location = experience.city,
                    date = editorialDate(experience.startYear, experience.displayEndYear),
                    description = experience.achievements,
                    descriptionFormatting = experience.achievementsFormatting,
                )
                if (index != resume.experiences.lastIndex) Spacer(Modifier.height(10.dp))
            }
        }

        if (resume.education.isNotEmpty()) {
            if (resume.experiences.isNotEmpty()) EditorialMainGap()
            EditorialMainTitle("EDUCACION")
            resume.education.forEachIndexed { index, education ->
                EditorialEntry(
                    title = education.degree,
                    organization = education.institution,
                    location = education.city,
                    date = editorialDate(education.startYear, education.endYear),
                )
                if (index != resume.education.lastIndex) Spacer(Modifier.height(9.dp))
            }
        }

        if (resume.skills.isNotEmpty()) {
            if (resume.experiences.isNotEmpty() || resume.education.isNotEmpty()) EditorialMainGap()
            EditorialMainTitle("HABILIDADES")
            val skillRows = resume.skills.chunked(2)
            skillRows.forEachIndexed { rowIndex, skills ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    skills.forEach { skill ->
                        EditorialSkill(skill, Modifier.weight(1f))
                    }
                    if (skills.size == 1) Spacer(Modifier.weight(1f))
                }
                if (rowIndex != skillRows.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }

        if (showCourses) {
            if (
                resume.experiences.isNotEmpty() ||
                resume.education.isNotEmpty() ||
                resume.skills.isNotEmpty()
            ) {
                EditorialMainGap()
            }
            EditorialMainTitle("CURSOS")
            resume.courses.forEachIndexed { index, course ->
                EditorialEntry(
                    title = course.name,
                    organization = course.institution,
                    location = "",
                    date = editorialDate(course.startDate, course.endDate),
                )
                if (index != resume.courses.lastIndex) Spacer(Modifier.height(9.dp))
            }
        }
    }
}

@Composable
private fun EditorialEntry(
    title: String,
    organization: String,
    location: String,
    date: String,
    description: String = "",
    descriptionFormatting: ResumeTextFormatting = ResumeTextFormatting(),
) {
    Text(
        text = buildString {
            append(title)
            if (organization.isNotBlank()) append(" - ").append(organization)
            if (location.isNotBlank()) append(", ").append(location)
        },
        color = EditorialInk,
        fontSize = 7.4.sp,
        lineHeight = 9.sp,
        fontWeight = FontWeight.Bold,
    )
    if (date.isNotBlank()) {
        Spacer(Modifier.height(1.5.dp))
        Text(date, color = EditorialMuted, fontSize = 6.5.sp, lineHeight = 8.sp)
    }
    if (description.isNotBlank()) {
        Spacer(Modifier.height(4.dp))
        ExperienceDescription(
            text = description,
            formatting = descriptionFormatting,
            modifier = Modifier.fillMaxWidth(),
            color = EditorialMuted,
            fontSize = 6.6.sp,
            lineHeight = 9.sp,
        )
    }
}

@Composable
private fun EditorialSkill(skill: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = skill.uppercase(),
            color = EditorialMuted,
            fontSize = 6.4.sp,
            lineHeight = 8.sp,
        )
        Spacer(Modifier.height(2.dp))
        Box(Modifier.fillMaxWidth().height(2.5.dp).background(EditorialInk))
    }
}

@Composable
private fun EditorialProgressBar(label: String, progress: Float) {
    Text(label, color = EditorialMuted, fontSize = 6.6.sp, lineHeight = 8.sp)
    Spacer(Modifier.height(2.dp))
    Box(Modifier.fillMaxWidth().height(2.5.dp).background(EditorialTrack)) {
        Box(
            Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .background(EditorialInk),
        )
    }
}

@Composable
private fun EditorialContactValue(value: String) {
    if (value.isBlank()) return
    Text(
        text = value,
        modifier = Modifier.padding(bottom = 5.dp),
        color = EditorialMuted,
        fontSize = 6.8.sp,
        lineHeight = 8.5.sp,
    )
}

@Composable
private fun EditorialSmallText(value: String, prefix: String = "") {
    if (value.isBlank()) return
    Text(
        text = if (prefix.isBlank()) value else "$prefix $value",
        color = EditorialMuted,
        fontSize = 6.5.sp,
        lineHeight = 8.5.sp,
    )
}

@Composable
private fun EditorialSideTitle(text: String) {
    Text(
        text = text,
        color = EditorialInk,
        fontSize = 9.5.sp,
        lineHeight = 11.sp,
        fontWeight = FontWeight.Normal,
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun EditorialSideDivider() {
    Spacer(Modifier.height(14.dp))
    HorizontalDivider(color = EditorialInk, thickness = 0.7.dp)
    Spacer(Modifier.height(14.dp))
}

@Composable
private fun EditorialMainTitle(text: String) {
    HorizontalDivider(color = EditorialInk, thickness = 0.7.dp)
    Spacer(Modifier.height(12.dp))
    Text(
        text = text,
        color = EditorialInk,
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Normal,
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun EditorialMainGap() = Spacer(Modifier.height(17.dp))

private fun editorialDate(start: String, end: String): String =
    listOf(start, end).filter(String::isNotBlank).joinToString(" - ")

private fun editorialLanguageProgress(level: String): Float = when (level.lowercase()) {
    "nativo" -> 1f
    "avanzado" -> 0.85f
    "intermedio" -> 0.65f
    else -> 0.4f
}

private fun editorialInitials(name: String, lastName: String): String =
    listOf(name, lastName)
        .mapNotNull { value -> value.trim().firstOrNull()?.uppercase() }
        .joinToString("")
        .take(2)
        .ifBlank { "CV" }

@Preview(name = "Riga", showBackground = true)
@Composable
private fun RigaTemplatePreview() {
    ResumeTemplateDocument(
        resume = previewDocument().copy(templateName = ResumeTemplateCatalog.RIGA_ID),
        modifier = Modifier.fillMaxWidth(),
    )
}
