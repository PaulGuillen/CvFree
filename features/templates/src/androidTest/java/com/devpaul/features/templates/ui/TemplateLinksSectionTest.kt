package com.devpaul.features.templates.ui

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.features.templates.R
import com.devpaul.features.templates.ResumeTemplateCatalog
import org.junit.Rule
import org.junit.Test

class TemplateLinksSectionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun linksHaveOneHeadingAndRespectVisibilityAcrossNewTemplates() {
        val sample = previewDocument()
        var resume by mutableStateOf(sample)
        val heading = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.ui_links_d38518)
        compose.setContent { ResumeTemplateDocument(resume, Modifier.width(490.dp)) }

        listOf(
            ResumeTemplateCatalog.CAMBRIDGE_CLASSIC_ID,
            ResumeTemplateCatalog.BERLIN_STRUCTURED_ID,
            ResumeTemplateCatalog.VIENNA_EDITORIAL_ID,
            ResumeTemplateCatalog.OSLO_COMPACT_ID,
            ResumeTemplateCatalog.LIMA_PROFESSIONAL_ID,
            ResumeTemplateCatalog.TOKYO_COMPACT_ID,
        ).forEach { id ->
            compose.runOnIdle { resume = sample.copy(templateName = id) }
            compose.onAllNodesWithText(heading, ignoreCase = true).assertCountEquals(1)
            sample.links.forEach { link ->
                compose.onAllNodesWithText(link.url, substring = true).assertCountEquals(1)
            }
            compose.runOnIdle {
                resume = resume.copy(optionalSections = resume.optionalSections - ResumeOptionalSection.Links)
            }
            compose.onAllNodesWithText(heading, ignoreCase = true).assertCountEquals(0)
            sample.links.forEach { link ->
                compose.onAllNodesWithText(link.url, substring = true).assertCountEquals(0)
            }
            compose.runOnIdle { resume = sample.copy(templateName = id, links = emptyList()) }
            compose.onAllNodesWithText(heading, ignoreCase = true).assertCountEquals(0)
        }
    }
}
