package com.devpaul.features.templates.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorialColumnsTest {
    @Test
    fun `column layouts preserve every section without duplication`() {
        val resume = previewDocument()
        listOf(false, true).forEach { oslo ->
            val (left, right) = editorialColumns(resume, oslo)
            assertEquals(resume.experiences, left.experiences + right.experiences)
            assertEquals(resume.education, left.education + right.education)
            assertEquals(resume.skills, left.skills + right.skills)
            assertEquals(resume.links, left.links + right.links)
            assertEquals(resume.languages, left.languages + right.languages)
            assertEquals(resume.courses, left.courses + right.courses)
            assertEquals(resume.references, left.references + right.references)
            assertEquals(resume.summary, left.summary + right.summary)
            listOf(left, right).forEach { column ->
                assertEquals(resume.optionalSections, column.optionalSections)
                assertEquals(resume.summaryFormatting, column.summaryFormatting)
            }
        }
    }

    @Test
    fun `Oslo places qualifications on the left while Vienna places career there`() {
        val resume = previewDocument()
        val (osloLeft, osloRight) = editorialColumns(resume, oslo = true)
        val (viennaLeft, viennaRight) = editorialColumns(resume, oslo = false)
        assertEquals(resume.education, osloLeft.education)
        assertTrue(osloLeft.experiences.isEmpty())
        assertEquals(resume.experiences, osloRight.experiences)
        assertTrue(osloLeft.skills.isEmpty())
        assertEquals(resume.skills, osloRight.skills)
        assertEquals(resume.languages, osloLeft.languages)
        assertTrue(osloRight.languages.isEmpty())
        assertEquals(resume.summary, viennaLeft.summary)
        assertTrue(viennaRight.summary.isEmpty())
        assertTrue(viennaLeft.courses.isEmpty())
        assertEquals(resume.courses, viennaRight.courses)
        assertEquals(osloRight.copy(skills = emptyList(), courses = emptyList()), viennaLeft)
        assertEquals(osloLeft.copy(skills = resume.skills, courses = resume.courses), viennaRight)
    }
}
