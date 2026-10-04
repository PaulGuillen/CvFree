package com.devpaul.features.templates.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor
import com.devpaul.features.templates.ResumeTemplateCatalog
import com.devpaul.features.templates.ResumeTemplateDefinition
import com.devpaul.features.templates.ResumeTemplateRenderer

@Composable
fun TemplateSelectionScreen(
    onBack: () -> Unit,
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialTemplate: String = ResumeTemplateCatalog.CLASSIC_SIDEBAR_ID,
) {
    var selectedId by rememberSaveable(initialTemplate) {
        mutableStateOf(ResumeTemplateCatalog.find(initialTemplate).id)
    }
    val background = MaterialTheme.colorScheme.background
    SetStatusBarColor(background, darkIcons = !isSystemInDarkTheme())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TemplateTopBar(onBack = onBack)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Selecciona una plantilla",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = "El diseño define tanto la vista previa como el archivo PDF.",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(18.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(
                    items = ResumeTemplateCatalog.templates,
                    key = ResumeTemplateDefinition::id,
                ) { template ->
                    TemplateOption(
                        template = template,
                        selected = template.id == selectedId,
                        onClick = { selectedId = template.id },
                    )
                }
            }
        }
        Button(
            onClick = { onContinue(selectedId) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .height(54.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Continuar al preview", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun TemplateOption(
    template: ResumeTemplateDefinition,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.width(220.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            TemplateThumbnail(
                template = template,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = template.displayName,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = template.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            if (selected) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Seleccionada",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
            }
        }
    }
}

@Composable
private fun TemplateThumbnail(
    template: ResumeTemplateDefinition,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFD8DEE8)),
    ) {
        when (template.renderer) {
            ResumeTemplateRenderer.ClassicSidebar -> ClassicSidebarThumbnail()
            ResumeTemplateRenderer.EditorialLine -> EditorialLineThumbnail()
        }
    }
}

@Composable
private fun ClassicSidebarThumbnail() {
    Row {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.28f)
                .background(Color(0xFF414143)),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.72f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.fillMaxWidth(0.7f).height(12.dp).background(Color(0xFF414143)))
            Box(Modifier.fillMaxWidth(0.45f).height(5.dp).background(Color(0xFF9CA3AF)))
            Spacer(modifier = Modifier.height(12.dp))
            repeat(4) {
                Box(Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFD1D5DB)))
            }
        }
    }
}

@Composable
private fun EditorialLineThumbnail() {
    Column(modifier = Modifier.padding(14.dp)) {
        Box(Modifier.fillMaxWidth(0.62f).height(12.dp).background(Color(0xFF242424)))
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth(0.38f).height(4.dp).background(Color(0xFF9CA3AF)))
        Spacer(Modifier.height(18.dp))
        Row(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                repeat(5) { index ->
                    Box(
                        Modifier
                            .fillMaxWidth(if (index % 2 == 0) 0.86f else 0.64f)
                            .height(if (index == 0) 5.dp else 3.dp)
                            .background(if (index == 0) Color(0xFF242424) else Color(0xFFC4C7CC)),
                    )
                }
            }
            Box(Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF242424)))
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
                    .padding(start = 9.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF242424)))
                repeat(7) { index ->
                    Box(
                        Modifier
                            .fillMaxWidth(if (index % 3 == 0) 0.7f else 1f)
                            .height(if (index % 3 == 0) 4.dp else 3.dp)
                            .background(if (index % 3 == 0) Color(0xFF242424) else Color(0xFFC4C7CC)),
                    )
                }
            }
        }
    }
}

@Composable
private fun TemplateTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = "Plantillas",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun TemplateSelectionPreview() {
    CvFreeTheme { TemplateSelectionScreen(onBack = {}, onContinue = {}) }
}
