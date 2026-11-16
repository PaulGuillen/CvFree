package com.devpaul.features.templates.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import com.devpaul.core.domain.resume.ResumeDocumentCodec
import com.devpaul.core.domain.resume.ResumeDocument
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer
import kotlin.math.abs

object ResumePdfExporter {
    private const val PAGE_MARGIN = 24f

    suspend fun export(
        context: Context,
        destination: Uri,
        preview: Bitmap,
        resume: ResumeDocument,
        format: ResumePdfFormat = ResumePdfFormat.A4,
    ) = withContext(Dispatchers.IO) {
        require(preview.width > 0 && preview.height > 0) { "La vista previa está vacía" }
        val isHardwareBitmap = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            preview.config == Bitmap.Config.HARDWARE
        require(!isHardwareBitmap) {
            "La vista previa debe usar un bitmap compatible con Canvas"
        }

        PDFBoxResourceLoader.init(context.applicationContext)
        val pdf = PDDocument()
        try {
            pdf.documentInformation.apply {
                creator = "CVFree"
                producer = "CVFree"
                title = resume.displayName
                setCustomMetadataValue(ResumePdfMetadata.SCHEMA, ResumeDocumentCodec.SCHEMA_VERSION)
                setCustomMetadataValue(ResumePdfMetadata.TEMPLATE, resume.templateName)
                setCustomMetadataValue(ResumePdfMetadata.RESUME, ResumeDocumentCodec.encode(resume))
            }

            if (format == ResumePdfFormat.Continuous) {
                val width = 595f
                val height = (preview.height * (width / preview.width)).coerceAtLeast(1f)
                val page = PDPage(PDRectangle(width, height))
                pdf.addPage(page)
                val image = LosslessFactory.createFromImage(pdf, preview)
                PDPageContentStream(pdf, page).use { content ->
                    content.drawImage(image, 0f, 0f, width, height)
                }
            } else {
                val pageSize = PDRectangle.A4
                val contentWidth = pageSize.width - PAGE_MARGIN * 2
                val contentHeight = pageSize.height - PAGE_MARGIN * 2
                val scale = contentWidth / preview.width
                val row = IntArray(preview.width)
                val slices = calculatePageSlices(
                    contentHeight = preview.height,
                    maximumPageHeight = (contentHeight / scale).toInt().coerceAtLeast(1),
                    rowInkScore = { y ->
                        preview.getPixels(row, 0, preview.width, 0, y, preview.width, 1)
                        // Count changes rather than dark pixels so solid sidebar backgrounds
                        // do not prevent detecting the spaces between text lines.
                        var transitions = 0
                        for (x in 1 until row.size) {
                            val previous = row[x - 1]
                            val current = row[x]
                            if (abs(Color.red(current) - Color.red(previous)) +
                                abs(Color.green(current) - Color.green(previous)) +
                                abs(Color.blue(current) - Color.blue(previous)) > 60) transitions++
                        }
                        transitions
                    },
                )
                slices.forEach { slice ->
                    val page = PDPage(pageSize)
                    pdf.addPage(page)
                    val bitmap = Bitmap.createBitmap(preview, 0, slice.top, preview.width, slice.height)
                    try {
                        val image = LosslessFactory.createFromImage(pdf, bitmap)
                        val imageHeight = slice.height * scale
                        PDPageContentStream(pdf, page).use { content ->
                            content.drawImage(image, PAGE_MARGIN, pageSize.height - PAGE_MARGIN - imageHeight,
                                contentWidth, imageHeight)
                        }
                    } finally {
                        if (bitmap !== preview) bitmap.recycle()
                    }
                }
            }

            context.contentResolver.openOutputStream(destination)?.use(pdf::save)
                ?: error("No se pudo abrir el archivo seleccionado")
        } finally {
            pdf.close()
        }
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
