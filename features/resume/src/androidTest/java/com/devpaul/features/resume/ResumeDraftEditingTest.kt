package com.devpaul.features.resume

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.devpaul.core.domain.resume.ResumeContact
import com.devpaul.core.domain.resume.ResumeEducation
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.features.resume.ui.contact.ContactScreen
import com.devpaul.features.resume.ui.education.EducationScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResumeDraftEditingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun typingAnIncompleteContactUpdatesTheDraftWithoutContinuing() {
        var pendingContact = ResumeContact()
        var continued = false
        compose.setContent {
            CvFreeTheme {
                ContactScreen(
                    onBack = {},
                    onContinue = { continued = true },
                    initialData = ResumeContact(),
                    onDraftChange = { pendingContact = it },
                )
            }
        }

        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Partial name")

        compose.runOnIdle {
            assertEquals("Partial name", pendingContact.name)
            assertTrue(pendingContact.professionalTitle.isEmpty())
            assertFalse(continued)
        }
    }

    @Test
    fun anIncompleteEntryHasAStableIdentifierAndCancelDiscardsIt() {
        var pendingEducation: ResumeEducation? = null
        var confirmed = false
        compose.setContent {
            CvFreeTheme {
                EducationScreen(
                    onBack = {},
                    onContinue = {},
                    onEducationChange = { confirmed = true },
                    onDraftChange = { pendingEducation = it },
                )
            }
        }

        compose.onAllNodes(hasSetTextAction())[0].performTextInput("Draft university")
        var entryId = ""
        compose.runOnIdle {
            val pending = checkNotNull(pendingEducation)
            assertEquals("Draft university", pending.institution)
            assertTrue(pending.degree.isEmpty())
            entryId = pending.id
            assertFalse(entryId.isBlank())
        }
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("Partial degree")
        compose.runOnIdle {
            assertEquals(entryId, pendingEducation?.id)
            assertFalse(confirmed)
        }

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        compose.onNodeWithText(context.getString(R.string.ui_cancel_c111e0))
            .performScrollTo()
            .performClick()

        compose.runOnIdle {
            assertNull(pendingEducation)
            assertFalse(confirmed)
        }
    }
}
