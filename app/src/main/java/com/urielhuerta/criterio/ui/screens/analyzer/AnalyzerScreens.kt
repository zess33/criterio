package com.urielhuerta.criterio.ui.screens.analyzer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.urielhuerta.criterio.data.preferences.UserPreferences
import com.urielhuerta.criterio.data.preferences.UserPreferencesRepository
import com.urielhuerta.criterio.domain.engine.CognitiveAnalyzerEngine
import com.urielhuerta.criterio.domain.model.SituationAnalysis
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.components.RawModeBanner
import com.urielhuerta.criterio.ui.theme.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdviceItem(
    val id: String,
    val claim: String,
    val originSource: String,
    val analysis: AdviceAnalysis
)

data class AdviceAnalysis(
    val meaning: String,
    val availableEvidence: String,
    val potentialBiases: String,
    val whatMayBeTrue: String,
    val whatIsExaggeration: String,
    val whatIsUnfounded: String,
    val conclusion: String
)

class AnalyzerViewModel(
    private val cognitiveEngine: CognitiveAnalyzerEngine,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    var situationInput by mutableStateOf("")
    var currentAnalysis by mutableStateOf<SituationAnalysis?>(null)
    var isAnalyzing by mutableStateOf(false)

    var adviceClaimInput by mutableStateOf("")
    var selectedAdviceItem by mutableStateOf<AdviceItem?>(null)

    fun analyzeSituation() {
        if (situationInput.isBlank()) return
        isAnalyzing = true
        val isRaw = preferences.value.isRawModeEnabled
        viewModelScope.launch {
            currentAnalysis = cognitiveEngine.analyzeSituation(situationInput, isRaw)
            isAnalyzing = false
        }
    }

    fun toggleRawMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setRawModeEnabled(enabled)
            if (currentAnalysis != null && situationInput.isNotBlank()) {
                currentAnalysis = cognitiveEngine.analyzeSituation(situationInput, enabled)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SituationAnalyzerScreen(
    viewModel: AnalyzerViewModel
) {
    val prefs by viewModel.preferences.collectAsState()
    val analysis = viewModel.currentAnalysis
    var showTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analizador de Situaciones", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showTutorial = true }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Tutorial", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Banner Tutorial
            item {
                com.urielhuerta.criterio.ui.components.QuickTutorialBanner(
                    title = "Tutorial: Cómo redactar para un análisis óptimo",
                    shortDesc = "Aprende a separar lo que pasó de lo que tu mente imagina.",
                    onOpenFullTutorial = { showTutorial = true }
                )
            }

            // Banner de Modo Crudo
            item {
                RawModeBanner(
                    isEnabled = prefs.isRawModeEnabled,
                    onToggle = { viewModel.toggleRawMode(it) }
                )
            }

            // Input de la situación
            item {
                CriterioCard {
                    Text(
                        text = "Describe la interacción o situación:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Escribe libremente qué ocurrió (ej: 'Conocí a una chica, hablamos 3 días muy bien, pero hoy me dejó en visto 8 horas y creo que está jugando conmigo').",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.situationInput,
                        onValueChange = { viewModel.situationInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Describe los hechos y tus pensamientos...") },
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.analyzeSituation() },
                        enabled = viewModel.situationInput.isNotBlank() && !viewModel.isAnalyzing,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Analytics, contentDescription = null)
                            Text(if (viewModel.isAnalyzing) "Analizando sesgos..." else "Desglosar Hechos vs Interpretaciones")
                        }
                    }
                }
            }

            // Resultados del Análisis Cognitivo
            if (analysis != null) {
                // Modo Crudo Alerta (si está activo)
                if (analysis.rawModeFeedback != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = RawModeAmber.copy(alpha = 0.15f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RawModeAmber)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = RawModeAmber)
                                    Text("LO QUE PROBABLEMENTE NO QUIERES ESCUCHAR", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RawModeAmber)
                                }
                                Text(analysis.rawModeFeedback, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                // 1. Hechos Observables
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("1. HECHOS OBSERVABLES (Lo verificable)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                            analysis.facts.forEach { f -> Text("• $f", style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }

                // 2. Interpretaciones y Suposiciones
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RiskRed.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RiskRed.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("2. INTERPRETACIONES / LECTURA DE MENTE", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                            analysis.userInterpretations.forEach { ui -> Text("• $ui", style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }

                // 3. Información Faltante
                item {
                    CriterioCard {
                        Text("3. INFORMACIÓN FALTANTE QUE NO CONOCES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        analysis.missingInformation.forEach { mi -> Text("• $mi", style = MaterialTheme.typography.bodySmall) }
                    }
                }

                // 4. Hipótesis Alternativas
                item {
                    CriterioCard {
                        Text("4. EXPLICACIONES ALTERNATIVAS PLAUSIBLES", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        analysis.alternativeHypotheses.forEach { ah -> Text("• $ah", style = MaterialTheme.typography.bodyMedium) }
                    }
                }

                // 5. Nivel de Incertidumbre y Recomendación
                item {
                    CriterioCard {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nivel de Incertidumbre:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(analysis.uncertaintyLevel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("RECOMENDACIÓN RACIONAL", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                        Text(analysis.recommendation, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("QUÉ NO HACER", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = RiskRed)
                        analysis.whatNotToDo.forEach { wntd -> Text("❌ $wntd", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }

        if (showTutorial) {
            com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
                title = "Tutorial: Analizador de Situaciones",
                objective = "El analizador desmonta las trampas mentales y la sobreinterpretación en tus interacciones sociales.",
                steps = listOf(
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 1,
                        title = "Escribe lo que ocurrió sin juzgar",
                        description = "Incluye detalles observables: tiempos de respuesta, mensajes enviados, propuestas de planes o silencios."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 2,
                        title = "Revisa la separación de Hechos vs Interpretaciones",
                        description = "La app te mostrará qué parte es real e indiscutible y qué parte es suposición de tu mente (ej. 'seguro está jugando')."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 3,
                        title = "Aplica las opciones de acción calibradas",
                        description = "Sigue la recomendación racional y revisa la sección 'Qué NO Hacer' para no cometer errores impulsivos."
                    )
                ),
                practicalExample = "Texto: 'La invité a salir el viernes y me dijo que tenía un compromiso pero no propuso otro día'. Análisis: Hecho (rechazo puntual sin alternativa) -> Acción (no insistir y esperar iniciativa de su parte).",
                commonMistakes = listOf(
                    "No asumas que sabes lo que la otra persona piensa sin que te lo haya dicho.",
                    "No envíes dobles mensajes de reclamo por un retraso de horas."
                ),
                onDismiss = { showTutorial = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternetAdviceAnalyzerScreen(
    viewModel: AnalyzerViewModel,
    onNavigateBack: () -> Unit
) {
    var adviceClaim by remember { mutableStateOf("") }
    var currentResult by remember { mutableStateOf<AdviceAnalysis?>(null) }
    var showAdviceTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Desmitificador de Consejos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showAdviceTutorial = true }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Tutorial", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                CriterioCard {
                    Text("Analiza una Afirmación de Redes Sociales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Pega un consejo viral de TikTok, Instagram, foros de seducción o coaches de pareja para deconstruirlo con rigor científico.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adviceClaim,
                        onValueChange = { adviceClaim = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: 'El hombre siempre debe pagar el 100%' o 'Tarda 3 horas en responder para generar interés'") },
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            currentResult = AdviceAnalysis(
                                meaning = "Plantea una regla conductual rígida que asume que el comportamiento interpersonal responde a fórmulas universales de causa-efecto.",
                                availableEvidence = "EVIDENCIA LIMITADA: Los estudios de psicología social demuestran que las personas reaccionan de forma sumamente heterogénea según su apego y valores.",
                                potentialBiases = "Sesgo de confirmación, simplificación excesiva y mentalidad transaccional.",
                                whatMayBeTrue = "La consideración, los límites claros y tener una vida ocupada son saludables y atractivos.",
                                whatIsExaggeration = "Afirmar que existe una regla matemática que funciona con el 100% de las mujeres.",
                                whatIsUnfounded = "La creencia de que fingir desinterés crea relaciones estables y sinceras a largo plazo.",
                                conclusion = "Descarta las fórmulas mágicas de internet. Prioriza la autenticidad, la reciprocidad observable y la comunicación honesta."
                            )
                        },
                        enabled = adviceClaim.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Deconstruir Afirmación")
                    }
                }
            }

            if (currentResult != null) {
                val res = currentResult!!
                item {
                    CriterioCard {
                        Text("1. Significado Subyacente", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(res.meaning, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("2. Evidencia Científica Disponible", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(res.availableEvidence, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("3. Posibles Sesgos Cognitivos", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                        Text(res.potentialBiases, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                item {
                    CriterioCard {
                        Text("4. Qué Parte Puede Ser Cierta", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                        Text(res.whatMayBeTrue, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("5. Qué Parte es Exageración", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RawModeAmber)
                        Text(res.whatIsExaggeration, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text("6. Qué Parte Carece de Fundamento", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                        Text(res.whatIsUnfounded, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("7. Conclusión Racional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(res.conclusion, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (showAdviceTutorial) {
            com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
                title = "Tutorial: Desmitificador de Consejos",
                objective = "Aprende a filtrar el contenido viral de redes sociales separando la evidencia real de los sesgos y el rage bait.",
                steps = listOf(
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 1,
                        title = "Pega cualquier consejo o frase de internet",
                        description = "Ejemplos: 'El hombre siempre debe pagar', 'Tarda el doble en responder', 'Todas las mujeres buscan lo mismo'."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 2,
                        title = "Examina el desglose de 8 puntos",
                        description = "Descubre el nivel de evidencia científica, los sesgos implícitos y qué partes son exageraciones o mitos sin sustento."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 3,
                        title = "Quédate con la conclusión racional",
                        description = "Aplica sólo principios probados basados en autenticidad, reciprocidad y respeto mutuo."
                    )
                ),
                practicalExample = "Consejo viral: 'Si no te responde en 5 minutos no le importas'. Desglose: Sesgo de catastrofismo y lectura de mente. Conclusión: Los tiempos digitales varían por responsabilidades y no definen valor.",
                commonMistakes = listOf(
                    "No confundas millones de 'likes' con rigor científico.",
                    "No adoptes dogmas de internet sin contrastarlos con la realidad."
                ),
                onDismiss = { showAdviceTutorial = false }
            )
        }
    }
}
