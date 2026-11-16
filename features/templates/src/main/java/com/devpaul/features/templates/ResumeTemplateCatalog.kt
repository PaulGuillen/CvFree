package com.devpaul.features.templates

data class ResumeTemplateDefinition(
    val id: String,
    val displayName: String,
    val description: String,
    val hasSidebar: Boolean,
    val renderer: ResumeTemplateRenderer,
    val legacyIdentifiers: Set<String> = emptySet(),
    val isNew: Boolean = false,
)

enum class ResumeTemplateRenderer {
    Budapest,
    Riga,
    PragueEditorial,
    TokyoCompact,
    CambridgeClassic,
    BerlinStructured,
    ViennaEditorial,
    OsloCompact,
    LimaProfessional,
}

object ResumeTemplateCatalog {
    const val BUDAPEST_ID = "budapest"
    const val RIGA_ID = "riga"
    const val PRAGUE_EDITORIAL_ID = "prague_editorial"
    const val TOKYO_COMPACT_ID = "tokyo_compact"

    const val CAMBRIDGE_CLASSIC_ID = "cambridge_classic"
    const val BERLIN_STRUCTURED_ID = "berlin_structured"

    const val VIENNA_EDITORIAL_ID = "vienna_editorial"
    const val OSLO_COMPACT_ID = "oslo_compact"

    const val LIMA_PROFESSIONAL_ID = "lima_professional"

    val templates: List<ResumeTemplateDefinition> = listOf(
        ResumeTemplateDefinition(
            id = BUDAPEST_ID,
            displayName = "Budapest",
            description = "Diseño profesional de dos columnas con títulos en mayúsculas.",
            hasSidebar = true,
            renderer = ResumeTemplateRenderer.Budapest,
            legacyIdentifiers = setOf("classic_sidebar", "Clásico Sidebar", "Clásico con barra lateral"),
        ),
        ResumeTemplateDefinition(
            id = RIGA_ID,
            displayName = "Riga",
            description = "Estilo editorial en blanco y negro con columnas y divisores finos.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.Riga,
            legacyIdentifiers = setOf("editorial_line", "Editorial Line", "Línea editorial"),
        ),
        ResumeTemplateDefinition(
            id = PRAGUE_EDITORIAL_ID,
            displayName = "Praga",
            description = "Diseño sobrio con encabezado dividido y secciones estructuradas.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.PragueEditorial,
            legacyIdentifiers = setOf("Prague"),
        ),
        ResumeTemplateDefinition(
            id = TOKYO_COMPACT_ID,
            displayName = "Tokio",
            description = "Formato compacto de una columna con secciones resaltadas en azul claro.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.TokyoCompact,
            legacyIdentifiers = setOf("Tokyo"),
        ),
        ResumeTemplateDefinition(
            id = CAMBRIDGE_CLASSIC_ID,
            displayName = "Cambridge",
            description = "Clásica de una columna, tipografía serif y divisores discretos.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.CambridgeClassic,
            isNew = true,
        ),
        ResumeTemplateDefinition(
            id = BERLIN_STRUCTURED_ID,
            displayName = "Berlín",
            description = "Cabecera dividida, títulos en el margen izquierdo y contenido a la derecha.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.BerlinStructured,
            isNew = true,
        ),
        ResumeTemplateDefinition(
            id = VIENNA_EDITORIAL_ID,
            displayName = "Viena",
            description = "Dos columnas equilibradas con una línea central y cabecera editorial.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.ViennaEditorial,
            isNew = true,
        ),
        ResumeTemplateDefinition(
            id = OSLO_COMPACT_ID,
            displayName = "Oslo",
            description = "Cabecera amplia y columna lateral estrecha para formación y habilidades.",
            hasSidebar = true,
            renderer = ResumeTemplateRenderer.OsloCompact,
            isNew = true,
        ),
        ResumeTemplateDefinition(
            id = LIMA_PROFESSIONAL_ID,
            displayName = "Lima",
            description = "Una columna con títulos centrados, líneas y guía vertical en la experiencia.",
            hasSidebar = false,
            renderer = ResumeTemplateRenderer.LimaProfessional,
            isNew = true,
        ),
    )

    val default: ResumeTemplateDefinition = templates.first()

    fun find(idOrLegacyName: String): ResumeTemplateDefinition = templates.firstOrNull { template ->
        template.id == idOrLegacyName ||
            template.displayName.equals(idOrLegacyName, ignoreCase = true) ||
            template.legacyIdentifiers.any { it.equals(idOrLegacyName, ignoreCase = true) }
    } ?: default
}
