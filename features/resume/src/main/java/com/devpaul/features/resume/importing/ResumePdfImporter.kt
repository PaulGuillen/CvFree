package com.devpaul.features.resume.importing

import android.content.Context
import android.net.Uri
import com.devpaul.core.domain.resume.ResumeDocumentCodec
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.features.templates.pdf.ResumePdfMetadata
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

internal enum class ResumeImportStage(
    val title: String,
    val detail: String,
) {
    Preparing(
        title = "Preparando tu currículum...",
        detail = "Abriendo el documento de forma segura.",
    ),
    Reading(
        title = "Analizando tu currículum...",
        detail = "Leyendo el contenido del PDF.",
    ),
    Extracting(
        title = "Identificando tu información...",
        detail = "Extrayendo experiencia, educación y habilidades.",
    ),
    Enhancing(
        title = "Revisando con inteligencia artificial...",
        detail = "Validando secciones y corrigiendo la lectura del documento.",
    ),
    Building(
        title = "Construyendo tu currículum...",
        detail = "Organizando los datos en tus formularios.",
    ),
    Complete(
        title = "Tu currículum está listo",
        detail = "Revisa y mejora cada sección antes de guardarlo.",
    ),
}

internal object ResumePdfImporter {

    suspend fun import(
        context: Context,
        uri: Uri,
        onProgress: suspend (progress: Int, stage: ResumeImportStage) -> Unit,
    ): ResumeDocument {
        onProgress(6, ResumeImportStage.Preparing)
        delay(250.milliseconds)

        onProgress(18, ResumeImportStage.Reading)
        val extracted = extractPdf(context, uri)
        extracted.embeddedDocument?.let { document ->
            onProgress(78, ResumeImportStage.Building)
            delay(300.milliseconds)
            onProgress(100, ResumeImportStage.Complete)
            delay(500.milliseconds)
            return document.copy(
                skills = ResumeTextParser.normalizeImportedSkills(document.skills),
            )
        }
        val content = extracted.textContent
        if (content == null || content.fullText.isBlank() && content.flowText.isBlank()) {
            throw ResumeImportException(
                "No pudimos reconocer texto legible. Prueba con un PDF más nítido o con mayor resolución.",
            )
        }

        onProgress(48, ResumeImportStage.Extracting)
        delay(350.milliseconds)
        val document = withContext(Dispatchers.Default) {
            ResumeTextParser.parse(content)
        }

        onProgress(66, ResumeImportStage.Enhancing)
        val enhancedDocument = try {
            ResumeAiExtractor.enhance(content, document)
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            null
        }

        onProgress(84, ResumeImportStage.Building)
        delay(450.milliseconds)
        onProgress(100, ResumeImportStage.Complete)
        delay(700.milliseconds)
        return enhancedDocument ?: document
    }

    private data class ExtractedPdf(
        val embeddedDocument: ResumeDocument? = null,
        val textContent: ResumePdfTextContent? = null,
    )

    private suspend fun extractPdf(context: Context, uri: Uri): ExtractedPdf = withContext(Dispatchers.IO) {
        PDFBoxResourceLoader.init(context.applicationContext)
        val input = context.contentResolver.openInputStream(uri)
            ?: throw ResumeImportException("No se pudo abrir el archivo seleccionado.")
        try {
            input.use { stream ->
                PDDocument.load(stream).use { document ->
                    if (document.isEncrypted) {
                        throw ResumeImportException("El PDF está protegido con contraseña y no puede analizarse.")
                    }
                    if (document.numberOfPages > 20) {
                        throw ResumeImportException("El documento supera el límite de 20 páginas.")
                    }
                    val information = document.documentInformation
                    val embeddedDocument = information
                        .getCustomMetadataValue(ResumePdfMetadata.RESUME)
                        .let(ResumeDocumentCodec::decodeOrNull)
                    if (embeddedDocument != null) {
                        ExtractedPdf(embeddedDocument = embeddedDocument)
                    } else {
                        val declaredTemplate = information
                            .getCustomMetadataValue(ResumePdfMetadata.TEMPLATE)
                        val extractedText = ResumePdfLayoutDetector.extract(document, declaredTemplate)
                        ExtractedPdf(
                            textContent = if (extractedText.fullText.isNotBlank()) {
                                extractedText
                            } else {
                                try {
                                    ResumePdfOcr.extract(document, extractedText.templateId)
                                } catch (error: CancellationException) {
                                    throw error
                                } catch (_: Exception) {
                                    throw ResumeImportException(
                                        "No se pudo reconocer el texto de las páginas escaneadas.",
                                    )
                                }
                            },
                        )
                    }
                }
            }
        } catch (error: CancellationException) {
            throw error
        } catch (error: ResumeImportException) {
            throw error
        } catch (_: Exception) {
            throw ResumeImportException("El archivo no es un PDF válido o está dañado.")
        }
    }
}

internal class ResumeImportException(message: String) : IllegalArgumentException(message)
