package com.devpaul.features.resume.ui

import com.devpaul.core.platform.analytics.trackedClick
import com.devpaul.core.platform.analytics.AppAnalytics
import com.devpaul.core.platform.analytics.TrackAnalyticsView
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.component.CvBottomNavigation
import com.devpaul.core.designsystem.component.CvMainDestination
import com.devpaul.core.domain.resume.ResumeDocument
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.core.platform.theme.LocalAppDarkTheme
import com.devpaul.features.resume.R
import com.devpaul.features.resume.importing.ResumeImportPolicy
import com.devpaul.features.resume.ui.model.CvFilter
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateRenderer
import com.devpaul.features.templates.ui.localizedName
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCvsScreen(
    modifier: Modifier = Modifier,
    importPolicy: ResumeImportPolicy = ResumeImportPolicy(),
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCreateCv: () -> Unit,
    onImportPdf: (Uri) -> Unit = {},
    resumes: List<ResumeDocument> = emptyList(),
    onEditResume: (ResumeDocument) -> Unit = {},
    onViewResume: (ResumeDocument) -> Unit = {},
    onDeleteResume: (ResumeDocument) -> Unit = {},
) {
    var selectedFilterName by rememberSaveable { mutableStateOf(CvFilter.All.name) }
    var showCreateOptions by rememberSaveable { mutableStateOf(false) }
    var pendingImportAction by remember { mutableStateOf<ImportAction?>(null) }
    var pendingDelete by remember { mutableStateOf<ResumeDocument?>(null) }
    val selectedFilter = CvFilter.valueOf(selectedFilterName)
    val context = LocalContext.current
    val activity = context.findActivity()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scannedPdfError = stringResource(R.string.ui_the_scanned_pdf_couldn_t_be_created_13dfd7)
    val scannerUnavailableError = stringResource(R.string.ui_the_scanner_isn_t_available_on_this_device_fcd110)
    val scannerOpenError = stringResource(R.string.ui_the_scanner_couldn_t_be_opened_try_again_0cdc7e)
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }.onFailure { AppAnalytics.error("persist_import_permission", it) }
            onImportPdf(uri)
        }
    }
    val scanner = remember {
        GmsDocumentScanning.getClient(
            GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(true)
                .setPageLimit(12)
                .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .build(),
        )
    }
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scannedPdf = GmsDocumentScanningResult
                .fromActivityResultIntent(result.data)
                ?.pdf
                ?.uri
            if (scannedPdf != null) {
                onImportPdf(scannedPdf)
            } else {
                AppAnalytics.error("scan_resume", code = "missing_pdf")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(scannedPdfError)
                }
            }
        }
    }

    fun launchScanner() {
        val hostActivity = activity
        if (hostActivity == null) {
            AppAnalytics.error("scan_resume", code = "missing_activity")
            coroutineScope.launch {
                snackbarHostState.showSnackbar(scannerUnavailableError)
            }
            return
        }
        scanner.getStartScanIntent(hostActivity)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener { error ->
                AppAnalytics.error("open_document_scanner", error)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(scannerOpenError)
                }
            }
    }

    fun launchImport(action: ImportAction) {
        showCreateOptions = false
        when (action) {
            ImportAction.Pdf -> pdfPicker.launch(arrayOf("application/pdf"))
            ImportAction.Scan -> launchScanner()
        }
    }

    fun requestImport(action: ImportAction) {
        if (importPolicy.aiEnhancementEnabled) {
            pendingImportAction = action
        } else {
            launchImport(action)
        }
    }
    val visibleResumes = resumes
        .let { filtered ->
            when (selectedFilter) {
                CvFilter.All -> filtered
                CvFilter.Recent -> filtered.sortedByDescending(ResumeDocument::updatedAt)
                CvFilter.Template -> filtered.sortedBy(ResumeDocument::templateName)
            }
        }
    val background = MaterialTheme.colorScheme.background

    SetStatusBarColor(background, darkIcons = !LocalAppDarkTheme.current)

    Scaffold(
        modifier = modifier,
        containerColor = background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CvBottomNavigation(
                selectedDestination = CvMainDestination.MyCvs,
                onDestinationSelected = { destination ->
                    when (destination) {
                         CvMainDestination.Home -> onNavigateToHome()
                        CvMainDestination.MyCvs -> Unit
                        CvMainDestination.Settings -> onNavigateToSettings()
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = trackedClick("open_create_options") { showCreateOptions = true },
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.ui_create_or_import_resume_cf411e),
                    modifier = Modifier.size(34.dp),
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 24.dp,
                end = 20.dp,
                bottom = 96.dp,
            ),
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.ui_my_resumes_29f302),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        modifier = Modifier.size(38.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = resumes.size.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    CvFilter.entries.forEach { filter ->
                        FilterChip(
                            filter = filter,
                            selected = filter == selectedFilter,
                            onClick = trackedClick("filter_resumes") {
                                AppAnalytics.selection("resume_filter", filter.name.lowercase())
                                selectedFilterName = filter.name
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            if (visibleResumes.isEmpty()) {
                item {
                    EmptyCvsState(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                    )
                }
            } else {
                items(
                    items = visibleResumes,
                    key = ResumeDocument::id,
                ) { resume ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ResumeCard(
                        resume = resume,
                        onEdit = { onEditResume(resume) },
                        onView = { onViewResume(resume) },
                        onDelete = { pendingDelete = resume },
                    )
                }
            }
        }
    }

    if (showCreateOptions) {
        TrackAnalyticsView("create_resume_sheet")
        ModalBottomSheet(onDismissRequest = { showCreateOptions = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            ) {
                Text(
                    text = stringResource(R.string.ui_create_resume_1ba285),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.ui_choose_how_you_want_to_begin_c950b6),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(16.dp))
                CreateResumeOption(
                    icon = Icons.Outlined.EditNote,
                    title = stringResource(R.string.ui_start_from_scratch_fad4a9),
                    description = stringResource(R.string.ui_complete_each_section_step_by_step_af5b50),
                    onClick = trackedClick("create_resume") {
                        showCreateOptions = false
                        onCreateCv()
                    },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.outline,
                )
                CreateResumeOption(
                    icon = Icons.Outlined.DocumentScanner,
                    title = stringResource(R.string.ui_scan_document_c873c8),
                    description = stringResource(R.string.ui_capture_crop_and_enhance_multiple_pages_automatically_5952c0),
                    onClick = trackedClick("scan_resume") { requestImport(ImportAction.Scan) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = MaterialTheme.colorScheme.outline,
                )
                CreateResumeOption(
                    icon = Icons.Outlined.UploadFile,
                    title = stringResource(R.string.ui_import_pdf_23e4c6),
                    description = if (importPolicy.aiEnhancementEnabled) {
                        stringResource(R.string.ui_analyze_and_organize_the_content_with_ai_assistance_7b0063)
                    } else {
                        stringResource(R.string.ui_extract_and_organize_the_content_directly_on_the_device_1ea11b)
                    },
                    onClick = trackedClick("import_pdf") { requestImport(ImportAction.Pdf) },
                )
            }
        }
    }

    pendingImportAction?.let { action ->
        TrackAnalyticsView("ai_import_confirmation")
        AlertDialog(
            onDismissRequest = trackedClick("dismiss_ai_import") { pendingImportAction = null },
            title = { Text(stringResource(R.string.ui_smart_processing_a52050)) },
            text = {
                Text(
                    stringResource(R.string.ui_to_organize_the_resume_the_recognized_text_will_be_sent_secu_4ee2d9) +
                        if (importPolicy.isFreePlan) "\n\n" + stringResource(R.string.ai_weekly_free_notice) else "",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = trackedClick("confirm_ai_import") {
                        pendingImportAction = null
                        launchImport(action)
                    },
                ) {
                    Text(stringResource(R.string.ui_continue_b8cb6f))
                }
            },
            dismissButton = {
                TextButton(onClick = trackedClick("cancel_ai_import") { pendingImportAction = null }) {
                    Text(stringResource(R.string.ui_cancel_c111e0))
                }
            },
        )
    }

    pendingDelete?.let { resume ->
        TrackAnalyticsView("delete_resume_dialog")
        AlertDialog(
            onDismissRequest = trackedClick("dismiss_delete_resume") { pendingDelete = null },
            title = { Text(stringResource(R.string.ui_delete_resume_daf230)) },
            text = {
                Text(
                    stringResource(R.string.resume_delete_confirmation, resume.displayName),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = trackedClick("confirm_delete_resume") {
                        pendingDelete = null
                        onDeleteResume(resume)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(R.string.ui_delete_54e140))
                }
            },
            dismissButton = {
                TextButton(onClick = trackedClick("cancel_delete_resume") { pendingDelete = null }) {
                    Text(stringResource(R.string.ui_cancel_c111e0))
                }
            },
        )
    }
}

private enum class ImportAction {
    Pdf,
    Scan,
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun CreateResumeOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = androidx.compose.ui.graphics.Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(25.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun ResumeCard(
    resume: ResumeDocument,
    onEdit: () -> Unit,
    onView: () -> Unit,
    onDelete: () -> Unit,
) {
    var showActionsMenu by remember { mutableStateOf(false) }
    val template = ResumeTemplateCatalog.find(resume.templateName)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                ResumeThumbnail(renderer = template.renderer)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {
                        Text(
                            text = template.localizedName(),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }
                    Spacer(modifier = Modifier.height(9.dp))
                    Text(
                        text = resume.displayName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(R.string.resume_updated, formatResumeDate(resume.updatedAt)),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                Box {
                    IconButton(onClick = trackedClick("open_resume_menu") { showActionsMenu = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = stringResource(R.string.resume_more_options, resume.displayName),
                        )
                    }
                    DropdownMenu(
                        expanded = showActionsMenu,
                        onDismissRequest = { showActionsMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.ui_delete_resume_daf230), color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            },
                            onClick = trackedClick("delete_resume") {
                                showActionsMenu = false
                                onDelete()
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Button(
                    onClick = trackedClick("edit_resume", onEdit),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.ui_edit_2b5f23))
                }
                FilledTonalButton(
                    onClick = trackedClick("view_resume", onView),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.ui_view_49b372))
                }
            }
        }
    }
}

@Composable
private fun ResumeThumbnail(renderer: ResumeTemplateRenderer) {
    Surface(
        modifier = Modifier.size(width = 84.dp, height = 108.dp),
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        if (renderer == ResumeTemplateRenderer.TokyoCompact) {
            TokyoResumeThumbnail()
        } else if (renderer == ResumeTemplateRenderer.PragueEditorial) {
            PragueResumeThumbnail()
        } else {
            Row(modifier = Modifier.padding(14.dp)) {
                if (renderer == ResumeTemplateRenderer.Budapest) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(androidx.compose.ui.graphics.Color.White)
                        .padding(9.dp),
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (index == 0) 0.55f else 1f)
                                .height(if (index == 0) 8.dp else 4.dp)
                                .background(
                                    if (index == 0) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color(0xFFCBD5E1),
                                    CircleShape,
                                ),
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                    }
                    if (renderer == ResumeTemplateRenderer.Riga) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(Modifier.weight(0.3f).fillMaxHeight())
                            Box(
                                Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .background(androidx.compose.ui.graphics.Color(0xFF414143)),
                            )
                            Box(Modifier.weight(0.7f).fillMaxHeight())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TokyoResumeThumbnail() {
    val ink = androidx.compose.ui.graphics.Color(0xFF30333A)
    val muted = androidx.compose.ui.graphics.Color(0xFFCBD5E1)
    val section = androidx.compose.ui.graphics.Color(0xFFEAF2FF)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(horizontal = 8.dp, vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.fillMaxWidth(0.65f).height(6.dp).background(ink))
        Spacer(Modifier.height(3.dp))
        Box(Modifier.fillMaxWidth(0.38f).height(3.dp).background(muted))
        Spacer(Modifier.height(5.dp))
        repeat(2) { index ->
            Box(Modifier.fillMaxWidth().height(7.dp).background(section))
            Spacer(Modifier.height(4.dp))
            repeat(if (index == 1) 3 else 2) { line ->
                Box(
                    Modifier
                        .fillMaxWidth(if (line == 0) 1f else 0.8f)
                        .height(if (line == 0) 2.5.dp else 2.dp)
                        .background(if (line == 0) ink else muted),
                )
                Spacer(Modifier.height(3.dp))
            }
        }
    }
}

@Composable
private fun PragueResumeThumbnail() {
    val ink = androidx.compose.ui.graphics.Color(0xFF414143)
    val muted = androidx.compose.ui.graphics.Color(0xFFCBD5E1)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(0.43f)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Box(Modifier.fillMaxWidth().height(5.dp).background(ink))
                Box(Modifier.fillMaxWidth().height(1.dp).background(ink))
                Box(Modifier.fillMaxWidth(0.7f).height(2.dp).background(muted))
            }
            Box(Modifier.width(1.dp).fillMaxHeight().background(ink))
            Column(
                modifier = Modifier
                    .weight(0.57f)
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Box(Modifier.fillMaxWidth(0.5f).height(3.dp).background(ink))
                repeat(3) { Box(Modifier.fillMaxWidth().height(2.dp).background(muted)) }
            }
        }
        Spacer(Modifier.height(5.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(ink))
        Spacer(Modifier.height(5.dp))
        Row(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .weight(0.29f)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(5) { index ->
                    Box(
                        Modifier
                            .fillMaxWidth(if (index % 2 == 0) 1f else 0.72f)
                            .height(if (index % 2 == 0) 3.dp else 2.dp)
                            .background(if (index % 2 == 0) ink else muted),
                    )
                }
            }
            Box(Modifier.width(1.dp).fillMaxHeight().background(ink))
            Column(
                modifier = Modifier
                    .weight(0.71f)
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(6) { index ->
                    Box(
                        Modifier
                            .fillMaxWidth(if (index % 3 == 0) 0.72f else 1f)
                            .height(if (index % 3 == 0) 3.dp else 2.dp)
                            .background(if (index % 3 == 0) ink else muted),
                    )
                }
            }
        }
    }
}

private fun formatResumeDate(value: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(value))

@Composable
private fun FilterChip(
    filter: CvFilter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        contentColor = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        border = if (selected) null else {
            androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(filter.label),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun EmptyCvsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Description,
            contentDescription = null,
            modifier = Modifier.size(68.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.ui_you_don_t_have_any_resumes_yet_5c79fc),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.ui_create_your_first_professional_resume_with_the_button_f321a0),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(name = "My CVs Light", showBackground = true, heightDp = 860)
@Composable
private fun MyCvsLightPreview() {
    CvFreeTheme(darkTheme = false) {
        MyCvsScreen(
            onNavigateToHome = {},
            onNavigateToSettings = {},
            onCreateCv = {},
        )
    }
}

@Preview(name = "My CVs Dark", showBackground = true, heightDp = 860)
@Composable
private fun MyCvsDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        MyCvsScreen(
            onNavigateToHome = {},
            onNavigateToSettings = {},
            onCreateCv = {},
        )
    }
}
