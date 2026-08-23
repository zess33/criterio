package com.urielhuerta.criterio.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.urielhuerta.criterio.domain.model.EvidenceLevel
import com.urielhuerta.criterio.domain.model.ModuleCategory

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val levelIndex: Int,
    val title: String,
    val description: String,
    val category: ModuleCategory,
    val requiredScore: Int,
    val isUnlocked: Boolean = true,
    val estimatedMinutes: Int = 45
)

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = ModuleEntity::class,
            parentColumns = ["id"],
            childColumns = ["moduleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("moduleId")]
)
data class LessonEntity(
    @PrimaryKey val id: String,
    val moduleId: String,
    val orderIndex: Int,
    val title: String,
    val evidenceLevel: EvidenceLevel,
    val conceptExplanation: String,
    val examplesJson: String,         // List<String> encoded as JSON
    val counterExamplesJson: String,  // List<String> encoded as JSON
    val commonErrorsJson: String,     // List<String> encoded as JSON
    val isCompleted: Boolean = false,
    val quizDataJson: String? = null
)

@Entity(tableName = "spaced_repetition_cards")
data class SpacedRepetitionCardEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val question: String,
    val answer: String,
    val explanation: String,
    val repetitions: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Float = 2.5f,
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val lastReviewedTimestamp: Long = 0L
)

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val personAlias: String,
    val contextDescription: String,
    val observableFacts: String,
    val userInterpretation: String,
    val primaryEmotion: String,
    val actionTaken: String,
    val actualOutcome: String,
    val detectedDistortionsJson: String = "[]",
    val aiCognitiveFeedback: String = ""
)

@Entity(tableName = "predictions")
data class PredictionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val scenario: String,
    val hypothesis: String,
    val estimatedProbability: Double, // 0.0 to 1.0
    val actualOutcome: Boolean? = null, // null while pending, true or false when resolved
    val resolvedTimestamp: Long? = null,
    val brierScoreContribution: Double? = null
)

@Entity(tableName = "simulation_records")
data class SimulationRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scenarioId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val totalScore: Int,
    val clarityScore: Int,
    val pressureScore: Int,
    val contextReadingScore: Int,
    val strengthsJson: String,
    val areasToImproveJson: String,
    val transcriptJson: String
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isUnlocked: Boolean = false,
    val unlockedAtTimestamp: Long? = null
)
