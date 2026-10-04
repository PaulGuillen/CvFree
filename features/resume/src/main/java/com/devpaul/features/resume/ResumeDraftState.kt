package com.devpaul.features.resume

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.core.domain.resume.ResumeCourse
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeEducation
import com.devpaul.core.domain.resume.ResumeExperience
import com.devpaul.core.domain.resume.ResumeLanguage
import com.devpaul.core.domain.resume.ResumeLink
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.core.domain.resume.ResumeReference
import com.devpaul.core.domain.resume.ResumeTextFormatting
import java.util.UUID

@Stable
class ResumeDraftState : ViewModel() {
    var document by mutableStateOf(defaultResume())
        private set

    fun startNew(optionalSections: Set<ResumeOptionalSection>) {
        document = defaultResume().copy(optionalSections = optionalSections)
    }

    fun load(resume: ResumeDocument) {
        document = resume
    }

    fun updateContact(value: ResumeContact) {
        document = document.copy(contact = value)
    }

    fun updateExperiences(value: List<ResumeExperience>) {
        document = document.copy(experiences = value)
    }

    fun updateEducation(value: List<ResumeEducation>) {
        document = document.copy(education = value)
    }

    fun updateSkills(value: List<String>) {
        document = document.copy(skills = value)
    }

    fun updateOptionalSections(value: Set<ResumeOptionalSection>) {
        document = document.copy(optionalSections = value)
    }

    fun updateSummary(value: String, formatting: ResumeTextFormatting = document.summaryFormatting) {
        document = document.copy(summary = value, summaryFormatting = formatting)
    }

    fun updateCourses(value: List<ResumeCourse>) {
        document = document.copy(courses = value)
    }

    fun updateLinks(value: List<ResumeLink>) {
        document = document.copy(links = value)
    }

    fun updateLanguages(value: List<ResumeLanguage>) {
        document = document.copy(languages = value)
    }

    fun updateReferences(value: List<ResumeReference>) {
        document = document.copy(references = value)
    }

    fun updateTemplate(value: String) {
        document = document.copy(templateName = value)
    }

    fun snapshotForSave(): ResumeDocument = document.copy(
        id = document.id.ifBlank { UUID.randomUUID().toString() },
        updatedAt = System.currentTimeMillis(),
    )

    private companion object {
        fun defaultResume() = ResumeDocument()
    }
}
