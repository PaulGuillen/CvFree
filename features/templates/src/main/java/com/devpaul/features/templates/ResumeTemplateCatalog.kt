package com.devpaul.features.templates

data class ResumeTemplateDefinition(
    val id: String,
    val displayName: String,
    val description: String,
    val hasSidebar: Boolean,
    val renderer: ResumeTemplateRenderer,
    val legacyNames: Set<String> = emptySet(),
)

enum class ResumeTemplateRenderer {
    ClassicSidebar,
    EditorialLine,
    PragueEditorial,
}

object ResumeTemplateCatalog {
    const val CLASSIC_SIDEBAR_ID = "classic_sidebar"
    const val EDITORIAL_LINE_ID = "editorial_line"
    const val PRAGUE_EDITORIAL_ID = "prague_editorial"

    val templates: List<ResumeTemplateDefinition> = listOf(
        ResumeTemplateDefinition(
            id = CLASSIC_SIDEBAR_ID,
            displayName = "Clásico con barra lateral",
            description = "Diseño profesional de dos columnas con títulos en mayúsculas.",
            hasSidebar = true,
            renderer = ResumeTemplateRenderer.ClassicSidebar,
            legacyNames = setOf("Clásico Sidebar"),
        ),
        ResumeTemplateDefinition(
            id = EDITORIAL_LINE_ID,
            displayName = "Línea editorial",
            description = "Estilo editorial en blanco y negro con columnas y divisores finos.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.EditorialLine,
            legacyNames = setOf("Editorial Line"),
        ),
        ResumeTemplateDefinition(
            id = PRAGUE_EDITORIAL_ID,
            displayName = "Praga",
            description = "Diseño sobrio con encabezado dividido y secciones estructuradas.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.PragueEditorial,
            legacyNames = setOf("Prague"),
        ),
    )

    val default: ResumeTemplateDefinition = templates.first()

    fun find(idOrLegacyName: String): ResumeTemplateDefinition = templates.firstOrNull { template ->
        template.id == idOrLegacyName ||
            template.displayName.equals(idOrLegacyName, ignoreCase = true) ||
            template.legacyNames.any { it.equals(idOrLegacyName, ignoreCase = true) }
    } ?: default
}
