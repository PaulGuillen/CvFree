package com.devpaul.features.templates

import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.features.templates.pdf.ResumePdfExporter
import org.junit.Assert.assertEquals
import org.junit.Test

class ResumeTemplateCatalogTest {

    @Test
    fun `resolves Budapest by current and legacy identifiers`() {
        assertEquals(ResumeTemplateCatalog.BUDAPEST_ID, ResumeDocument().templateName)
        assertEquals(
            ResumeTemplateCatalog.BUDAPEST_ID,
            ResumeTemplateCatalog.find(ResumeTemplateCatalog.BUDAPEST_ID).id,
        )
        assertEquals(
            ResumeTemplateCatalog.BUDAPEST_ID,
            ResumeTemplateCatalog.find("classic_sidebar").id,
        )
        assertEquals(
            ResumeTemplateCatalog.BUDAPEST_ID,
            ResumeTemplateCatalog.find("Clásico Sidebar").id,
        )
        assertEquals(ResumeTemplateCatalog.BUDAPEST_ID, ResumeTemplateCatalog.find("Budapest").id)
    }

    @Test
    fun `resolves Riga by current and legacy identifiers`() {
        assertEquals(
            ResumeTemplateCatalog.RIGA_ID,
            ResumeTemplateCatalog.find(ResumeTemplateCatalog.RIGA_ID).id,
        )
        assertEquals(
            ResumeTemplateCatalog.RIGA_ID,
            ResumeTemplateCatalog.find("editorial_line").id,
        )
        assertEquals(
            ResumeTemplateCatalog.RIGA_ID,
            ResumeTemplateCatalog.find("Editorial Line").id,
        )
        assertEquals(
            ResumeTemplateCatalog.RIGA_ID,
            ResumeTemplateCatalog.find("Línea editorial").id,
        )
        assertEquals(ResumeTemplateCatalog.RIGA_ID, ResumeTemplateCatalog.find("Riga").id)
    }

    @Test
    fun `resolves prague template by id and names`() {
        assertEquals(
            ResumeTemplateCatalog.PRAGUE_EDITORIAL_ID,
            ResumeTemplateCatalog.find(ResumeTemplateCatalog.PRAGUE_EDITORIAL_ID).id,
        )
        assertEquals(
            ResumeTemplateCatalog.PRAGUE_EDITORIAL_ID,
            ResumeTemplateCatalog.find("Prague").id,
        )
        assertEquals(
            ResumeTemplateCatalog.PRAGUE_EDITORIAL_ID,
            ResumeTemplateCatalog.find("Praga").id,
        )
    }

    @Test
    fun `resolves tokyo template by id and names`() {
        assertEquals(
            ResumeTemplateCatalog.TOKYO_COMPACT_ID,
            ResumeTemplateCatalog.find(ResumeTemplateCatalog.TOKYO_COMPACT_ID).id,
        )
        assertEquals(
            ResumeTemplateCatalog.TOKYO_COMPACT_ID,
            ResumeTemplateCatalog.find("Tokyo").id,
        )
        assertEquals(
            ResumeTemplateCatalog.TOKYO_COMPACT_ID,
            ResumeTemplateCatalog.find("Tokio").id,
        )
    }

    @Test
    fun `catalog identifiers and names resolve without collisions`() {
        val templates = ResumeTemplateCatalog.templates
        assertEquals(templates.size, templates.map { it.id }.distinct().size)
        assertEquals(templates.size, templates.map { it.renderer }.distinct().size)
        templates.forEach { template ->
            assertEquals(template, ResumeTemplateCatalog.find(template.id))
            assertEquals(template, ResumeTemplateCatalog.find(template.displayName))
        }
        assertEquals(ResumeTemplateRenderer.CambridgeClassic, ResumeTemplateCatalog.find("cambridge_classic").renderer)
        assertEquals(9, templates.size)
        assertEquals(ResumeTemplateRenderer.LimaProfessional, ResumeTemplateCatalog.find("lima_professional").renderer)
        assertEquals(ResumeTemplateRenderer.ViennaEditorial, ResumeTemplateCatalog.find("vienna_editorial").renderer)
        assertEquals(ResumeTemplateRenderer.OsloCompact, ResumeTemplateCatalog.find("oslo_compact").renderer)
        assertEquals(false, templates.any { it.id == "london_executive" || it.id == "zurich_minimal" })
        assertEquals(ResumeTemplateRenderer.BerlinStructured, ResumeTemplateCatalog.find("berlin_structured").renderer)
    }

    @Test
    fun `creates a portable pdf file name`() {
        val resume = ResumeDocument(
            contact = ResumeContact(name = "Paúl", lastName = "Acuña Ramos"),
        )

        assertEquals("CV_Paul_Acuna_Ramos.pdf", ResumePdfExporter.suggestedFileName(resume))
    }
}
