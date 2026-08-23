package com.urielhuerta.criterio.domain.engine

import com.urielhuerta.criterio.domain.model.*
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Motor Cognitivo de Análisis de Situaciones.
 * Desglosa objetivamente cualquier interacción interpersonal en hechos vs interpretaciones.
 */
class CognitiveAnalyzerEngine {

    fun analyzeSituation(inputText: String, isRawMode: Boolean): SituationAnalysis {
        val lower = inputText.lowercase()

        // 1. Extraer hechos observables
        val facts = mutableListOf<String>()
        if (lower.contains("horas") || lower.contains("minutos") || lower.contains("días") || lower.contains("dias")) {
            facts.add("Existe un lapso temporal medible entre interacciones o respuestas.")
        }
        if (lower.contains("visto") || lower.contains("leído") || lower.contains("leido") || lower.contains("mensaje")) {
            facts.add("Se registró un intercambio de mensajería digital.")
        }
        if (lower.contains("cita") || lower.contains("salida") || lower.contains("café") || lower.contains("cenar")) {
            facts.add("Hubo una propuesta o realización de encuentro presencial.")
        }
        if (facts.isEmpty()) {
            facts.add("Se describe una interacción social específica con datos verbales o conductuales.")
        }

        // 2. Extraer interpretaciones y suposiciones del usuario
        val userInterpretations = mutableListOf<String>()
        if (lower.contains("ignora") || lower.contains("pasa de mi") || lower.contains("desinterés") || lower.contains("desinteres")) {
            userInterpretations.add("Estás asumiendo desinterés o frialdad premeditada a partir de la pausa comunicativa.")
        }
        if (lower.contains("juega") || lower.contains("manipula") || lower.contains("hacerse la difícil") || lower.contains("test")) {
            userInterpretations.add("Estás asumiendo que la otra persona está ejecutando una estrategia psicológica de poder.")
        }
        if (lower.contains("otra persona") || lower.contains("otro hombre") || lower.contains("sale con alguien")) {
            userInterpretations.add("Estás deduciendo la existencia de un tercero sin confirmación factual.")
        }
        if (userInterpretations.isEmpty()) {
            userInterpretations.add("Estás atribuyendo intenciones internas a conductas externas sin corroboración directa.")
        }

        // 3. Identificar vacíos de información crítica
        val missingInfo = listOf(
            "Desconoces el contexto laboral, familiar o nivel de estrés de la otra persona en ese momento exacto.",
            "No cuentas con su historial habitual de hábitos digitales y tiempos de respuesta con otras personas.",
            "Careces de información sobre sus prioridades actuales y su disponibilidad emocional en esta etapa de su vida."
        )

        // 4. Hipótesis alternativas plausibles
        val alternativeHypotheses = listOf(
            "Hipótesis 1: Estaba ocupada con responsabilidades cotidianas y no priorizó el teléfono.",
            "Hipótesis 2: No sabía con certeza qué responder o deseaba tomarse tiempo para pensar.",
            "Hipótesis 3: Su estilo comunicativo habitual es de bajo contacto digital.",
            "Hipótesis 4: Su interés es moderado o neutro, esperando ver cómo evoluciona la dinámica sin urgencia."
        )

        // 5. Evidencias a favor y en contra
        val evidenceFor = listOf(
            "La conducta o silencio reportado ocurrió en el plano de los hechos observables."
        )
        val evidenceAgainst = listOf(
            "Un evento aislado no constituye una serie temporal estadísticamente significativa.",
            "No hay declaraciones verbales explícitas de rechazo o manipulación."
        )

        // 6. Nivel de incertidumbre
        val uncertainty = if (inputText.length < 150) "ALTO (Pocos datos contextuales)" else "MODERADO"

        // 7. Opciones de acción
        val options = listOf(
            "Opción A (Recomendada): Mantener la calma, continuar con tu rutina y permitir que el ritmo de la conversación sea natural sin forzar.",
            "Opción B: Si hay un plan pendiente en el aire, enviar una confirmación ligera y libre de reproches un día antes.",
            "Opción C: Si la falta de reciprocidad es crónica tras varias semanas, reducir tu inversión y redirigir tu atención hacia tus proyectos."
        )

        val risks = listOf(
            "Riesgo de sobre-reaccionar: Enviar reclamos o mensajes pasivo-agresivos destruirá la comodidad y denotará necesidad de aprobación.",
            "Riesgo de complacencia: Insistir repetidamente cuando no hay respuesta reduce tu dignidad y fomenta asimetría."
        )

        val recommendation = "No tomes decisiones basadas en suposiciones de lo que la otra persona 'podría estar pensando'. Evalúa únicamente patrones consolidados a lo largo de varias semanas y mantén tu enfoque en construir una vida plena e independiente."

        val whatNotToDo = listOf(
            "No envíes mensajes dobles de reclamo ('¿por qué no contestas?').",
            "No revises obsesivamente sus redes sociales buscando pistas ocultas.",
            "No asumas que eres el centro de sus pensamientos ni para bien ni para mal."
        )

        // Modo Crudo
        val rawFeedback = if (isRawMode) {
            "Estás invirtiendo demasiada energía mental en analizar micro-señales de alguien que apenas conoces. Si una persona tiene interés claro y disponibilidad, la comunicación fluye con facilidad razonable. Si tienes que descifrar cada mensaje como un enigma, la reciprocidad es baja o inexistente en este momento. Deja de buscar frases mágicas y enfócate en tu propia vida."
        } else null

        return SituationAnalysis(
            facts = facts,
            userInterpretations = userInterpretations,
            missingInformation = missingInfo,
            alternativeHypotheses = alternativeHypotheses,
            evidenceFor = evidenceFor,
            evidenceAgainst = evidenceAgainst,
            uncertaintyLevel = uncertainty,
            options = options,
            risks = risks,
            recommendation = recommendation,
            whatNotToDo = whatNotToDo,
            rawModeFeedback = rawFeedback
        )
    }
}

/**
 * Motor de Probabilidad Bayesiana para toma de decisiones y actualización de creencias.
 */
class BayesianProbabilityEngine {

    /**
     * Calcula P(H|E) = (P(E|H) * P(H)) / (P(E|H)*P(H) + P(E|~H)*P(~H))
     */
    fun calculateBayesianUpdate(
        priorH: Double,      // P(H) Probabilidad inicial de compatibilidad/interés (0.01 a 0.99)
        truePositive: Double, // P(E|H) Probabilidad de observar la señal si hay interés real (0.01 a 0.99)
        falsePositive: Double // P(E|~H) Probabilidad de observar la señal por casualidad / amabilidad (0.01 a 0.99)
    ): BayesianCalculation {
        val pNotH = 1.0 - priorH
        val numerator = truePositive * priorH
        val denominator = (truePositive * priorH) + (falsePositive * pNotH)
        val posterior = if (denominator > 0) numerator / denominator else priorH

        val explanation = when {
            posterior > 0.75 -> "La señal actualiza significativamente la probabilidad a favor de la hipótesis (Fuerte evidencia positiva)."
            posterior in 0.40..0.75 -> "La señal aporta información moderada, pero persiste un margen notable de incertidumbre. Se requiere observar consistencia."
            else -> "La tasa de falsos positivos o la baja probabilidad inicial mantienen la probabilidad en niveles bajos. Una señal aislada no compensa una baja tasa base."
        }

        return BayesianCalculation(
            priorProbability = priorH,
            truePositiveRate = truePositive,
            falsePositiveRate = falsePositive,
            posteriorProbability = (posterior * 1000).roundToInt() / 1000.0,
            qualitativeExplanation = explanation
        )
    }
}

/**
 * Algoritmo de Repetición Espaciada SM-2 (SuperMemo-2).
 */
class SpacedRepetitionEngine {

    /**
     * @param rating Calificación del usuario de 0 (olvido total) a 5 (respuesta perfecta e inmediata).
     */
    fun calculateNextReview(
        currentRepetitions: Int,
        currentIntervalDays: Int,
        currentEaseFactor: Float,
        rating: Int
    ): Triple<Int, Int, Float> {
        val q = rating.coerceIn(0, 5)

        // Nuevo factor de facilidad: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
        val newEaseFactor = max(1.3f, currentEaseFactor + (0.1f - (5 - q) * (0.08f + (5 - q) * 0.02f)))

        val newRepetitions: Int
        val newIntervalDays: Int

        if (q < 3) {
            // Si falló (rating 0, 1 o 2), reinicia repeticiones e intervalo a 1 día
            newRepetitions = 0
            newIntervalDays = 1
        } else {
            newRepetitions = currentRepetitions + 1
            newIntervalDays = when (newRepetitions) {
                1 -> 1
                2 -> 6
                else -> (currentIntervalDays * newEaseFactor).roundToInt()
            }
        }

        return Triple(newRepetitions, newIntervalDays, newEaseFactor)
    }
}

/**
 * Calculador del Indicador Educativo de Reciprocidad.
 */
class ReciprocityCalculator {

    fun assessReciprocity(
        initiative: Int,    // 1-5
        effort: Int,        // 1-5
        consistency: Int,   // 1-5
        communication: Int  // 1-5
    ): ReciprocityAssessment {
        val avg = (initiative + effort + consistency + communication) / 4.0
        val level = when {
            avg >= 3.8 -> ReciprocityLevel.ALTA
            avg >= 2.4 -> ReciprocityLevel.MEDIA
            else -> ReciprocityLevel.BAJA
        }

        val observations = mutableListOf<String>()
        if (initiative <= 2) observations.add("Baja iniciativa: Prácticamente todos los contactos y planes son propuestos por ti.")
        else observations.add("Buena iniciativa: Hay equilibrio en quién inicia las conversaciones y propone planes.")

        if (effort <= 2) observations.add("Bajo esfuerzo logístico: La otra persona rara vez se desplaza o ajusta sus horarios.")
        else observations.add("Esfuerzo balanceado: Ambas partes invierten tiempo y recursos equitativamente.")

        if (consistency <= 2) observations.add("Inconsistencia: Patrones impredecibles de interés seguidos de desapariciones.")
        else observations.add("Consistencia sólida: Las palabras coinciden con los comportamientos observables.")

        val explanation = when (level) {
            ReciprocityLevel.ALTA -> "Existe un equilibrio observable en la inversión de tiempo, energía y comunicación. La dinámica muestra bases saludables de mutua cooperación."
            ReciprocityLevel.MEDIA -> "Hay señales mixtas. Puede existir interés incipiente pero aún no consolidado, o diferencias en estilos de comunicación. Mantén una inversión moderada."
            ReciprocityLevel.BAJA -> "Asimetría marcada. Estás invirtiendo significativamente más recursos emocionales y de tiempo que la otra persona. Se recomienda calibrar tu inversión a la baja."
        }

        return ReciprocityAssessment(
            initiativeScore = initiative,
            effortScore = effort,
            consistencyScore = consistency,
            communicationScore = communication,
            balanceLevel = level,
            explanation = explanation,
            observations = observations
        )
    }
}

/**
 * Motor de Calibración Probabilística y Brier Score.
 */
class CalibrationEngine {

    /**
     * Brier Score = (1/N) * sum((prob - outcome)^2), donde outcome es 1.0 (ocurrió) o 0.0 (no ocurrió).
     * Puntuación entre 0.0 (calibración perfecta) y 1.0 (completamente errado).
     */
    fun calculateBrierScore(predictions: List<Pair<Double, Boolean>>): Double {
        if (predictions.isEmpty()) return 0.0
        val sum = predictions.sumOf { (prob, outcome) ->
            val outcomeVal = if (outcome) 1.0 else 0.0
            (prob - outcomeVal).pow(2)
        }
        return (sum / predictions.size * 1000).roundToInt() / 1000.0
    }

    fun getCalibrationStats(predictions: List<Pair<Double, Boolean>>): CalibrationStats {
        val total = predictions.size
        if (total == 0) {
            return CalibrationStats(0.0, 0, 0, 0.0, "Sin predicciones resueltas")
        }

        val brier = calculateBrierScore(predictions)
        val correctCount = predictions.count { (prob, outcome) ->
            (prob >= 0.5 && outcome) || (prob < 0.5 && !outcome)
        }
        val accuracy = (correctCount.toDouble() / total) * 100.0

        val avgProb = predictions.map { it.first }.average()
        val actualRate = predictions.count { it.second }.toDouble() / total

        val tendency = when {
            brier <= 0.15 -> "Excelente Calibración Racional"
            avgProb > actualRate + 0.15 -> "Sesgo de Sobreconfianza (Asumes más certeza de la real)"
            avgProb < actualRate - 0.15 -> "Sesgo de Infraestimación / Paranoia (Esperas el peor resultado innecesariamente)"
            else -> "Calibración Aceptable"
        }

        return CalibrationStats(
            brierScore = brier,
            totalPredictions = total,
            resolvedPredictions = total,
            accuracyPercentage = (accuracy * 10).roundToInt() / 10.0,
            tendencyLabel = tendency
        )
    }
}

/**
 * Detector de Patrones Cognitivos y Relacionales en el Diario.
 */
class PatternDetectionEngine {

    fun detectPatternsInEntries(
        interpretations: List<String>,
        emotions: List<String>
    ): List<String> {
        val detected = mutableListOf<String>()
        val total = interpretations.size
        if (total < 2) return detected

        val combinedText = interpretations.joinToString(" ").lowercase()

        val mindReadingCount = interpretations.count {
            val t = it.lowercase()
            t.contains("piensa que") || t.contains("seguro cree") || t.contains("quiere hacerme") || t.contains("está jugando")
        }
        if (mindReadingCount >= 2) {
            detected.add("Patrón de Lectura de Mente detectado en $mindReadingCount entradas: Tiendes a asumir lo que la otra persona piensa sin pruebas directas.")
        }

        val timeParanoiaCount = interpretations.count {
            val t = it.lowercase()
            t.contains("tardó") || t.contains("horas") || t.contains("visto") || t.contains("en línea")
        }
        if (timeParanoiaCount >= 2) {
            detected.add("Patrón de Hipersensibilidad Temporal: Has registrado $timeParanoiaCount situaciones donde analizas obsesivamente los minutos de respuesta digital.")
        }

        val anxietyCount = emotions.count {
            val e = it.lowercase()
            e.contains("ansiedad") || e.contains("miedo") || e.contains("inseguridad") || e.contains("angustia")
        }
        if (anxietyCount >= total / 2) {
            detected.add("Predominancia Emocional de Inseguridad: Más del 50% de tus registros van acompañados de estados ansiosos frente a la ambigüedad.")
        }

        return detected
    }
}

/**
 * Entrenador de Voz: Análisis acústico y de cadencia verbal.
 */
class VoiceCoachEngine {

    data class VoiceAnalysisResult(
        val wordsPerMinute: Int,
        val pauseQuality: String,
        val fillerWordCount: Int,
        val feedbackSummary: String
    )

    fun evaluateSpeechSample(
        durationSeconds: Int,
        estimatedWords: Int,
        detectedFillerWords: List<String>
    ): VoiceAnalysisResult {
        val wpm = if (durationSeconds > 0) (estimatedWords * 60) / durationSeconds else 120
        val pauseQuality = when {
            wpm > 160 -> "Ritmo Acelerado (Posible ansiedad o urgencia por terminar)"
            wpm < 100 -> "Ritmo Excesivamente Lento (Puede perder dinamismo)"
            else -> "Cadencia Óptima (110-150 palabras/minuto, transmite serenidad y seguridad)"
        }

        val fillerCount = detectedFillerWords.size
        val feedback = buildString {
            append("Tu velocidad estimada fue de $wpm palabras por minuto ($pauseQuality). ")
            if (fillerCount > 3) {
                append("Detectamos $fillerCount muletillas (${detectedFillerWords.take(3).joinToString(", ")}). Intenta reemplazar las muletillas con pausas de silencio conscientes.")
            } else {
                append("Excelente control de muletillas y pausas limpias.")
            }
        }

        return VoiceAnalysisResult(
            wordsPerMinute = wpm,
            pauseQuality = pauseQuality,
            fillerWordCount = fillerCount,
            feedbackSummary = feedback
        )
    }
}
