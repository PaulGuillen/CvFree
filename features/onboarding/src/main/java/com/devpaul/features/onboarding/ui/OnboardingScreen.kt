package com.devpaul.features.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devpaul.core.designsystem.theme.CvFreeTheme
import com.devpaul.core.designsystem.theme.SetStatusBarColor
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val firstFeatureIcon: ImageVector,
    val firstFeature: String,
    val secondFeatureIcon: ImageVector,
    val secondFeature: String,
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Outlined.Description,
        title = "Elige una plantilla profesional",
        description = "Completa tus datos, elige una plantilla y descarga tu CV en PDF.",
        firstFeatureIcon = Icons.Outlined.AutoAwesome,
        firstFeature = "Diseños personalizables",
        secondFeatureIcon = Icons.Outlined.PictureAsPdf,
        secondFeature = "PDF de alta calidad",
    ),
    OnboardingPage(
        icon = Icons.Outlined.Edit,
        title = "Completa tu perfil fácilmente",
        description = "Organiza tu experiencia, estudios y habilidades paso a paso.",
        firstFeatureIcon = Icons.Outlined.Edit,
        firstFeature = "Edición sencilla",
        secondFeatureIcon = Icons.Outlined.VerifiedUser,
        secondFeature = "Datos protegidos",
    ),
    OnboardingPage(
        icon = Icons.Outlined.Download,
        title = "Tu próximo empleo empieza aquí",
        description = "Mantén tu currículum actualizado y listo para compartir cuando lo necesites.",
        firstFeatureIcon = Icons.Outlined.AutoAwesome,
        firstFeature = "Resultado profesional",
        secondFeatureIcon = Icons.Outlined.Download,
        secondFeature = "Descarga inmediata",
    ),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val background = MaterialTheme.colorScheme.background

    SetStatusBarColor(color = background, darkIcons = background.luminance() > 0.5f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        TextButton(
            onClick = onFinished,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text = "Saltar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            OnboardingPageContent(page = onboardingPages[page])
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(onboardingPages.size) { index ->
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (index == pagerState.currentPage) 32.dp else 8.dp)
                        .background(
                            color = if (index == pagerState.currentPage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)
                            },
                            shape = CircleShape,
                        ),
                )
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage == onboardingPages.lastIndex) {
                    onFinished()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = if (pagerState.currentPage == onboardingPages.lastIndex) "Empezar" else "Siguiente",
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OnboardingIllustration(icon = page.icon)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = page.title,
            modifier = Modifier.widthIn(max = 520.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.description,
            modifier = Modifier.widthIn(max = 520.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FeatureTile(
                icon = page.firstFeatureIcon,
                label = page.firstFeature,
                modifier = Modifier.weight(1f),
            )
            FeatureTile(
                icon = page.secondFeatureIcon,
                label = page.secondFeature,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun OnboardingIllustration(icon: ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(244.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .size(width = 214.dp, height = 174.dp)
                .rotate(-8f),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
            ),
        ) {}
        Surface(
            modifier = Modifier
                .size(width = 174.dp, height = 210.dp)
                .shadow(12.dp, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(36.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(modifier = Modifier.height(22.dp))
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth(if (index == 2) 0.68f else 1f)
                            .height(8.dp)
                            .background(
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                                CircleShape,
                            ),
                    )
                }
            }
        }
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 118.dp, bottom = 8.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp,
        ) {
            Text(
                text = "Recomendado",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun FeatureTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.tertiary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun Color.luminance(): Float {
    return (0.2126f * red) + (0.7152f * green) + (0.0722f * blue)
}

@Preview(name = "Onboarding Light", showBackground = true, heightDp = 820)
@Composable
private fun OnboardingLightPreview() {
    CvFreeTheme(darkTheme = false) {
        OnboardingScreen(onFinished = {})
    }
}

@Preview(name = "Onboarding Dark", showBackground = true, heightDp = 820)
@Composable
private fun OnboardingDarkPreview() {
    CvFreeTheme(darkTheme = true) {
        OnboardingScreen(onFinished = {})
    }
}
