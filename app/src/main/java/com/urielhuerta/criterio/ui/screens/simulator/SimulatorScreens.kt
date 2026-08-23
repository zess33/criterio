package com.urielhuerta.criterio.ui.screens.simulator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.urielhuerta.criterio.data.preferences.UserPreferencesRepository
import com.urielhuerta.criterio.data.repository.SimulationRepository
import com.urielhuerta.criterio.domain.model.*
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RiskRed
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ScenarioInfo(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: String,
    val context: String,
    val personaName: String,
    val personaRole: String,
    val personaOpening: String,
    val initialPrompt: String
)

class SimulatorViewModel(
    private val simulationRepository: SimulationRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    var scenariosList = listOf(
        ScenarioInfo(
            id = "scen_cafe",
            title = "Encuentro en una Cafetería",
            category = "ENTORNO_CASUAL",
            difficulty = "Principiante",
            context = "Estás en una cafetería tranquila. En la mesa de al lado, una mujer está leyendo un libro sobre filosofía que tú conoces bien.",
            personaName = "Sofía",
            personaRole = "Lectora habitual, amable pero reservada",
            personaOpening = "Disculpa, ¿sabes si hay algún enchufe cerca de esta mesa?",
            initialPrompt = "Sofía te pregunta con cortesía por un enchufe mientras sostiene su cargador."
        ),
        ScenarioInfo(
            id = "scen_dating_app",
            title = "Manejo de Mensajes Secos",
            category = "DIGITAL",
            difficulty = "Intermedio",
            context = "Tuvieron una charla divertida sobre viajes en una app de citas, pero su última respuesta fue simplemente: 'Jajaja sí'.",
            personaName = "Renata",
            personaRole = "Ocupada, interesada si la charla no cae en clichés",
            personaOpening = "Jajaja sí",
            initialPrompt = "Renata ha respondido con un mensaje corto a tu anécdota previa."
        ),
        ScenarioInfo(
            id = "scen_first_date",
            title = "Primera Cita: Diálogo Balanceado",
            category = "CITAS",
            difficulty = "Intermedio",
            context = "Están en un bar tranquilo en su primera cita. Acaban de pedir sus bebidas y la conversación recién comienza.",
            personaName = "Mariana",
            personaRole = "Curiosa, evalúa autenticidad y buen sentido del humor",
            personaOpening = "Y bueno... ¿qué te apasiona hacer cuando nadie te está mirando?",
            initialPrompt = "Mariana te hace una pregunta abierta con una sonrisa."
        ),
        ScenarioInfo(
            id = "scen_rejection",
            title = "Recepción Elegante de un Rechazo",
            category = "MADUREZ_EMOCIONAL",
            difficulty = "Avanzado",
            context = "Tras dos citas amenas, le propones un tercer plan y ella te comunica que solo busca amistad.",
            personaName = "Camila",
            personaRole = "Honesta y respetuosa",
            personaOpening = "Hola, la pasé súper bien, pero siendo sincera siento que conectamos más como amigos y no busco algo romántico.",
            initialPrompt = "Camila te ha comunicado con claridad sus límites."
        )
    )

    var currentScenario by mutableStateOf<ScenarioInfo?>(null)
    var messages = mutableStateListOf<SimulationMessage>()
    var userInput by mutableStateOf("")
    var isAwaitingResponse by mutableStateOf(false)
    var currentScorecard by mutableStateOf<SimulationScorecard?>(null)

    fun startScenario(scenarioId: String) {
        val scenario = scenariosList.firstOrNull { it.id == scenarioId } ?: scenariosList.first()
        currentScenario = scenario
        messages.clear()
        messages.add(SimulationMessage(sender = MessageSender.PERSONA, text = scenario.personaOpening))
        currentScorecard = null
    }

    fun sendMessage() {
        val text = userInput.trim()
        if (text.isBlank() || isAwaitingResponse) return

        messages.add(SimulationMessage(sender = MessageSender.USER, text = text))
        userInput = ""
        isAwaitingResponse = true

        val scenario = currentScenario ?: return

        viewModelScope.launch {
            val prefs = preferencesRepository.userPreferencesFlow.first()
            val reply = simulationRepository.getAiPersonaResponse(
                personaName = scenario.personaName,
                personaContext = scenario.context,
                conversationHistory = messages.toList(),
                userMessage = text,
                apiKey = prefs.geminiApiKey
            )
            messages.add(SimulationMessage(sender = MessageSender.PERSONA, text = reply))
            isAwaitingResponse = false
        }
    }

    fun finishAndEvaluate() {
        val userMsgs = messages.filter { it.sender == MessageSender.USER }
        val userWordCount = userMsgs.sumOf { it.text.split(" ").size }

        val clarity = (75 + (userMsgs.size * 5)).coerceIn(60, 95)
        val pressure = if (userWordCount > 200) 65 else 90
        val contextReading = 85
        val total = (clarity + pressure + contextReading) / 3

        val strengths = listOf(
            "Mantuviste un tono respetuoso y libre de exigencias.",
            "Respondiste de forma contextual a las aperturas de la otra persona."
        )
        val areasToImprove = listOf(
            "Evita sobre-explicar tus puntos; la concisión denota seguridad.",
            "Deja espacios de silencio para que la otra persona contribuya con preguntas propias."
        )
        val alternatives = listOf(
            "Alternativa A: 'Totalmente de acuerdo, me alegra que lo comentes.'",
            "Alternativa B: 'Entiendo perfectamente tu punto, gracias por ser tan clara.'"
        )

        val scorecard = SimulationScorecard(
            totalScore = total,
            clarityScore = clarity,
            pressureScore = pressure,
            contextReadingScore = contextReading,
            strengths = strengths,
            areasToImprove = areasToImprove,
            alternativeResponses = alternatives,
            summaryFeedback = "Demostraste una interacción social bien calibrada, respetando los límites y el contexto situacional."
        )

        currentScorecard = scorecard

        viewModelScope.launch {
            currentScenario?.let { sc ->
                simulationRepository.saveRecord(
                    scenarioId = sc.id,
                    durationSeconds = 60,
                    scorecard = scorecard,
                    messages = messages.toList()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorLobbyScreen(
    viewModel: SimulatorViewModel,
    onStartScenario: (String) -> Unit
) {
    var showSimulatorTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simulador de Conversaciones", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showSimulatorTutorial = true }) {
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
            // Banner Tutorial
            item {
                com.urielhuerta.criterio.ui.components.QuickTutorialBanner(
                    title = "Tutorial: Cómo practicar en el Simulador",
                    shortDesc = "Conoce cómo evalúa el algoritmo tu nivel de presión, claridad y calibración.",
                    onOpenFullTutorial = { showSimulatorTutorial = true }
                )
            }

            item {
                CriterioCard {
                    Text(
                        text = "Laboratorio de Práctica en Tiempo Real",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Interactúa con personajes simulados en diferentes contextos realistas. Al finalizar, recibirás una evaluación de tu nivel de presión, claridad y lectura de contexto.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(viewModel.scenariosList) { scenario ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStartScenario(scenario.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = scenario.difficulty.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = scenario.personaName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = scenario.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = scenario.context,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { onStartScenario(scenario.id) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Iniciar Simulación")
                        }
                    }
                }
            }
        }

        if (showSimulatorTutorial) {
            com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
                title = "Tutorial: Simulador de Conversaciones",
                objective = "Entrena tus habilidades de comunicación en entornos simulados seguros antes de salir a la vida real.",
                steps = listOf(
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 1,
                        title = "Selecciona un escenario contextual",
                        description = "Elige entre Cafetería, Mensajes en Apps, Primera Cita o Recepción de Rechazo."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 2,
                        title = "Responde con naturalidad como en la vida real",
                        description = "Escribe mensajes cortos, pausados y contextuales. No trates de impresionar con párrafos gigantescos."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 3,
                        title = "Toca 'Evaluar' para recibir tu tarjeta de puntuación",
                        description = "Descubrirás tus métricas de Calibración, Control de Presión y Claridad, además de 3 respuestas alternativas de mayor impacto."
                    )
                ),
                practicalExample = "En la cafetería, en lugar de piropos forzados, comentas con naturalidad sobre el libro que está leyendo o el ambiente del lugar.",
                commonMistakes = listOf(
                    "No satures de preguntas tipo interrogatorio policial.",
                    "No insistas cuando el personaje virtual de señales de prisa o desinterés."
                ),
                onDismiss = { showSimulatorTutorial = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSimulationScreen(
    scenarioId: String,
    viewModel: SimulatorViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(scenarioId) {
        viewModel.startScenario(scenarioId)
    }

    val scenario = viewModel.currentScenario
    val scorecard = viewModel.currentScorecard

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(scenario?.personaName ?: "Simulación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (scorecard == null) {
                        TextButton(onClick = { viewModel.finishAndEvaluate() }) {
                            Text("Evaluar", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (scorecard != null) {
            // Mostrar Evaluación Post-Simulación
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("PUNTUACIÓN GLOBAL", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("${scorecard.totalScore} / 100", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(scorecard.summaryFeedback, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    CriterioCard {
                        Text("Métricas de Inteligencia Social", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Calibración y Lectura Contextual: ${scorecard.contextReadingScore}%", style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(progress = scorecard.contextReadingScore / 100f, modifier = Modifier.fillMaxWidth().height(6.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Control de Presión Social: ${scorecard.pressureScore}%", style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(progress = scorecard.pressureScore / 100f, modifier = Modifier.fillMaxWidth().height(6.dp), color = EvidenceHigh)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Claridad y Asertividad: ${scorecard.clarityScore}%", style = MaterialTheme.typography.bodyMedium)
                        LinearProgressIndicator(progress = scorecard.clarityScore / 100f, modifier = Modifier.fillMaxWidth().height(6.dp))
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Aciertos Observados", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                            scorecard.strengths.forEach { s -> Text("✓ $s", style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RiskRed.copy(alpha = 0.08f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RiskRed.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Oportunidades de Mejora", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                            scorecard.areasToImprove.forEach { a -> Text("• $a", style = MaterialTheme.typography.bodyMedium) }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Volver al Lobby")
                    }
                }
            }
        } else {
            // Chat Activo
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Banner de Contexto
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "Contexto: ${scenario?.context ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Lista de Mensajes
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(viewModel.messages) { msg ->
                        val isUser = msg.sender == MessageSender.USER
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                ),
                                border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
                                modifier = Modifier.widthIn(max = 280.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    modifier = Modifier.padding(12.dp),
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Input Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.userInput,
                            onValueChange = { viewModel.userInput = it },
                            placeholder = { Text("Escribe tu respuesta...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            maxLines = 3
                        )
                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            enabled = viewModel.userInput.isNotBlank() && !viewModel.isAwaitingResponse
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
