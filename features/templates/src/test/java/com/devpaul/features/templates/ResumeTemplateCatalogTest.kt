package com.devpaul.features.templates

import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.features.templates.pdf.ResumePdfExporter
import org.junit.Assert.assertEquals
import org.junit.Test

class ResumeTemplateCatalogTest {

    @Test
    fun `resolves stable id and legacy display name`() {
        assertEquals(
            ResumeTemplateCatalog.CLASSIC_SIDEBAR_ID,
            ResumeTemplateCatalog.find(ResumeTemplateCatalog.CLASSIC_SIDEBAR_ID).id,
        )
        assertEquals(
            ResumeTemplateCatalog.CLASSIC_SIDEBAR_ID,
            ResumeTemplateCatalog.find("Clásico Sidebar").id,
        )
    }

    @Test
    fun `creates a portable pdf file name`() {
        val resume = ResumeDocument(
            contact = ResumeContact(name = "Paúl", lastName = "Acuña Ramos"),
        )

        assertEquals("CV_Paul_Acuna_Ramos.pdf", ResumePdfExporter.suggestedFileName(resume))
    }
}
