package com.urielhuerta.criterio.data.preloader

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.urielhuerta.criterio.data.local.CriterioDatabase
import com.urielhuerta.criterio.data.local.entities.*
import com.urielhuerta.criterio.domain.model.EvidenceLevel
import com.urielhuerta.criterio.domain.model.ModuleCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

data class JsonModule(
    val id: String,
    val levelIndex: Int,
    val title: String,
    val description: String,
    val category: String,
    val requiredScore: Int,
    val estimatedMinutes: Int,
    val lessons: List<JsonLesson>
)

data class JsonLesson(
    val id: String,
    val orderIndex: Int,
    val title: String,
    val evidenceLevel: String,
    val conceptExplanation: String,
    val examples: List<String>,
    val counterExamples: List<String>,
    val commonErrors: List<String>,
    val quizDataJson: String?
)

class ContentPreloader(
    private val context: Context,
    private val database: CriterioDatabase
) {
    private val gson = Gson()

    suspend fun preloadIfNeeded() = withContext(Dispatchers.IO) {
        val moduleDao = database.moduleDao()
        val lessonDao = database.lessonDao()
        val cardDao = database.spacedRepetitionDao()
        val achievementDao = database.achievementDao()

        // Si ya existen lecciones cargadas, no sobreescribir ni resetear el progreso
        if (lessonDao.getLessonsCount() > 0) {
            return@withContext
        }

        // 1. Precargar Módulos y Lecciones si no existen
        try {
            val jsonStream = context.assets.open("modules_data.json")
            val type = object : TypeToken<List<JsonModule>>() {}.type
            val modulesJson: List<JsonModule> = gson.fromJson(InputStreamReader(jsonStream), type)

            val moduleEntities = mutableListOf<ModuleEntity>()
            val lessonEntities = mutableListOf<LessonEntity>()
            val cardEntities = mutableListOf<SpacedRepetitionCardEntity>()

            modulesJson.forEach { jm ->
                val categoryEnum = try {
                    ModuleCategory.valueOf(jm.category)
                } catch (e: Exception) {
                    ModuleCategory.FUNDAMENTOS
                }

                moduleEntities.add(
                    ModuleEntity(
                        id = jm.id,
                        levelIndex = jm.levelIndex,
                        title = jm.title,
                        description = jm.description,
                        category = categoryEnum,
                        requiredScore = jm.requiredScore,
                        isUnlocked = jm.levelIndex == 0,
                        estimatedMinutes = jm.estimatedMinutes
                    )
                )

                jm.lessons.forEach { jl ->
                    val evidenceEnum = try {
                        EvidenceLevel.valueOf(jl.evidenceLevel)
                    } catch (e: Exception) {
                        EvidenceLevel.MODERATE_EVIDENCE
                    }

                    lessonEntities.add(
                        LessonEntity(
                            id = jl.id,
                            moduleId = jm.id,
                            orderIndex = jl.orderIndex,
                            title = jl.title,
                            evidenceLevel = evidenceEnum,
                            conceptExplanation = jl.conceptExplanation,
                            examplesJson = gson.toJson(jl.examples),
                            counterExamplesJson = gson.toJson(jl.counterExamples),
                            commonErrorsJson = gson.toJson(jl.commonErrors),
                            isCompleted = false,
                            quizDataJson = jl.quizDataJson
                        )
                    )

                    // Crear tarjeta inicial de repaso espaciado para cada lección
                    cardEntities.add(
                        SpacedRepetitionCardEntity(
                            id = "card_${jl.id}",
                            lessonId = jl.id,
                            question = "Concepto clave: ${jl.title}",
                            answer = jl.conceptExplanation.take(180) + "...",
                            explanation = jl.conceptExplanation,
                            nextReviewTimestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            moduleDao.insertModules(moduleEntities)
            lessonDao.insertLessons(lessonEntities)
            cardDao.insertCards(cardEntities)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Precargar Logros Iniciales
        try {
            val initialAchievements = listOf(
                AchievementEntity("ach_first_analysis", "Primer Análisis Racional", "Completaste tu primer desglose de hechos vs interpretaciones", "PENSAMIENTO_CRITICO"),
                AchievementEntity("ach_first_sim", "Simulación Superada", "Completaste tu primera práctica de conversación interactiva", "CONVERSACION"),
                AchievementEntity("ach_bayesian_mind", "Mente Bayesiana", "Actualizaste una probabilidad calculando la incertidumbre", "ESTADISTICA"),
                AchievementEntity("ach_brier_calibrated", "Predicción Calibrada", "Registraste y verificaste tu primera predicción social", "PROBABILIDAD"),
                AchievementEntity("ach_healthy_boundary", "Límite Establecido", "Completaste el módulo de límites y respeto mutuo", "CONFIANZA"),
                AchievementEntity("ach_rejection_master", "Aceptación del Rechazo", "Superaste el módulo de manejo digno del rechazo", "MADUREZ_EMOCIONAL")
            )
            achievementDao.insertAchievements(initialAchievements)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
