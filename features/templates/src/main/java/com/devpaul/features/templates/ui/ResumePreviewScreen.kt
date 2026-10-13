package com.devpaul.features.templates.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import android.util.Log
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.domain.resume.ResumeEducation
import com.devpaul.core.domain.resume.ResumeExperience
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.pdf.ResumePdfExporter
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun ResumePreviewScreen(
    onBack: () -> Unit,
    onSave: suspend () -> Unit,
    resume: ResumeDocument,
    modifier: Modifier = Modifier,
    onExported: suspend () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val previewGraphicsLayer = rememberGraphicsLayer()
    var isExporting by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var pendingPreview by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val createPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
    ) { destination ->
        if (destination == null) {
            pendingPreview = null
            isExporting = false
            return@rememberLauncherForActivityResult
        }
        scope.launch {
            val exportResult = runCatching {
                val bitmap = checkNotNull(pendingPreview) { "No se pudo capturar la vista previa" }
                ResumePdfExporter.export(context, destination, bitmap, resume)
            }
            pendingPreview = null
            isExporting = false
            if (exportResult.isSuccess) {
                runCatching { onExported() }
                    .onFailure { error -> Log.e("ResumePreview", "No se pudo guardar el CV", error) }
                snackbarHostState.showSnackbar("PDF guardado correctamente")
            } else {
                val error = exportResult.exceptionOrNull()
                Log.e("ResumePreview", "No se pudo generar el PDF", error)
                snackbarHostState.showSnackbar("No se pudo generar el PDF")
            }
        }
    }
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PreviewTopBar(onBack = onBack)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "Tu currículum está listo",
                    modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 20.dp, bottom = 12.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineLarge,
                )
                ScaledResumeDocument(
                    resume = resume,
                    captureLayer = previewGraphicsLayer,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                )
                Spacer(Modifier.height(24.dp))
            }
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = {
                        isExporting = true
                        scope.launch {
                            runCatching {
                                val captured = previewGraphicsLayer.toImageBitmap().asAndroidBitmap()
                                captured.copy(android.graphics.Bitmap.Config.ARGB_8888, false)
                                    ?: error("No se pudo convertir la vista previa")
                            }.onSuccess { bitmap ->
                                pendingPreview = bitmap
                                createPdf.launch(ResumePdfExporter.suggestedFileName(resume))
                            }.onFailure { error ->
                                isExporting = false
                                Log.e("ResumePreview", "No se pudo capturar la vista previa", error)
                                snackbarHostState.showSnackbar("No se pudo capturar la vista previa")
                            }
                        }
                    },
                    enabled = !isExporting && !isSaving,
                    modifier = Modifier
                        .weight(1.25f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Download, contentDescription = null)
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(if (isExporting) "Generando" else "Descargar PDF")
                }
                Button(
                    onClick = {
                        isSaving = true
                        scope.launch {
                            runCatching { onSave() }
                                .onFailure { error ->
                                    Log.e("ResumePreview", "No se pudo guardar el CV", error)
                                    snackbarHostState.showSnackbar("No se pudo guardar el CV")
                                }
                            isSaving = false
                        }
                    },
                    enabled = !isExporting && !isSaving,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(if (isSaving) "Guardando" else "Guardar CV", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 76.dp),
        )
    }
}

@Composable
fun TemplateExamplePreviewScreen(
    templateId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val template = ResumeTemplateCatalog.find(templateId)
    val exampleResume = remember(template.id) {
        previewDocument().copy(templateName = template.id)
    }
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        PreviewTopBar(
            onBack = onBack,
            title = template.displayName,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Vista previa con información de ejemplo",
                modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 20.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            ScaledResumeDocument(
                resume = exampleResume,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScaledResumeDocument(
    resume: ResumeDocument,
    captureLayer: GraphicsLayer? = null,
    modifier: Modifier = Modifier,
) {
    val designWidth = with(LocalDensity.current) { RESUME_DESIGN_WIDTH_DP.dp.roundToPx() }
    Layout(
        modifier = modifier,
        content = {
            val documentModifier = if (captureLayer == null) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        captureLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(captureLayer)
                    }
            }
            ResumeTemplateDocument(
                resume = resume,
                modifier = documentModifier,
            )
        },
    ) { measurables, constraints ->
        val availableWidth = constraints.maxWidth
        val placeable = measurables.single().measure(
            Constraints(
                minWidth = designWidth,
                maxWidth = designWidth,
                minHeight = 0,
                maxHeight = Constraints.Infinity,
            ),
        )
        val scale = availableWidth.toFloat() / designWidth
        val scaledHeight = ceil(placeable.height * scale).toInt()
        layout(availableWidth, scaledHeight) {
            placeable.placeWithLayer(0, 0) {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0f, 0f)
            }
        }
    }
}

@Composable
private fun PreviewTopBar(
    onBack: () -> Unit,
    title: String = "Vista previa",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

private fun previewResume() = ResumeDocument(
    contact = ResumeContact(
        name = "Elena",
        lastName = "Vargas Salazar",
        professionalTitle = "Desarrolladora Android",
        email = "elena.vargas@example.com",
        phone = "+51 900 000 000",
        city = "Arequipa",
    ),
    summary = "Ingeniero experto desarrollador Android Senior en reactivo que sea escalable y mantenible.",
    experiences = listOf(
        ResumeExperience(
            position = "Senior Android Developer",
            company = "Nexo Digital",
            city = "Arequipa",
            startYear = "2022",
            endYear = "Actualidad",
            achievements = "Desarrollo de aplicaciones con Kotlin y Jetpack Compose.\nArquitectura limpia y pruebas automatizadas.",
        ),
    ),
    education = listOf(
        ResumeEducation(
            institution = "Universidad Nacional",
            degree = "Ingeniería de Sistemas",
            city = "Arequipa",
            startYear = "2017",
            endYear = "2022",
        ),
    ),
    skills = listOf("Kotlin", "Jetpack Compose", "Clean Architecture", "Firebase"),
)

@Preview(showBackground = true)
@Composable
private fun ResumePreview() {
    CvFreeTheme { ResumePreviewScreen(onBack = {}, onSave = {}, resume = previewDocument()) }
}
