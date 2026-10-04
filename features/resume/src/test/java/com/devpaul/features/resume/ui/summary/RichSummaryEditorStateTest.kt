package com.devpaul.features.resume.ui.summary

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.devpaul.core.domain.resume.ResumeTextFormatting
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RichTextEditorStateTest {

    @Test
    fun selectedStyle_isStoredAndSurvivesTextInsertion() {
        val state = RichTextEditorState("Hola mundo", ResumeTextFormatting())

        state.onValueChange(TextFieldValue("Hola mundo", TextRange(0, 4)))
        state.toggle(InlineStyle.Bold)
        state.onValueChange(TextFieldValue("Hola mundo!", TextRange(11)))

        assertEquals(1, state.formatting.spans.size)
        assertEquals(0, state.formatting.spans.single().start)
        assertEquals(4, state.formatting.spans.single().end)
        assertTrue(state.formatting.spans.single().bold)
    }

    @Test
    fun collapsedStyle_isAppliedToNewText() {
        val state = RichTextEditorState("Perfil", ResumeTextFormatting())

        state.onValueChange(TextFieldValue("Perfil", TextRange(6)))
        state.toggle(InlineStyle.Italic)
        state.onValueChange(TextFieldValue("Perfil senior", TextRange(13)))

        val span = state.formatting.spans.single()
        assertEquals(6, span.start)
        assertEquals(13, span.end)
        assertTrue(span.italic)
    }

    @Test
    fun numberedList_canBeAppliedAndRemoved() {
        val state = RichTextEditorState("Primero\nSegundo", ResumeTextFormatting())
        state.onValueChange(TextFieldValue(state.value.text, TextRange(0, state.value.text.length)))

        state.toggleList(numbered = true)
        assertEquals("1. Primero\n2. Segundo", state.value.text)

        state.toggleList(numbered = true)
        assertEquals("Primero\nSegundo", state.value.text)
    }
}
