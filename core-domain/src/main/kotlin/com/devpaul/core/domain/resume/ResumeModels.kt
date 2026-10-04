package com.devpaul.core.domain.resume

import java.util.UUID

fun newResumeEntryId(): String = UUID.randomUUID().toString()

enum class ResumeOptionalSection {
    Courses,
    Links,
    Languages,
    References,
}

enum class ResumeTextAlignment {
    Start,
    Center,
    End,
    Justify,
}

data class ResumeTextSpan(
    val start: Int,
    val end: Int,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val strikethrough: Boolean = false,
    val textColorArgb: Long? = null,
    val backgroundColorArgb: Long? = null,
    val url: String? = null,
)

data class ResumeParagraphStyle(
    val start: Int,
    val end: Int,
    val alignment: ResumeTextAlignment = ResumeTextAlignment.Start,
)

data class ResumeTextFormatting(
    val spans: List<ResumeTextSpan> = emptyList(),
    val paragraphs: List<ResumeParagraphStyle> = emptyList(),
)

data class ResumeContact(
    val name: String = "",
    val lastName: String = "",
    val professionalTitle: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val address: String = "",
)

data class ResumeExperience(
    val position: String = "",
    val company: String = "",
    val city: String = "",
    val startYear: String = "",
    val endYear: String = "",
    val achievements: String = "",
    val achievementsFormatting: ResumeTextFormatting = ResumeTextFormatting(),
    val id: String = newResumeEntryId(),
)

data class ResumeEducation(
    val institution: String = "",
    val degree: String = "",
    val city: String = "",
    val startYear: String = "",
    val endYear: String = "",
    val id: String = newResumeEntryId(),
)

data class ResumeCourse(
    val name: String = "",
    val institution: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val id: String = newResumeEntryId(),
)

data class ResumeLink(
    val name: String = "",
    val url: String = "",
    val id: String = newResumeEntryId(),
)

data class ResumeLanguage(
    val language: String = "",
    val level: String = "",
    val id: String = newResumeEntryId(),
)

data class ResumeReference(
    val name: String = "",
    val company: String = "",
    val phone: String = "",
    val email: String = "",
    val id: String = newResumeEntryId(),
)

data class ResumeDocument(
    val id: String = "",
    val updatedAt: Long = 0L,
    val templateName: String = "classic_sidebar",
    val optionalSections: Set<ResumeOptionalSection> = emptySet(),
    val contact: ResumeContact = ResumeContact(),
    val experiences: List<ResumeExperience> = emptyList(),
    val education: List<ResumeEducation> = emptyList(),
    val skills: List<String> = emptyList(),
    val summary: String = "",
    val summaryFormatting: ResumeTextFormatting = ResumeTextFormatting(),
    val courses: List<ResumeCourse> = emptyList(),
    val links: List<ResumeLink> = emptyList(),
    val languages: List<ResumeLanguage> = emptyList(),
    val references: List<ResumeReference> = emptyList(),
) {
    val displayName: String
        get() = contact.professionalTitle.ifBlank {
            listOf(contact.name, contact.lastName)
                .filter(String::isNotBlank)
                .joinToString(" ")
                .ifBlank { "Mi currículum" }
        }
}
