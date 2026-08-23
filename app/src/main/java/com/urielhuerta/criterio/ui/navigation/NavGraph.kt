package com.urielhuerta.criterio.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.urielhuerta.criterio.CriterioApp
import com.urielhuerta.criterio.ui.screens.academy.*
import com.urielhuerta.criterio.ui.screens.analyzer.*
import com.urielhuerta.criterio.ui.screens.darkpsychology.*
import com.urielhuerta.criterio.ui.screens.home.*
import com.urielhuerta.criterio.ui.screens.journal.*
import com.urielhuerta.criterio.ui.screens.onboarding.*
import com.urielhuerta.criterio.ui.screens.predictions.*
import com.urielhuerta.criterio.ui.screens.settings.*
import com.urielhuerta.criterio.ui.screens.simulator.*
import com.urielhuerta.criterio.ui.screens.stats.*
import com.urielhuerta.criterio.ui.screens.testme.*
import com.urielhuerta.criterio.ui.screens.tools.*

@Composable
fun CriterioNavGraph(
    navController: NavHostController,
    app: CriterioApp,
    startDestination: String
) {
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Academy,
        Screen.Analyzer,
        Screen.Simulator,
        Screen.Journal
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    // ViewModels instanciados desde el Service Locator de la App
    val onboardingViewModel = remember { OnboardingViewModel(app.userPreferencesRepository) }
    val homeViewModel = remember { HomeViewModel(app.educationRepository, app.spacedRepetitionRepository, app.userPreferencesRepository) }
    val academyViewModel = remember { AcademyViewModel(app.educationRepository) }
    val analyzerViewModel = remember { AnalyzerViewModel(app.cognitiveAnalyzerEngine, app.userPreferencesRepository) }
    val simulatorViewModel = remember { SimulatorViewModel(app.simulationRepository, app.userPreferencesRepository) }
    val journalViewModel = remember { JournalViewModel(app.journalRepository) }
    val predictionsViewModel = remember { PredictionsViewModel(app.predictionRepository) }
    val testMeViewModel = remember { TestMeViewModel() }
    val darkPsychologyViewModel = remember { DarkPsychologyViewModel(app.darkPsychologyEngine) }
    val statsViewModel = remember { StatsViewModel(app.educationRepository, app.achievementRepository) }
    val settingsViewModel = remember { SettingsViewModel(app.userPreferencesRepository, app.appUpdateManager) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToLesson = { lessonId ->
                        navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                    },
                    onNavigateToAnalyzer = { navController.navigate(Screen.Analyzer.route) },
                    onNavigateToSimulator = { navController.navigate(Screen.Simulator.route) },
                    onNavigateToTestMe = { navController.navigate(Screen.TestMe.route) },
                    onNavigateToInternetAdvice = { navController.navigate(Screen.InternetAdvice.route) },
                    onNavigateToBayes = { navController.navigate(Screen.BayesLab.route) },
                    onNavigateToReciprocity = { navController.navigate(Screen.ReciprocityTool.route) },
                    onNavigateToVoiceCoach = { navController.navigate(Screen.VoiceCoach.route) },
                    onNavigateToDarkPsychology = { navController.navigate(Screen.DarkPsychology.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Academy.route) {
                AcademyScreen(
                    viewModel = academyViewModel,
                    onNavigateToLesson = { lessonId ->
                        navController.navigate(Screen.LessonDetail.createRoute(lessonId))
                    }
                )
            }

            composable(
                route = Screen.LessonDetail.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
                LessonDetailScreen(
                    lessonId = lessonId,
                    viewModel = academyViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNextLesson = { nextId ->
                        navController.navigate(Screen.LessonDetail.createRoute(nextId)) {
                            popUpTo(Screen.LessonDetail.createRoute(lessonId)) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Analyzer.route) {
                SituationAnalyzerScreen(viewModel = analyzerViewModel)
            }

            composable(Screen.InternetAdvice.route) {
                InternetAdviceAnalyzerScreen(
                    viewModel = analyzerViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Simulator.route) {
                SimulatorLobbyScreen(
                    viewModel = simulatorViewModel,
                    onStartScenario = { scenarioId ->
                        navController.navigate(Screen.ChatSimulation.createRoute(scenarioId))
                    }
                )
            }

            composable(
                route = Screen.ChatSimulation.route,
                arguments = listOf(navArgument("scenarioId") { type = NavType.StringType })
            ) { backStackEntry ->
                val scenarioId = backStackEntry.arguments?.getString("scenarioId") ?: ""
                ChatSimulationScreen(
                    scenarioId = scenarioId,
                    viewModel = simulatorViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TestMe.route) {
                TestMeScreen(
                    viewModel = testMeViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Journal.route) {
                JournalScreen(
                    viewModel = journalViewModel,
                    onNavigateToNewEntry = { navController.navigate(Screen.NewJournalEntry.route) }
                )
            }

            composable(Screen.NewJournalEntry.route) {
                NewJournalEntryScreen(
                    viewModel = journalViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Predictions.route) {
                PredictionsScreen(
                    viewModel = predictionsViewModel,
                    onNavigateToNewPrediction = { navController.navigate(Screen.NewPrediction.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.NewPrediction.route) {
                NewPredictionScreen(
                    viewModel = predictionsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BayesLab.route) {
                BayesLabScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.ReciprocityTool.route) {
                ReciprocityScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.VoiceCoach.route) {
                VoiceCoachScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.DarkPsychology.route) {
                DarkPsychologyScreen(
                    viewModel = darkPsychologyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = statsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
