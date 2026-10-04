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
    EditorialLine,
}

object ResumeTemplateCatalog {
    const val CLASSIC_SIDEBAR_ID = "classic_sidebar"
    const val EDITORIAL_LINE_ID = "editorial_line"

    val templates: List<ResumeTemplateDefinition> = listOf(
        ResumeTemplateDefinition(
            id = CLASSIC_SIDEBAR_ID,
            displayName = "Clásico Sidebar",
            description = "Diseño profesional de dos columnas con títulos en mayúsculas.",
            hasSidebar = true,
            renderer = ResumeTemplateRenderer.ClassicSidebar,
        ),
        ResumeTemplateDefinition(
            id = EDITORIAL_LINE_ID,
            displayName = "Editorial Line",
            description = "Estilo editorial en blanco y negro con columnas y divisores finos.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.EditorialLine,
        ),
    )

    val default: ResumeTemplateDefinition = templates.first()

    fun find(idOrLegacyName: String): ResumeTemplateDefinition = templates.firstOrNull { template ->
        template.id == idOrLegacyName || template.displayName.equals(idOrLegacyName, ignoreCase = true)
    } ?: default
}
