package com.devpaul.features.templates.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.features.templates.R
import org.junit.Rule
import org.junit.Test

class PdfDownloadDialogTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun downloadCapturesPreviewBeforeOfferingFormatsAndCanBeCancelledAndReopened() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        fun text(id: Int) = context.getString(id)
        compose.setContent {
            CvFreeTheme {
                ResumePreviewScreen(onBack = {}, onSave = {}, resume = previewDocument())
            }
        }
        compose.onNodeWithText(text(R.string.ui_download_pdf_40863e)).performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodes(hasText(text(R.string.pdf_format_title)))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(text(R.string.pdf_format_a4)).assertIsDisplayed()
        compose.onNodeWithText(text(R.string.pdf_format_continuous)).performClick()
        compose.onNodeWithText(text(R.string.pdf_format_cancel)).performClick()
        compose.onNodeWithText(text(R.string.ui_download_pdf_40863e)).assertIsEnabled().performClick()
        compose.waitUntil(10_000) {
            compose.onAllNodes(hasText(text(R.string.pdf_format_title)))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(text(R.string.pdf_format_a4)).assertIsDisplayed()
    }
}
