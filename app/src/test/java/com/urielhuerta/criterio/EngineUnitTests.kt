package com.urielhuerta.criterio

import com.urielhuerta.criterio.domain.engine.*
import com.urielhuerta.criterio.domain.model.ReciprocityLevel
import org.junit.Assert.*
import org.junit.Test

class EngineUnitTests {

    @Test
    fun testBayesianCalculation_exactMathematics() {
        val engine = BayesianProbabilityEngine()
        // Prior: 0.20, TruePositive: 0.80, FalsePositive: 0.40
        // Expected: 0.16 / (0.16 + 0.32) = 0.16 / 0.48 = 0.333
        val result = engine.calculateBayesianUpdate(
            priorH = 0.20,
            truePositive = 0.80,
            falsePositive = 0.40
        )

        assertEquals(0.333, result.posteriorProbability, 0.005)
        assertEquals(0.20, result.priorProbability, 0.001)
    }

    @Test
    fun testSpacedRepetitionSM2_successfulReview() {
        val engine = SpacedRepetitionEngine()
        // Primera repetición exitosa (rating 4)
        val (rep1, int1, ef1) = engine.calculateNextReview(0, 1, 2.5f, 4)
        assertEquals(1, rep1)
        assertEquals(1, int1)

        // Segunda repetición exitosa (rating 4)
        val (rep2, int2, ef2) = engine.calculateNextReview(rep1, int1, ef1, 4)
        assertEquals(2, rep2)
        assertEquals(6, int2)
    }

    @Test
    fun testSpacedRepetitionSM2_failedReviewReset() {
        val engine = SpacedRepetitionEngine()
        // Fallo (rating 1)
        val (rep, int, ef) = engine.calculateNextReview(3, 15, 2.5f, 1)
        assertEquals(0, rep)
        assertEquals(1, int)
        assertTrue(ef >= 1.3f)
    }

    @Test
    fun testReciprocityCalculator_asymmetricVsBalanced() {
        val calculator = ReciprocityCalculator()

        val lowResult = calculator.assessReciprocity(1, 1, 2, 2)
        assertEquals(ReciprocityLevel.BAJA, lowResult.balanceLevel)

        val highResult = calculator.assessReciprocity(5, 4, 5, 4)
        assertEquals(ReciprocityLevel.ALTA, highResult.balanceLevel)
    }

    @Test
    fun testCalibrationBrierScore_calculation() {
        val engine = CalibrationEngine()
        // 2 predicciones: (0.8, true) -> (0.8-1)^2 = 0.04
        //                 (0.2, false) -> (0.2-0)^2 = 0.04
        // Media = 0.04
        val predictions = listOf(
            Pair(0.8, true),
            Pair(0.2, false)
        )
        val brier = engine.calculateBrierScore(predictions)
        assertEquals(0.04, brier, 0.001)

        val stats = engine.getCalibrationStats(predictions)
        assertEquals("Excelente Calibración Racional", stats.tendencyLabel)
    }

    @Test
    fun testCognitiveAnalyzer_factsVsInterpretations() {
        val engine = CognitiveAnalyzerEngine()
        val text = "Le mandé un mensaje y tardó 6 horas en responder, seguro piensa que soy aburrido y está jugando."
        val analysis = engine.analyzeSituation(text, isRawMode = true)

        assertTrue(analysis.facts.isNotEmpty())
        assertTrue(analysis.userInterpretations.isNotEmpty())
        assertNotNull(analysis.rawModeFeedback)
        assertTrue(analysis.options.isNotEmpty())
        assertTrue(analysis.whatNotToDo.isNotEmpty())
    }

    @Test
    fun testPatternDetection_mindReading() {
        val engine = PatternDetectionEngine()
        val interpretations = listOf(
            "Seguro cree que no soy su tipo",
            "Piensa que soy pesado por escribirle",
            "Estaba ocupada con su trabajo"
        )
        val emotions = listOf("Ansiedad", "Inseguridad", "Calma")

        val patterns = engine.detectPatternsInEntries(interpretations, emotions)
        assertTrue(patterns.isNotEmpty())
        assertTrue(patterns.any { it.contains("Lectura de Mente") })
    }

    @Test
    fun testDarkPsychology_gaslightingAndGuiltDetection() {
        val engine = DarkPsychologyEngine()
        val gaslightingResult = engine.analyzeManipulativeInput("Estás loco, yo nunca dije eso, todo te lo imaginas")
        assertTrue(gaslightingResult.detectedTacticName.contains("Gaslighting"))
        assertTrue(gaslightingResult.immediateSurgicalResponse.isNotBlank())

        val guiltResult = engine.analyzeManipulativeInput("Si me quisieras no saldrías con tus amigos, por tu culpa me pongo mal")
        assertTrue(guiltResult.detectedTacticName.contains("Chantaje"))
        assertEquals("CRÍTICO", guiltResult.manipulationLevel)
    }
}
