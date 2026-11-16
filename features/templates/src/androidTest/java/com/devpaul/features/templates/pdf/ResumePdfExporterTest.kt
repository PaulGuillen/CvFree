package com.devpaul.features.templates.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import com.devpaul.core.domain.resume.ResumeDocument
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ResumePdfExporterTest {
    @Test
    fun a4PdfKeepsStandardPageSizeMarginsAndContentAcrossPages() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = Bitmap.createBitmap(490, 1000, Bitmap.Config.ARGB_8888)
        Canvas(source).apply {
            drawColor(Color.WHITE)
            val ink = Paint().apply { color = Color.BLACK }
            drawRect(40f, 40f, 450f, 100f, ink)
            drawRect(40f, 900f, 450f, 960f, ink)
        }
        val file = File(context.cacheDir, "a4-export-test.pdf")
        try {
            ResumePdfExporter.export(context, Uri.fromFile(file), source, ResumeDocument(), ResumePdfFormat.A4)
            PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)).use { renderer ->
                assertEquals(2, renderer.pageCount)
                repeat(renderer.pageCount) { index ->
                    renderer.openPage(index).use { page ->
                        assertTrue(page.width in 595..596)
                        assertTrue(page.height in 841..842)
                        val rendered = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        try {
                            rendered.eraseColor(Color.WHITE)
                            page.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                            assertEquals(Color.WHITE, rendered.getPixel(10, 40))
                            assertTrue(Color.red(rendered.getPixel(200, if (index == 0) 90 else 260)) < 50)
                        } finally {
                            rendered.recycle()
                        }
                    }
                }
            }
        } finally {
            source.recycle()
            file.delete()
        }
    }

    @Test
    fun continuousPdfRendersTopAndBottomOnOnePage() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = Bitmap.createBitmap(490, 1500, Bitmap.Config.ARGB_8888)
        Canvas(source).apply {
            drawColor(Color.WHITE)
            val ink = Paint().apply { color = Color.BLACK }
            drawRect(40f, 40f, 450f, 100f, ink)
            drawRect(40f, 1400f, 450f, 1460f, ink)
        }
        val file = File(context.cacheDir, "continuous-export-test.pdf")
        try {
            ResumePdfExporter.export(context, Uri.fromFile(file), source, ResumeDocument(), ResumePdfFormat.Continuous)
            PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)).use { renderer ->
                assertEquals(1, renderer.pageCount)
                renderer.openPage(0).use { page ->
                    assertEquals(595, page.width)
                    assertTrue(page.height in 1820..1822)
                    val rendered = Bitmap.createBitmap(490, 1500, Bitmap.Config.ARGB_8888)
                    try {
                        rendered.eraseColor(Color.WHITE)
                        page.render(rendered, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        assertTrue(Color.red(rendered.getPixel(200, 70)) < 50)
                        assertTrue(Color.red(rendered.getPixel(200, 1430)) < 50)
                    } finally {
                        rendered.recycle()
                    }
                }
            }
        } finally {
            source.recycle()
            file.delete()
        }
    }
}
