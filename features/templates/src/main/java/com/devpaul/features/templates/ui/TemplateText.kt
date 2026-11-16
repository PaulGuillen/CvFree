package com.devpaul.features.templates.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.devpaul.features.templates.R
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateDefinition

@Composable
fun ResumeTemplateDefinition.localizedName(): String = when (id) {
    ResumeTemplateCatalog.PRAGUE_EDITORIAL_ID -> stringResource(R.string.ui_prague_4a9ba9)
    ResumeTemplateCatalog.TOKYO_COMPACT_ID -> stringResource(R.string.ui_tokyo_08bc81)
    ResumeTemplateCatalog.CAMBRIDGE_CLASSIC_ID -> stringResource(R.string.template_cambridge_name)
    ResumeTemplateCatalog.BERLIN_STRUCTURED_ID -> stringResource(R.string.template_berlin_name)
    ResumeTemplateCatalog.VIENNA_EDITORIAL_ID -> stringResource(R.string.template_vienna_name)
    ResumeTemplateCatalog.OSLO_COMPACT_ID -> stringResource(R.string.template_oslo_name)
    ResumeTemplateCatalog.LIMA_PROFESSIONAL_ID -> stringResource(R.string.template_lima_name)
    else -> displayName
}

@Composable
fun ResumeTemplateDefinition.localizedDescription(): String = stringResource(when (id) {
    ResumeTemplateCatalog.BUDAPEST_ID -> R.string.ui_professional_two_column_design_with_uppercase_headings_42bfe8
    ResumeTemplateCatalog.RIGA_ID -> R.string.ui_black_and_white_editorial_style_with_columns_and_fine_divide_a05aa9
    ResumeTemplateCatalog.PRAGUE_EDITORIAL_ID -> R.string.ui_clean_design_with_a_split_header_and_structured_sections_2fd903
    ResumeTemplateCatalog.CAMBRIDGE_CLASSIC_ID -> R.string.template_cambridge_description
    ResumeTemplateCatalog.BERLIN_STRUCTURED_ID -> R.string.template_berlin_description
    ResumeTemplateCatalog.VIENNA_EDITORIAL_ID -> R.string.template_vienna_description
    ResumeTemplateCatalog.OSLO_COMPACT_ID -> R.string.template_oslo_description
    ResumeTemplateCatalog.LIMA_PROFESSIONAL_ID -> R.string.template_lima_description
    else -> R.string.ui_compact_single_column_layout_with_light_blue_highlighted_sec_f13561
})
