package com.devpaul.features.templates.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.devpaul.features.templates.ResumeTemplateCatalog
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OsloLayoutTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun tallLanguagesColumnIsNotClippedAndSkillsStayAboveCoursesOnRight() {
        val sample = previewDocument()
        val languages = (0..15).map { sample.languages.first().copy(language = "Test language $it") }
        compose.setContent {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                ResumeTemplateDocument(sample.copy(templateName = ResumeTemplateCatalog.OSLO_COMPACT_ID, languages = languages))
            }
        }
        compose.onNodeWithText("Test language 15", substring = true).performScrollTo().assertIsDisplayed()
        val languageBounds = compose.onNodeWithText("Test language 0", substring = true).fetchSemanticsNode().boundsInRoot
        val skillBounds = compose.onNodeWithText(sample.skills.first()).fetchSemanticsNode().boundsInRoot
        val courseBounds = compose.onNodeWithText(sample.courses.first().name, substring = true).fetchSemanticsNode().boundsInRoot
        assertTrue(skillBounds.left > languageBounds.left)
        assertTrue(skillBounds.bottom < courseBounds.top)
    }
}
