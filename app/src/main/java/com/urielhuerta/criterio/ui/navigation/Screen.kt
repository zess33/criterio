package com.urielhuerta.criterio.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding", "Bienvenida")
    
    // Bottom Bar Destinos Principales
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Academy : Screen("academy", "Academia", Icons.Default.School)
    object Analyzer : Screen("analyzer", "Analizador", Icons.Default.Psychology)
    object Simulator : Screen("simulator", "Simulador", Icons.Default.ChatBubble)
    object Journal : Screen("journal", "Diario", Icons.Default.MenuBook)

    // Pantallas Secundarias / Detalle
    object LessonDetail : Screen("lesson_detail/{lessonId}", "Lección") {
        fun createRoute(lessonId: String) = "lesson_detail/$lessonId"
    }
    object ChatSimulation : Screen("chat_simulation/{scenarioId}", "Simulación en Vivo") {
        fun createRoute(scenarioId: String) = "chat_simulation/$scenarioId"
    }
    object TestMe : Screen("test_me", "Ponme a Prueba", Icons.Default.Quiz)
    object InternetAdvice : Screen("internet_advice", "Consejos de Internet", Icons.Default.FactCheck)
    object Predictions : Screen("predictions", "Predicciones & Bayes", Icons.Default.Analytics)
    object NewJournalEntry : Screen("new_journal_entry", "Nueva Entrada")
    object NewPrediction : Screen("new_prediction", "Nueva Predicción")
    object BayesLab : Screen("bayes_lab", "Laboratorio Bayesiano", Icons.Default.Calculate)
    object ReciprocityTool : Screen("reciprocity_tool", "Indicador de Reciprocidad", Icons.Default.Balance)
    object VoiceCoach : Screen("voice_coach", "Entrenador de Comunicación", Icons.Default.Forum)
    object DarkPsychology : Screen("dark_psychology", "Psicología Oscura", Icons.Default.Security)
    object Stats : Screen("stats", "Progreso & Logros", Icons.Default.EmojiEvents)
    object Settings : Screen("settings", "Configuración", Icons.Default.Settings)
}
