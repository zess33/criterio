package com.urielhuerta.criterio.domain.model

enum class EvidenceLevel(val label: String, val description: String) {
    HIGH_EVIDENCE("Alta Evidencia Científica", "Respaldado por meta-análisis y estudios de replicación en psicología social y cognitiva."),
    MODERATE_EVIDENCE("Evidencia Moderada", "Respaldado por estudios empíricos observacionales con muestras representativas."),
    LIMITED_EVIDENCE("Evidencia Limitada", "Hipótesis teóricas válidas pero con muestras pequeñas o contexto-dependientes."),
    OPINION("Perspectiva / Opinión", "Reflexión filosófica o experiencia práctica sin estatus de hecho científico."),
    BELIEF_SYSTEM("Sistema de Creencias", "Práctica cultural o creencia personal respetable, no evidencia empírica.")
}

enum class ModuleCategory {
    FUNDAMENTOS,
    CONVERSACION,
    CONFIANZA,
    ATRACCION,
    CITAS,
    RELACIONES,
    PSICOLOGIA,
    PENSAMIENTO_CRITICO,
    ESTADISTICA,
    BAYES_DECISIONES,
    DINAMICAS_COMPLEJAS
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class SituationAnalysis(
    val facts: List<String>,
    val userInterpretations: List<String>,
    val missingInformation: List<String>,
    val alternativeHypotheses: List<String>,
    val evidenceFor: List<String>,
    val evidenceAgainst: List<String>,
    val uncertaintyLevel: String, // "ALTO", "MODERADO", "BAJO"
    val options: List<String>,
    val risks: List<String>,
    val recommendation: String,
    val whatNotToDo: List<String>,
    val rawModeFeedback: String? = null
)

data class ReciprocityAssessment(
    val initiativeScore: Int,    // 1-5
    val effortScore: Int,        // 1-5
    val consistencyScore: Int,   // 1-5
    val communicationScore: Int, // 1-5
    val balanceLevel: ReciprocityLevel,
    val explanation: String,
    val observations: List<String>
)

enum class ReciprocityLevel(val label: String) {
    BAJA("Baja Reciprocidad Observable"),
    MEDIA("Reciprocidad Moderada / En Construcción"),
    ALTA("Alta Reciprocidad y Cooperación Mutua")
}

data class BayesianCalculation(
    val priorProbability: Double,       // P(H)
    val truePositiveRate: Double,       // P(E|H)
    val falsePositiveRate: Double,      // P(E|not H)
    val posteriorProbability: Double,   // P(H|E)
    val qualitativeExplanation: String
)

data class CalibrationStats(
    val brierScore: Double,
    val totalPredictions: Int,
    val resolvedPredictions: Int,
    val accuracyPercentage: Double,
    val tendencyLabel: String // "Bien Calibrado", "Sobreconfiado", "Paranoico / Infraestimador"
)

data class SimulationMessage(
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER,
    PERSONA,
    COACH
}

data class SimulationScorecard(
    val totalScore: Int, // 0-100
    val clarityScore: Int,
    val pressureScore: Int,
    val contextReadingScore: Int,
    val strengths: List<String>,
    val areasToImprove: List<String>,
    val alternativeResponses: List<String>,
    val summaryFeedback: String
)
