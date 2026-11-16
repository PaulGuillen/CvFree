package com.devpaul.features.templates.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PreviewZoomTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun pinchZoomChangesPreviewButKeepsExportAtOriginalSize() {
        val sample = previewDocument().copy(summary = "", experiences = emptyList(), education = emptyList(),
            skills = emptyList(), courses = emptyList(), links = emptyList(), languages = emptyList(), references = emptyList())
        var dimensions: IntSize? = null
        lateinit var capture: () -> Unit
        compose.setContent {
            val scroll = rememberScrollState()
            val layer = rememberGraphicsLayer()
            val scope = rememberCoroutineScope()
            capture = {
                scope.launch {
                    val bitmap = layer.toImageBitmap()
                    dimensions = IntSize(bitmap.width, bitmap.height)
                }
            }
            Column(Modifier.width(300.dp).height(400.dp).verticalScroll(scroll)) {
                ScaledResumeDocument(sample, captureLayer = layer, scrollState = scroll,
                    modifier = Modifier.testTag("zoom_preview"))
            }
        }
        compose.runOnIdle { capture() }
        compose.waitUntil { dimensions != null }
        val originalSize = dimensions
        val preview = compose.onNodeWithTag("zoom_preview")
        preview.performTouchInput {
            pinch(center - Offset(width * 0.12f, 0f), center + Offset(width * 0.12f, 0f),
                center - Offset(width * 0.35f, 0f), center + Offset(width * 0.35f, 0f))
        }
        val zoom = preview.fetchSemanticsNode().config[SemanticsProperties.StateDescription].removeSuffix("%").toInt()
        assertTrue(zoom > 100)
        assertTrue(zoom <= 400)
        compose.runOnIdle { dimensions = null; capture() }
        compose.waitUntil { dimensions != null }
        assertEquals(originalSize, dimensions)
    }
}
