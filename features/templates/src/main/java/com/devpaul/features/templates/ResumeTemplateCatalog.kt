package com.devpaul.features.templates

data class ResumeTemplateDefinition(
    val id: String,
    val displayName: String,
    val description: String,
    val hasSidebar: Boolean,
    val renderer: ResumeTemplateRenderer,
)

enum class ResumeTemplateRenderer {
    ClassicSidebar,
}

object ResumeTemplateCatalog {
    const val CLASSIC_SIDEBAR_ID = "classic_sidebar"

    val templates: List<ResumeTemplateDefinition> = listOf(
        ResumeTemplateDefinition(
            id = CLASSIC_SIDEBAR_ID,
            displayName = "Clásico Sidebar",
            description = "Diseño profesional de dos columnas con títulos en mayúsculas.",
            hasSidebar = true,
            renderer = ResumeTemplateRenderer.ClassicSidebar,
        ),
    )

    val default: ResumeTemplateDefinition = templates.first()

    fun find(idOrLegacyName: String): ResumeTemplateDefinition = templates.firstOrNull { template ->
        template.id == idOrLegacyName || template.displayName.equals(idOrLegacyName, ignoreCase = true)
    } ?: default
}
