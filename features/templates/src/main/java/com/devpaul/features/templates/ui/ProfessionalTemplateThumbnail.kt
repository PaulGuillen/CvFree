package com.devpaul.features.templates.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import com.devpaul.features.templates.ResumeTemplateRenderer

@Composable
fun ProfessionalTemplateThumbnail(renderer: ResumeTemplateRenderer, modifier: Modifier = Modifier) {
    val accent = Color(0xFF20242B)
    Surface(modifier = modifier, color = Color.White) {
        Canvas(Modifier.fillMaxSize()) {
            scale(size.width / 100f, size.height / 140f, pivot = Offset.Zero) {
                if (renderer == ResumeTemplateRenderer.BerlinStructured) {
                    drawRect(accent, Offset(8f, 9f), Size(35f, 5f))
                    drawRect(accent, Offset(8f, 17f), Size(42f, 5f))
                    repeat(3) { drawRect(Color(0xFF737780), Offset(68f, 10f + it * 4f), Size(24f, 1.5f)) }
                    drawRect(accent, Offset(8f, 32f), Size(84f, 1.5f))
                    repeat(4) { section ->
                        val top = 40f + section * 23f
                        drawRect(accent, Offset(8f, top), Size(16f, 2f))
                        drawRect(Color(0xFFBBC0C7), Offset(29f, top), Size(0.5f, 16f))
                        repeat(3) { line ->
                            drawRect(Color(0xFFBBC0C7), Offset(34f, top + line * 5f), Size(if (line == 2) 44f else 58f, 1.5f))
                        }
                        drawRect(Color(0xFFBBC0C7), Offset(8f, top + 19f), Size(84f, 0.5f))
                    }
                    return@scale
                }
                if (renderer == ResumeTemplateRenderer.LimaProfessional) {
                    drawRect(accent, Offset(24f, 11f), Size(52f, 4f))
                    drawRect(Color(0xFF737780), Offset(16f, 21f), Size(68f, 1.6f))
                    repeat(4) { section ->
                        val top = 32f + section * 25f
                        drawRect(accent, Offset(12f, top), Size(76f, 0.5f))
                        drawRect(accent, Offset(33f, top + 4f), Size(34f, 2.5f))
                        if (section == 1) drawRect(accent, Offset(14f, top + 11f), Size(0.5f, 12f))
                        repeat(3) { line ->
                            drawRect(Color(0xFFBBC0C7), Offset(18f, top + 11f + line * 4f),
                                Size(if (line == 2) 52f else 66f, 1.3f))
                        }
                    }
                    return@scale
                }
                val centered = renderer in setOf(ResumeTemplateRenderer.CambridgeClassic, ResumeTemplateRenderer.OsloCompact)
                val oslo = renderer == ResumeTemplateRenderer.OsloCompact
                val vienna = renderer == ResumeTemplateRenderer.ViennaEditorial
                drawRect(accent, Offset(if (centered) 20f else 9f, 10f), Size(if (oslo) 66f else 52f, if (oslo) 7f else 5f))
                drawRect(Color(0xFF737780), Offset(if (centered) 32f else 9f, 21f), Size(36f, 2f))
                if (vienna) {
                    repeat(3) { drawRect(Color(0xFF737780), Offset(75f, 10f + it * 4f), Size(17f, 1f)) }
                }
                drawRect(accent, Offset(9f, 29f), Size(82f, if (oslo) 1f else 0.5f))
                fun section(left: Float, top: Float, width: Float) {
                    drawRect(accent, Offset(left, top), Size(width * 0.55f, 2f))
                    drawRect(accent, Offset(left, top + 5f), Size(width, 0.5f))
                    repeat(3) { line ->
                        drawRect(Color(0xFFBBC0C7), Offset(left, top + 10f + line * 3.5f),
                            Size(width * if (line == 2) 0.75f else 1f, 1.3f))
                    }
                }
                if (oslo || vienna) {
                    val divider = if (oslo) 35f else 53f
                    drawRect(Color(0xFFB7B7B7), Offset(divider, 29f), Size(0.5f, 101f))
                    repeat(3) { section(9f, 37f + it * 30f, divider - 13f) }
                    repeat(3) { section(divider + 5f, 37f + it * 30f, 86f - divider) }
                } else {
                    repeat(4) { section(9f, 37f + it * 24f, 82f) }
                }

            }
        }
    }
}
