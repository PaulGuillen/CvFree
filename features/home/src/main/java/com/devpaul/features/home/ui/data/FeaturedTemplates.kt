package com.devpaul.features.home.ui.data

import androidx.compose.ui.graphics.Color
import com.devpaul.features.home.ui.model.CvTemplateUiModel

internal val featuredTemplates = listOf(
    CvTemplateUiModel(
        name = "Budapest",
        description = "Elegante y moderno",
        accent = Color(0xFF163A5F),
        sidebar = false,
    ),
    CvTemplateUiModel(
        name = "Lima",
        description = "Minimalista y limpio",
        accent = Color(0xFF14B8A6),
        sidebar = true,
    ),
    CvTemplateUiModel(
        name = "Madrid",
        description = "Clásico y directo",
        accent = Color(0xFFA93802),
        sidebar = false,
    ),
)
