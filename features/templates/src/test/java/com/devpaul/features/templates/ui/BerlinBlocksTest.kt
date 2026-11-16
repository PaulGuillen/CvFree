package com.devpaul.features.templates.ui

import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.features.templates.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BerlinBlocksTest {
    @Test
    fun `section rail preserves every enabled section and formatting once`() {
        val sample = previewDocument()
        val blocks = berlinBlocks(sample)
        val documents = blocks.map { it.document }
        assertEquals(blocks.size, blocks.map { it.title }.distinct().size)
        assertEquals(sample.experiences, documents.flatMap { it.experiences })
        assertEquals(sample.education, documents.flatMap { it.education })
        assertEquals(sample.skills, documents.flatMap { it.skills })
        assertEquals(sample.links, documents.flatMap { it.links })
        assertEquals(sample.courses, documents.flatMap { it.courses })
        assertEquals(sample.languages, documents.flatMap { it.languages })
        assertEquals(sample.references, documents.flatMap { it.references })
        assertEquals(sample.summary, documents.joinToString("") { it.summary })
        assertTrue(documents.all { it.summaryFormatting == sample.summaryFormatting })
    }

    @Test
    fun `disabled or empty links do not leave a label in the rail`() {
        val sample = previewDocument()
        assertTrue(berlinBlocks(sample.copy(optionalSections = sample.optionalSections - ResumeOptionalSection.Links))
            .none { it.title == R.string.ui_links_d38518 })
        assertTrue(berlinBlocks(sample.copy(links = emptyList())).none { it.title == R.string.ui_links_d38518 })
    }
}
