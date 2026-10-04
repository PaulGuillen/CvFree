package com.devpaul.features.templates.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.devpaul.core.domain.resume.ResumeDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import kotlin.math.abs

object ResumePdfExporter {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842

    suspend fun export(
        context: Context,
        destination: Uri,
        preview: Bitmap,
    ) = withContext(Dispatchers.IO) {
        require(preview.width > 0 && preview.height > 0) { "La vista previa está vacía" }
        require(preview.config != Bitmap.Config.HARDWARE) {
            "La vista previa debe usar un bitmap compatible con Canvas"
        }

        val pdf = PdfDocument()
        try {
            val scale = PAGE_WIDTH.toFloat() / preview.width
            val sourcePageHeight = (PAGE_HEIGHT / scale).toInt().coerceAtLeast(1)
            val slices = calculatePageSlices(
                contentHeight = preview.height,
                maximumPageHeight = sourcePageHeight,
                rowInkScore = { y -> preview.rowInkScore(y) },
            )
            val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            slices.forEachIndexed { pageIndex, slice ->
                val page = pdf.startPage(
                    PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex + 1).create(),
                )
                page.canvas.drawColor(Color.WHITE)
                page.canvas.drawBitmap(
                    preview,
                    Rect(0, slice.top, preview.width, slice.bottom),
                    RectF(0f, 0f, PAGE_WIDTH.toFloat(), slice.height * scale),
                    bitmapPaint,
                )
                pdf.finishPage(page)
            }

            context.contentResolver.openOutputStream(destination)?.use(pdf::writeTo)
                ?: error("No se pudo abrir el archivo seleccionado")
        } finally {
            pdf.close()
        }
    }

    private fun Bitmap.rowInkScore(y: Int): Int {
        val sampleRadius = 2
        val firstRow = (y - sampleRadius).coerceAtLeast(0)
        val lastRow = (y + sampleRadius).coerceAtMost(height - 1)
        val pixels = IntArray(width)
        var score = 0
        for (row in firstRow..lastRow) {
            getPixels(pixels, 0, width, 0, row, width, 1)
            for (x in 2 until width - 2 step 4) {
                val color = pixels[x]
                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)
                val background = if (x < width * 0.28f) {
                    abs(red - 65) <= 18 && abs(green - 65) <= 18 && abs(blue - 67) <= 18
                } else {
                    red >= 245 && green >= 245 && blue >= 245
                }
                if (!background) score++
            }
        }
        return score
    }

    fun suggestedFileName(resume: ResumeDocument): String {
        val rawName = listOf(resume.contact.name, resume.contact.lastName)
            .filter(String::isNotBlank)
            .joinToString("_")
            .ifBlank { "Mi_CV" }
        val asciiName = Normalizer.normalize(rawName, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .replace("[^A-Za-z0-9_-]".toRegex(), "_")
        return "CV_$asciiName.pdf"
    }
}
