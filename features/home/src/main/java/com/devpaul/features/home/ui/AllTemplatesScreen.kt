package com.devpaul.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.platform.theme.CvFreeTheme
import com.devpaul.core.platform.theme.SetStatusBarColor
import com.devpaul.core.platform.theme.LocalAppDarkTheme
import com.devpaul.features.home.R
import com.devpaul.features.home.ui.util.localizedDescription
import com.devpaul.features.home.ui.util.localizedName
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateDefinition
import com.devpaul.features.templates.ui.TemplateThumbnail

@Composable
fun AllTemplatesScreen(
    onBack: () -> Unit,
    onPreviewTemplate: (String) -> Unit,
    modifier: Modifier = Modifier,
    templates: List<ResumeTemplateDefinition> = ResumeTemplateCatalog.templates,
) {
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !LocalAppDarkTheme.current)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Surface(color = background) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.ui_back_84a715),
                    )
                }
                Text(
                    text = stringResource(R.string.home_all_templates),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(templates, key = ResumeTemplateDefinition::id) { template ->
                AllTemplateCard(
                    template = template,
                    onClick = { onPreviewTemplate(template.id) },
                )
            }
        }
    }
}

@Composable
private fun AllTemplateCard(
    template: ResumeTemplateDefinition,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        TemplateThumbnail(
            template = template,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 3.dp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = template.localizedName(),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = template.localizedDescription(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.home_view))
        }
    }
}

@Preview(name = "All templates", showBackground = true)
@Composable
private fun AllTemplatesPreview() {
    CvFreeTheme { AllTemplatesScreen(onBack = {}, onPreviewTemplate = {}) }
}
