package com.urielhuerta.criterio

import android.app.Application
import com.urielhuerta.criterio.data.local.CriterioDatabase
import com.urielhuerta.criterio.data.preferences.UserPreferencesRepository
import com.urielhuerta.criterio.data.preloader.ContentPreloader
import com.urielhuerta.criterio.data.remote.GeminiApiClient
import com.urielhuerta.criterio.data.repository.*
import com.urielhuerta.criterio.domain.engine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CriterioApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Local Database & DataStore
    lateinit var database: CriterioDatabase private set
    lateinit var userPreferencesRepository: UserPreferencesRepository private set

    // Domain Calculation Engines
    val cognitiveAnalyzerEngine by lazy { CognitiveAnalyzerEngine() }
    val bayesianProbabilityEngine by lazy { BayesianProbabilityEngine() }
    val spacedRepetitionEngine by lazy { SpacedRepetitionEngine() }
    val reciprocityCalculator by lazy { ReciprocityCalculator() }
    val calibrationEngine by lazy { CalibrationEngine() }
    val patternDetectionEngine by lazy { PatternDetectionEngine() }
    val voiceCoachEngine by lazy { VoiceCoachEngine() }
    val darkPsychologyEngine by lazy { DarkPsychologyEngine() }

    // Repositories
    lateinit var educationRepository: EducationRepository private set
    lateinit var spacedRepetitionRepository: SpacedRepetitionRepository private set
    lateinit var journalRepository: JournalRepository private set
    lateinit var predictionRepository: PredictionRepository private set
    lateinit var simulationRepository: SimulationRepository private set
    lateinit var achievementRepository: AchievementRepository private set

    override fun onCreate() {
        super.onCreate()

        // 1. Iniciar Base de Datos y Preferencias
        database = CriterioDatabase.getInstance(this)
        userPreferencesRepository = UserPreferencesRepository(this)

        // 2. Iniciar Repositorios
        educationRepository = EducationRepository(database)
        spacedRepetitionRepository = SpacedRepetitionRepository(database, spacedRepetitionEngine)
        journalRepository = JournalRepository(database, patternDetectionEngine)
        predictionRepository = PredictionRepository(database, calibrationEngine)
        simulationRepository = SimulationRepository(database, cognitiveAnalyzerEngine, GeminiApiClient.service)
        achievementRepository = AchievementRepository(database)

        // 3. Precargar contenido en background
        applicationScope.launch {
            ContentPreloader(this@CriterioApp, database).preloadIfNeeded()
            userPreferencesRepository.updateStreak()
        }
    }
}
