package com.devpaul.features.templates.pdf

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfPageSlicesTest {

    @Test
    fun `short resume stays on one page`() {
        assertEquals(listOf(PdfPageSlice(0, 60)), calculatePageSlices(60, 100) { 0 })
    }

    @Test
    fun `exact page multiple does not add an empty page`() {
        assertEquals(
            listOf(PdfPageSlice(0, 100), PdfPageSlice(100, 200)),
            calculatePageSlices(200, 100) { 0 },
        )
    }

    @Test
    fun `prefers a gap over a row crossing text on a solid sidebar`() {
        val slices = calculatePageSlices(180, 100) { row -> if (row in 88..92) 1 else 30 }
        assertEquals(PdfPageSlice(0, 92), slices.first())
        assertEquals(PdfPageSlice(92, 180), slices.last())
    }

    @Test
    fun `uses a quiet row before the hard page boundary`() {
        val slices = calculatePageSlices(
            contentHeight = 180,
            maximumPageHeight = 100,
            rowInkScore = { row -> if (row == 82) 0 else 10 },
        )

        assertEquals(PdfPageSlice(0, 82), slices.first())
        assertEquals(PdfPageSlice(82, 180), slices.last())
    }

    @Test
    fun `covers all content without gaps`() {
        val slices = calculatePageSlices(
            contentHeight = 350,
            maximumPageHeight = 100,
            rowInkScore = { 1 },
        )

        assertEquals(0, slices.first().top)
        assertEquals(350, slices.last().bottom)
        assertTrue(slices.zipWithNext().all { (current, next) -> current.bottom == next.top })
        assertTrue(slices.all { it.height in 1..100 })
    }
}
