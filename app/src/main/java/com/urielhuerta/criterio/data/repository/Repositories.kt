package com.urielhuerta.criterio.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.urielhuerta.criterio.data.local.CriterioDatabase
import com.urielhuerta.criterio.data.local.dao.ModuleWithLessons
import com.urielhuerta.criterio.data.local.entities.*
import com.urielhuerta.criterio.data.remote.*
import com.urielhuerta.criterio.domain.engine.*
import com.urielhuerta.criterio.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class EducationRepository(private val database: CriterioDatabase) {
    private val moduleDao = database.moduleDao()
    private val lessonDao = database.lessonDao()

    fun getModulesWithLessons(): Flow<List<ModuleWithLessons>> = moduleDao.getModulesWithLessons()

    suspend fun getModuleWithLessonsById(moduleId: String): ModuleWithLessons? =
        moduleDao.getModuleWithLessonsById(moduleId)

    suspend fun getLessonById(lessonId: String): LessonEntity? = lessonDao.getLessonById(lessonId)

    suspend fun completeLesson(lessonId: String, completed: Boolean = true) {
        lessonDao.setLessonCompleted(lessonId, completed)
    }

    fun getCompletedLessonsCount(): Flow<Int> = lessonDao.getCompletedLessonsCount()
    fun getTotalLessonsCount(): Flow<Int> = lessonDao.getTotalLessonsCount()
}

class SpacedRepetitionRepository(
    private val database: CriterioDatabase,
    private val sm2Engine: SpacedRepetitionEngine
) {
    private val cardDao = database.spacedRepetitionDao()

    fun getCardsDueForReview(): Flow<List<SpacedRepetitionCardEntity>> = cardDao.getCardsDueForReview()
    fun getCardsDueCount(): Flow<Int> = cardDao.getCardsDueCount()

    suspend fun reviewCard(card: SpacedRepetitionCardEntity, rating: Int) {
        val (newRepetitions, newIntervalDays, newEaseFactor) = sm2Engine.calculateNextReview(
            currentRepetitions = card.repetitions,
            currentIntervalDays = card.intervalDays,
            currentEaseFactor = card.easeFactor,
            rating = rating
        )
        val nextReview = System.currentTimeMillis() + (newIntervalDays * 24 * 60 * 60 * 1000L)
        val updatedCard = card.copy(
            repetitions = newRepetitions,
            intervalDays = newIntervalDays,
            easeFactor = newEaseFactor,
            nextReviewTimestamp = nextReview,
            lastReviewedTimestamp = System.currentTimeMillis()
        )
        cardDao.updateCard(updatedCard)
    }
}

class JournalRepository(
    private val database: CriterioDatabase,
    private val patternEngine: PatternDetectionEngine
) {
    private val journalDao = database.journalDao()
    private val gson = Gson()

    fun getAllEntries(): Flow<List<JournalEntity>> = journalDao.getAllJournalEntries()

    suspend fun addEntry(
        personAlias: String,
        context: String,
        facts: String,
        interpretation: String,
        emotion: String,
        action: String,
        outcome: String
    ): Long {
        val entry = JournalEntity(
            personAlias = personAlias,
            contextDescription = context,
            observableFacts = facts,
            userInterpretation = interpretation,
            primaryEmotion = emotion,
            actionTaken = action,
            actualOutcome = outcome,
            aiCognitiveFeedback = "Registro almacenado privadamente para análisis de patrones cognitivos."
        )
        return journalDao.insertEntry(entry)
    }

    suspend fun getDetectedPatterns(): List<String> {
        val entries = journalDao.getAllJournalEntries().first()
        val interpretations = entries.map { it.userInterpretation }
        val emotions = entries.map { it.primaryEmotion }
        return patternEngine.detectPatternsInEntries(interpretations, emotions)
    }
}

class PredictionRepository(
    private val database: CriterioDatabase,
    private val calibrationEngine: CalibrationEngine
) {
    private val predictionDao = database.predictionDao()

    fun getAllPredictions(): Flow<List<PredictionEntity>> = predictionDao.getAllPredictions()
    fun getResolvedPredictions(): Flow<List<PredictionEntity>> = predictionDao.getResolvedPredictions()
    fun getPendingPredictions(): Flow<List<PredictionEntity>> = predictionDao.getPendingPredictions()

    suspend fun addPrediction(scenario: String, hypothesis: String, estimatedProbability: Double): Long {
        val entity = PredictionEntity(
            scenario = scenario,
            hypothesis = hypothesis,
            estimatedProbability = estimatedProbability
        )
        return predictionDao.insertPrediction(entity)
    }

    suspend fun resolvePrediction(predictionId: Long, actualOutcome: Boolean) {
        val existing = predictionDao.getPredictionById(predictionId) ?: return
        val outcomeValue = if (actualOutcome) 1.0 else 0.0
        val brierContribution = (existing.estimatedProbability - outcomeValue) * (existing.estimatedProbability - outcomeValue)
        val updated = existing.copy(
            actualOutcome = actualOutcome,
            resolvedTimestamp = System.currentTimeMillis(),
            brierScoreContribution = brierContribution
        )
        predictionDao.updatePrediction(updated)
    }

    suspend fun getCalibrationStats(): CalibrationStats {
        val resolved = predictionDao.getResolvedPredictions().first()
        val pairs = resolved.map { Pair(it.estimatedProbability, it.actualOutcome ?: false) }
        return calibrationEngine.getCalibrationStats(pairs)
    }
}

class SimulationRepository(
    private val database: CriterioDatabase,
    private val cognitiveEngine: CognitiveAnalyzerEngine,
    private val geminiApiService: GeminiApiService
) {
    private val simulationDao = database.simulationDao()
    private val gson = Gson()

    fun getAllRecords(): Flow<List<SimulationRecordEntity>> = simulationDao.getAllRecords()

    suspend fun saveRecord(
        scenarioId: String,
        durationSeconds: Int,
        scorecard: SimulationScorecard,
        messages: List<SimulationMessage>
    ): Long {
        val record = SimulationRecordEntity(
            scenarioId = scenarioId,
            durationSeconds = durationSeconds,
            totalScore = scorecard.totalScore,
            clarityScore = scorecard.clarityScore,
            pressureScore = scorecard.pressureScore,
            contextReadingScore = scorecard.contextReadingScore,
            strengthsJson = gson.toJson(scorecard.strengths),
            areasToImproveJson = gson.toJson(scorecard.areasToImprove),
            transcriptJson = gson.toJson(messages)
        )
        return simulationDao.insertRecord(record)
    }

    suspend fun getAiPersonaResponse(
        personaName: String,
        personaContext: String,
        conversationHistory: List<SimulationMessage>,
        userMessage: String,
        apiKey: String
    ): String {
        if (apiKey.isNotBlank()) {
            try {
                val systemPrompt = "Eres $personaName en una simulación de conversación interpersonal. Contexto: $personaContext. Responde con realismo, calibración y naturalidad como una persona real, ni complaciente ni hostil. Mantén respuestas breves (1-3 frases)."
                val contents = conversationHistory.map { msg ->
                    GeminiContent(
                        role = if (msg.sender == MessageSender.USER) "user" else "model",
                        parts = listOf(GeminiPart(msg.text))
                    )
                } + GeminiContent(role = "user", parts = listOf(GeminiPart(userMessage)))

                val request = GeminiRequest(
                    contents = contents,
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(systemPrompt)))
                )
                val response = geminiApiService.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) return text.trim()
            } catch (e: Exception) {
                // Fallback a motor local
            }
        }

        // Motor local offline de simulación de diálogo
        val lower = userMessage.lowercase()
        return when {
            lower.contains("enchufe") || lower.contains("cargador") -> "Sí, justo debajo de la mesa de la esquina hay uno libre. Gracias por avisar."
            lower.contains("libro") || lower.contains("filosofía") || lower.contains("leyendo") -> "Ah, sí, lo empecé hace poco por recomendación de una amiga. Es bastante interesante."
            lower.contains("cita") || lower.contains("salir") -> "Aprecio la invitación, pero prefiero que nos conozcamos un poco más primero por aquí."
            lower.length < 5 -> "Entiendo..."
            else -> "Interesante punto. Nunca lo había pensado de esa manera exactamente."
        }
    }
}

class AchievementRepository(private val database: CriterioDatabase) {
    private val achievementDao = database.achievementDao()
    fun getAllAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()
    suspend fun unlock(id: String) = achievementDao.unlockAchievement(id)
}
