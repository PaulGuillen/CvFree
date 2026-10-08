package com.devpaul.features.home.ui.model

import com.devpaul.core.domain.resume.ResumeDocument

internal data class ResumeProgress(
    val percent: Int,
    val message: String,
)

internal fun resumeProgress(resume: ResumeDocument): ResumeProgress {
    val sections = listOf(
        "Información de contacto" to (
            resume.contact.name.isNotBlank() &&
                resume.contact.professionalTitle.isNotBlank() &&
                (resume.contact.email.isNotBlank() || resume.contact.phone.isNotBlank())
            ),
        "Experiencia laboral" to resume.experiences.any {
            it.position.isNotBlank() && it.company.isNotBlank()
        },
        "Educación" to resume.education.any {
            it.institution.isNotBlank() && it.degree.isNotBlank()
        },
        "Habilidades" to resume.skills.any(String::isNotBlank),
        "Resumen profesional" to resume.summary.isNotBlank(),
    )
    val completed = sections.count { it.second }
    val percent = completed * 100 / sections.size
    val missingSection = sections.firstOrNull { !it.second }?.first
    val message = if (missingSection == null) {
        "Tu perfil está completo"
    } else {
        val nextPercent = (completed + 1) * 100 / sections.size
        "Completa «$missingSection» para llegar al $nextPercent%"
    }
    return ResumeProgress(percent = percent, message = message)
}
