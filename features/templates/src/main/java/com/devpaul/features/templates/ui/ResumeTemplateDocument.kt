package com.devpaul.features.templates.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
 import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.core.domain.resume.ResumeCourse
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeEducation
import com.devpaul.core.domain.resume.ResumeExperience
import com.devpaul.core.domain.resume.ResumeLanguage
import com.devpaul.core.domain.resume.ResumeLink
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.core.domain.resume.ResumeReference
import com.devpaul.core.domain.resume.ResumeTextAlignment
import com.devpaul.core.domain.resume.ResumeTextFormatting
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateRenderer

private val DocumentInk = Color(0xFF414143)
private val DocumentMuted = Color(0xFF73757A)
private val DocumentLine = Color(0xFF515154)
private val SidebarBackground = Color(0xFF414143)
private val SidebarText = Color(0xFFF8F8F8)

@Composable
fun ResumeTemplateDocument(
    resume: ResumeDocument,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(0.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFD5D8DE)),
    ) {
        when (ResumeTemplateCatalog.find(resume.templateName).renderer) {
            ResumeTemplateRenderer.ClassicSidebar -> ClassicSidebarTemplate(resume)
            ResumeTemplateRenderer.EditorialLine -> EditorialLineTemplate(resume)
            ResumeTemplateRenderer.PragueEditorial -> PragueEditorialTemplate(resume)
            ResumeTemplateRenderer.TokyoCompact -> TokyoCompactTemplate(resume)
        }
    }
}

@Composable
private fun ClassicSidebarTemplate(resume: ResumeDocument) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        ClassicSidebar(
            resume = resume,
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.28f),
        )
        ClassicMainContent(
            resume = resume,
            modifier = Modifier
                .weight(0.72f),
        )
    }
}

@Composable
private fun ClassicSidebar(
    resume: ResumeDocument,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(SidebarBackground)
            .padding(start = 18.dp, top = 26.dp, end = 18.dp, bottom = 26.dp),
    ) {
        val showLinks = ResumeOptionalSection.Links in resume.optionalSections && resume.links.isNotEmpty()
        val showLanguages = ResumeOptionalSection.Languages in resume.optionalSections && resume.languages.isNotEmpty()
        val showReferences = ResumeOptionalSection.References in resume.optionalSections && resume.references.isNotEmpty()

        if (resume.summary.isNotBlank()) {
            SidebarTitle("SOBRE MÍ")
            Text(
                text = formattedText(resume.summary, resume.summaryFormatting),
                color = SidebarText,
                fontSize = 7.8.sp,
                lineHeight = 10.5.sp,
            )
            if (showLinks || showLanguages || showReferences) SidebarDivider()
        }
        if (showLinks) {
            SidebarTitle("ENLACES")
            resume.links.forEachIndexed { index, link ->
                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(fontSize = 8.sp, fontWeight = FontWeight.Bold))
                        append(link.name.replaceFirstChar { it.uppercase() })
                        append(':')
                        pop()
                        append('\n')
                        pushStyle(SpanStyle(fontSize = 7.8.sp, textDecoration = TextDecoration.Underline))
                        append(link.url)
                        pop()
                    },
                    color = SidebarText,
                    fontSize = 7.8.sp,
                    lineHeight = 10.sp,
                )
                if (index != resume.links.lastIndex) Spacer(Modifier.height(7.dp))
            }
            if (showLanguages || showReferences) SidebarDivider()
        }
        if (showLanguages) {
            SidebarTitle("IDIOMAS")
            resume.languages.forEachIndexed { index, language ->
                Text(
                    text = language.language.uppercase(),
                    color = SidebarText,
                    fontSize = 8.sp,
                    lineHeight = 10.sp,
                )
                Spacer(Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.5.dp)
                        .background(Color.White.copy(alpha = 0.3f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(languageProgress(language.level))
                            .fillMaxHeight()
                            .background(Color.White),
                    )
                }
                if (index != resume.languages.lastIndex) Spacer(Modifier.height(9.dp))
            }
            if (showReferences) SidebarDivider()
        }
        if (showReferences) {
            SidebarTitle("REFERENCIAS")
            resume.references.forEachIndexed { index, reference ->
                Text(
                    text = reference.name.uppercase(),
                    color = SidebarText,
                    fontSize = 8.5.sp,
                    lineHeight = 10.5.sp,
                    fontWeight = FontWeight.Normal,
                )
                CompactSidebarText(reference.company)
                CompactSidebarText(reference.phone, prefix = "T:")
                CompactSidebarText(reference.email, prefix = "C:")
                if (index != resume.references.lastIndex) Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ClassicMainContent(
    resume: ResumeDocument,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 18.dp, vertical = 24.dp),
    ) {
        ResumeHeader(resume)
        Spacer(Modifier.height(16.dp))

        if (resume.experiences.isNotEmpty()) {
            MainSectionTitle("EXPERIENCIA LABORAL")
            resume.experiences.forEach { experience ->
                TimelineEntry(
                    organization = experience.company.uppercase(),
                    location = experience.city,
                    date = compactDate(experience.startYear, experience.endYear),
                    title = experience.position,
                    description = experience.achievements,
                    descriptionFormatting = experience.achievementsFormatting,
                )
            }
        }
        if (resume.skills.isNotEmpty()) {
            SectionGap()
            MainSectionTitle("HABILIDADES")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                resume.skills.forEach { skill -> SkillLabel(skill.uppercase()) }
            }
        }
        if (ResumeOptionalSection.Courses in resume.optionalSections && resume.courses.isNotEmpty()) {
            SectionGap()
            MainSectionTitle("CURSOS")
            resume.courses.forEach { course ->
                TimelineEntry(
                    organization = course.institution.uppercase(),
                    location = "",
                    date = compactDate(course.startDate, course.endDate),
                    title = course.name,
                )
            }
        }
        if (resume.education.isNotEmpty()) {
            SectionGap()
            MainSectionTitle("EDUCACIÓN")
            resume.education.forEach { education ->
                TimelineEntry(
                    organization = education.institution.uppercase(),
                    location = education.city,
                    date = compactDate(education.startYear, education.endYear),
                    title = education.degree,
                )
            }
        }
    }
}

@Composable
private fun ResumeHeader(resume: ResumeDocument) {
    val contact = resume.contact
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1.05f)) {
            Text(
                text = listOf(contact.name, contact.lastName)
                    .filter(String::isNotBlank)
                    .joinToString("\n")
                    .uppercase(),
                color = DocumentInk,
                fontSize = 16.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Black,
            )
            if (contact.professionalTitle.isNotBlank()) {
                Spacer(Modifier.height(1.5.dp))
                Text(
                    text = contact.professionalTitle.uppercase(),
                    color = DocumentInk,
                    fontSize = 7.sp,
                    lineHeight = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Column(modifier = Modifier.weight(0.95f)) {
            val contactLines = listOf(
                Icons.Outlined.LocationOn to listOf(contact.address, contact.city)
                    .filter(String::isNotBlank)
                    .joinToString(", "),
                Icons.Outlined.Phone to contact.phone,
                Icons.Outlined.Email to contact.email,
            ).filter { it.second.isNotBlank() }
            contactLines.forEachIndexed { index, (icon, text) ->
                ContactLine(icon, text, showDivider = index < contactLines.lastIndex)
            }
        }
    }
}

@Composable
private fun ContactLine(icon: ImageVector, text: String, showDivider: Boolean) {
    if (text.isBlank()) return
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(vertical = 1.5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .background(DocumentInk, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(9.dp), tint = Color.White)
            }
            Spacer(Modifier.width(4.dp))
            Text(text, color = DocumentMuted, fontSize = 6.2.sp, lineHeight = 8.sp)
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 19.dp),
                color = Color(0xFFE0E1E4),
                thickness = 0.6.dp,
            )
        }
    }
}

@Composable
private fun MainSectionTitle(text: String) {
    Text(text, color = DocumentInk, fontSize = 9.sp, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(3.dp))
    HorizontalDivider(color = DocumentLine, thickness = 0.7.dp)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun TimelineEntry(
    organization: String,
    location: String,
    date: String,
    title: String,
    description: String = "",
    descriptionFormatting: ResumeTextFormatting = ResumeTextFormatting(),
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier
                .width(56.dp)
                .padding(end = 4.dp, bottom = 7.dp),
        ) {
            Text(organization, color = DocumentInk, fontSize = 6.2.sp, lineHeight = 7.8.sp, fontWeight = FontWeight.Bold)
            if (location.isNotBlank()) Text(location, color = DocumentMuted, fontSize = 5.8.sp, lineHeight = 7.2.sp)
            if (date.isNotBlank()) Text(date, color = DocumentMuted, fontSize = 5.8.sp, lineHeight = 7.2.sp)
        }
        Box(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(Modifier.width(1.dp).fillMaxHeight().background(DocumentLine))
            Box(Modifier.padding(top = 1.dp).size(5.dp).background(DocumentLine, CircleShape))
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp, bottom = 8.dp),
        ) {
            Text(title, color = DocumentInk, fontSize = 7.sp, lineHeight = 8.5.sp, fontWeight = FontWeight.Bold)
            if (description.isNotBlank()) {
                Spacer(Modifier.height(3.dp))
                Text(
                    text = formattedText(description, descriptionFormatting),
                    modifier = Modifier.fillMaxWidth(),
                    color = DocumentMuted,
                    fontSize = 6.2.sp,
                    lineHeight = 8.5.sp,
                )
            }
        }
    }
}

@Composable
private fun SkillLabel(text: String) {
    Surface(
        shape = RoundedCornerShape(1.dp),
        color = Color.White,
        border = BorderStroke(0.6.dp, Color(0xFFB8BAC0)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
            color = DocumentMuted,
            fontSize = 5.2.sp,
            lineHeight = 6.2.sp,
        )
    }
}

@Composable
private fun SidebarTitle(text: String) {
    Text(text, color = SidebarText, fontSize = 10.sp, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SidebarDivider() {
    Spacer(Modifier.height(12.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.38f), thickness = 0.8.dp)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun CompactSidebarText(text: String, prefix: String = "") {
    if (text.isNotBlank()) {
        Text(
            text = if (prefix.isBlank()) text else "$prefix $text",
            color = SidebarText,
            fontSize = 8.sp,
            lineHeight = 10.5.sp,
        )
    }
}

@Composable
private fun SectionGap() = Spacer(Modifier.height(14.dp))

private fun compactDate(start: String, end: String): String =
    listOf(start, end).filter(String::isNotBlank).joinToString(" - ")

private fun languageProgress(level: String): Float = when (level.lowercase()) {
    "nativo" -> 1f
    "avanzado" -> 0.85f
    "intermedio" -> 0.65f
    else -> 0.4f
}

internal fun formattedText(text: String, formatting: ResumeTextFormatting): AnnotatedString =
    buildAnnotatedString {
        append(text)
        formatting.spans.forEach { span ->
            val start = span.start.coerceIn(0, text.length)
            val end = span.end.coerceIn(start, text.length)
            if (start == end) return@forEach
            addStyle(
                SpanStyle(
                    fontWeight = if (span.bold) FontWeight.Bold else null,
                    fontStyle = if (span.italic) FontStyle.Italic else null,
                    textDecoration = when {
                        span.underline && span.strikethrough -> TextDecoration.combine(
                            listOf(TextDecoration.Underline, TextDecoration.LineThrough),
                        )
                        span.underline -> TextDecoration.Underline
                        span.strikethrough -> TextDecoration.LineThrough
                        else -> null
                    },
                    color = span.textColorArgb?.let(::Color) ?: Color.Unspecified,
                    background = span.backgroundColorArgb?.let(::Color) ?: Color.Unspecified,
                ),
                start,
                end,
            )
        }
        formatting.paragraphs.forEach { paragraph ->
            val start = paragraph.start.coerceIn(0, text.length)
            val end = paragraph.end.coerceIn(start, text.length)
            if (start < end) {
                addStyle(
                    ParagraphStyle(
                        textAlign = when (paragraph.alignment) {
                            ResumeTextAlignment.Start -> TextAlign.Start
                            ResumeTextAlignment.Center -> TextAlign.Center
                            ResumeTextAlignment.End -> TextAlign.End
                            ResumeTextAlignment.Justify -> TextAlign.Justify
                        },
                    ),
                    start,
                    end,
                )
            }
        }
    }

@Preview(
    name = "Currículum completo",
    showBackground = true,
)
@Composable
private fun ResumeTemplateDocumentPreview() {
    ResumeTemplateDocument(
        resume = previewDocument(),
        modifier = Modifier.fillMaxWidth(),
    )
}

fun previewDocument() = ResumeDocument(
    optionalSections = setOf(
        ResumeOptionalSection.Courses,
        ResumeOptionalSection.Links,
        ResumeOptionalSection.Languages,
        ResumeOptionalSection.References,
    ),
    contact = ResumeContact(
        name = "Paul Antony",
        lastName = "Guillen Acuña",
        professionalTitle = "Ingeniero de Sistemas",
        email = "paulguillen190@gmail.com",
        phone = "+51 977 588 534",
        city = "Lima",
        address = "Lima, Perú",
    ),
    summary = "Desarrollador Android Senior con experiencia creando aplicaciones nativas, escalables y mantenibles con Kotlin y Jetpack Compose.",
    links = listOf(
        ResumeLink("LinkedIn", "https://linkedin.com/in/paulguillen"),
        ResumeLink("GitHub", "https://github.com/paulguillen"),
        ResumeLink("Portafolio", "https://paulguillen.dev"),
    ),
    languages = listOf(
        ResumeLanguage("Español", "Nativo"),
        ResumeLanguage("Inglés", "Intermedio"),
    ),
    references = listOf(
        ResumeReference("Renctio", "Líder Técnico", "+51 977 588 561", "test@gmail.com"),
        ResumeReference("Miguel Ángel Pacheco", "Indra Company", "+51 943 603 363", "mapacheco@indra.com"),
    ),
    experiences = listOf(
        ResumeExperience(
            position = "Mobile Developer - APP",
            company = "Scotiabank",
            city = "Lima",
            startYear = "2022",
            endYear = "Actualidad",
            achievements = "Desarrollo de aplicaciones nativas con Kotlin y arquitectura limpia. Implementación de nuevas funcionalidades y pruebas automatizadas.",
        ),
    ),
    skills = listOf("Kotlin", "Android", "Jetpack Compose", "Clean Architecture", "Git"),
    courses = listOf(
        ResumeCourse("Android Avanzado", "IDAT", "2024", "2025"),
    ),
    education = listOf(
        ResumeEducation(
            institution = "Universidad Autónoma del Perú",
            degree = "Ingeniería de Sistemas",
            city = "Lima",
            startYear = "2016",
            endYear = "2021",
        ),
    ),
)
