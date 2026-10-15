package com.devpaul.features.resume

import com.devpaul.features.resume.R

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.devpaul.core.domain.resume.ResumeOptionalSection
import com.devpaul.core.domain.resume.ResumeRepository
import com.devpaul.features.resume.importing.ResumeImportPolicy
import com.devpaul.features.resume.ui.MyCvsScreen
import com.devpaul.features.resume.ui.contact.ContactScreen
import com.devpaul.features.resume.ui.courses.CoursesScreen
import com.devpaul.features.resume.ui.education.EducationScreen
import com.devpaul.features.resume.ui.experience.ExperienceScreen
import com.devpaul.features.resume.ui.importing.ResumeImportScreen
import com.devpaul.features.resume.ui.languages.LanguagesScreen
import com.devpaul.features.resume.ui.links.LinksScreen
import com.devpaul.features.resume.ui.references.ReferencesScreen
import com.devpaul.features.resume.ui.sections.SectionSelectionScreen
import com.devpaul.features.resume.ui.skills.SkillsScreen
import com.devpaul.features.resume.ui.summary.SummaryScreen
import com.devpaul.features.templates.ui.ResumePreviewScreen
import com.devpaul.features.templates.ui.TemplateSelectionScreen
import com.devpaul.navigation.route.ResumeRoute
import kotlinx.coroutines.launch

fun NavGraphBuilder.resumeGraph(
    navController: NavHostController,
    repository: ResumeRepository,
    draftState: ResumeDraftState,
    importPolicy: ResumeImportPolicy = ResumeImportPolicy(),
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    fun selectedSections() = draftState.document.optionalSections

    fun nextOptionalSection(after: ResumeOptionalSection?): ResumeOptionalSection? {
        val orderedSections = ResumeOptionalSection.entries
        val startIndex = after?.let { orderedSections.indexOf(it) + 1 } ?: 0
        return orderedSections.drop(startIndex).firstOrNull(selectedSections()::contains)
    }

    fun routeFor(section: ResumeOptionalSection): Any = when (section) {
        ResumeOptionalSection.Courses -> ResumeRoute.Courses
        ResumeOptionalSection.Links -> ResumeRoute.Links
        ResumeOptionalSection.Languages -> ResumeRoute.Languages
        ResumeOptionalSection.References -> ResumeRoute.References
    }

    fun navigateAfter(section: ResumeOptionalSection?) {
        val nextSection = nextOptionalSection(section)
        navController.navigate(nextSection?.let(::routeFor) ?: ResumeRoute.Summary)
    }

    fun totalSteps() = 5 + selectedSections().size

    fun optionalStep(section: ResumeOptionalSection): Int {
        val selectedInOrder = ResumeOptionalSection.entries.filter(selectedSections()::contains)
        return 5 + selectedInOrder.indexOf(section)
    }

    navigation<ResumeRoute.Graph>(
        startDestination = ResumeRoute.ResumeList,
    ) {
        composable<ResumeRoute.ResumeList> {
        val resumes by repository.resumes.collectAsState(initial = emptyList())
        val coroutineScope = rememberCoroutineScope()

        MyCvsScreen(
            resumes = resumes,
            importPolicy = importPolicy,
            onCreateCv = {
                draftState.startNew(emptySet())
                navController.navigate(ResumeRoute.SectionSelection)
            },
            onImportPdf = { uri ->
                navController.navigate(ResumeRoute.Importing(uri.toString()))
            },
            onEditResume = { resume ->
                draftState.load(resume)
                navController.navigate(ResumeRoute.SectionSelection)
            },
            onViewResume = { resume ->
                draftState.load(resume)
                navController.navigate(ResumeRoute.Preview)
            },
            onDeleteResume = { resume ->
                coroutineScope.launch { repository.delete(resume.id) }
            },
            onNavigateToHome = onNavigateToHome,
            onNavigateToSettings = onNavigateToSettings,
        )
        }

        composable<ResumeRoute.Importing> { backStackEntry ->
        val route = backStackEntry.toRoute<ResumeRoute.Importing>()
        ResumeImportScreen(
            documentUri = android.net.Uri.parse(route.uri),
            importPolicy = importPolicy,
            onBack = { navController.popBackStack() },
            onComplete = { importedResume ->
                draftState.load(importedResume)
                navController.navigate(ResumeRoute.SectionSelection) {
                    popUpTo(ResumeRoute.ResumeList) { inclusive = false }
                }
            },
        )
        }

        composable<ResumeRoute.SectionSelection> {
        SectionSelectionScreen(
            initialSections = draftState.document.optionalSections,
            onBack = { navController.popBackStack() },
            onConfirm = { sections ->
                draftState.updateOptionalSections(sections)
                navController.navigate(ResumeRoute.Contact)
            },
        )
        }

        composable<ResumeRoute.Contact> {
        ContactScreen(
            totalSteps = totalSteps(),
            initialData = draftState.document.contact,
            onBack = { navController.popBackStack() },
            onContinue = { contact ->
                draftState.updateContact(contact)
                navController.navigate(ResumeRoute.Experience)
            },
        )
        }

        composable<ResumeRoute.Experience> {
        ExperienceScreen(
            totalSteps = totalSteps(),
            experiences = draftState.document.experiences,
            onExperiencesChange = draftState::updateExperiences,
            onBack = { navController.popBackStack() },
            onContinue = {
                navController.navigate(ResumeRoute.Education)
            },
        )
        }

        composable<ResumeRoute.Education> {
        EducationScreen(
            totalSteps = totalSteps(),
            education = draftState.document.education,
            onEducationChange = draftState::updateEducation,
            onBack = { navController.popBackStack() },
            onContinue = {
                navController.navigate(ResumeRoute.Skills)
            },
        )
        }

        composable<ResumeRoute.Skills> {
        SkillsScreen(
            totalSteps = totalSteps(),
            initialSkills = draftState.document.skills,
            onBack = { navController.popBackStack() },
            onContinue = { skills ->
                draftState.updateSkills(skills)
                navigateAfter(null)
            },
        )
        }

        composable<ResumeRoute.Courses> {
        CoursesScreen(
            step = optionalStep(ResumeOptionalSection.Courses),
            totalSteps = totalSteps(),
            actionText = stringResource(R.string.ui_continue_4ad2b3),
            courses = draftState.document.courses,
            onCoursesChange = draftState::updateCourses,
            onBack = { navController.popBackStack() },
            onContinue = { navigateAfter(ResumeOptionalSection.Courses) },
        )
        }

        composable<ResumeRoute.Links> {
        LinksScreen(
            step = optionalStep(ResumeOptionalSection.Links),
            totalSteps = totalSteps(),
            actionText = stringResource(R.string.ui_continue_4ad2b3),
            links = draftState.document.links,
            onLinksChange = draftState::updateLinks,
            onBack = { navController.popBackStack() },
            onContinue = { navigateAfter(ResumeOptionalSection.Links) },
        )
        }

        composable<ResumeRoute.Languages> {
        LanguagesScreen(
            step = optionalStep(ResumeOptionalSection.Languages),
            totalSteps = totalSteps(),
            actionText = stringResource(R.string.ui_continue_4ad2b3),
            selectedLanguages = draftState.document.languages,
            onLanguagesChange = draftState::updateLanguages,
            onBack = { navController.popBackStack() },
            onContinue = { navigateAfter(ResumeOptionalSection.Languages) },
        )
        }

        composable<ResumeRoute.References> {
        ReferencesScreen(
            step = optionalStep(ResumeOptionalSection.References),
            totalSteps = totalSteps(),
            actionText = stringResource(R.string.ui_continue_4ad2b3),
            references = draftState.document.references,
            onReferencesChange = draftState::updateReferences,
            onBack = { navController.popBackStack() },
            onContinue = { navigateAfter(ResumeOptionalSection.References) },
        )
        }

        composable<ResumeRoute.Summary> {
        SummaryScreen(
            step = totalSteps(),
            totalSteps = totalSteps(),
            initialSummary = draftState.document.summary,
            initialFormatting = draftState.document.summaryFormatting,
            onBack = { navController.popBackStack() },
            onContinue = { summary, formatting ->
                draftState.updateSummary(summary, formatting)
                navController.navigate(ResumeRoute.Template)
            },
        )
        }

        composable<ResumeRoute.Template> {
        TemplateSelectionScreen(
            initialTemplate = draftState.document.templateName,
            onBack = { navController.popBackStack() },
            onContinue = { template ->
                draftState.updateTemplate(template)
                navController.navigate(ResumeRoute.Preview)
            },
        )
        }

        composable<ResumeRoute.Preview> {
        ResumePreviewScreen(
            resume = draftState.document,
            onBack = { navController.popBackStack() },
            onSave = {
                val savedResume = draftState.snapshotForSave()
                repository.save(savedResume)
                draftState.load(savedResume)
                navController.popBackStack(ResumeRoute.ResumeList, inclusive = false)
            },
            onExported = {
                val savedResume = draftState.snapshotForSave()
                repository.save(savedResume)
                draftState.load(savedResume)
            },
        )
        }
    }
}
