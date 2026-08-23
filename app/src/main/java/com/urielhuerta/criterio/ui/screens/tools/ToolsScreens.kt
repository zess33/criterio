package com.urielhuerta.criterio.ui.screens.tools

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.urielhuerta.criterio.domain.engine.BayesianProbabilityEngine
import com.urielhuerta.criterio.domain.engine.ReciprocityCalculator
import com.urielhuerta.criterio.domain.engine.VoiceCoachEngine
import com.urielhuerta.criterio.domain.model.BayesianCalculation
import com.urielhuerta.criterio.domain.model.ReciprocityAssessment
import com.urielhuerta.criterio.ui.components.*
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RawModeAmber
import com.urielhuerta.criterio.ui.theme.RiskRed

// 1. LABORATORIO BAYESIANO
class BayesViewModel(
    private val bayesEngine: BayesianProbabilityEngine = BayesianProbabilityEngine()
) : ViewModel() {
    var priorH by mutableStateOf(0.20f)
    var truePositive by mutableStateOf(0.80f)
    var falsePositive by mutableStateOf(0.40f)

    val calculation: BayesianCalculation
        get() = bayesEngine.calculateBayesianUpdate(
            priorH = priorH.toDouble(),
            truePositive = truePositive.toDouble(),
            falsePositive = falsePositive.toDouble()
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BayesLabScreen(
    viewModel: BayesViewModel = remember { BayesViewModel() },
    onNavigateBack: () -> Unit
) {
    val calc = viewModel.calculation
    var showBayesTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laboratorio Bayesiano", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showBayesTutorial = true }) {
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
                QuickTutorialBanner(
                    title = "Tutorial: ¿Cómo aplicar Bayes a tus citas?",
                    shortDesc = "Descubre por qué una señal aislada no prueba nada si la tasa base es baja.",
                    onOpenFullTutorial = { showBayesTutorial = true }
                )
            }

            item {
                CriterioCard {
                    Text("Teorema de Bayes en Citas y Relaciones", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Actualiza tus expectativas racionales: una señal aislada (ej. una sonrisa o amabilidad) no demuestra atracción automática si la probabilidad base inicial es baja.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("PROBABILIDAD POSTERIOR ACTUALIZADA: P(H|E)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        val postPct = (calc.posteriorProbability * 100).toInt()
                        Text("$postPct%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(calc.qualitativeExplanation, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                CriterioCard {
                    Text("1. Probabilidad Previa de Interés Base P(H): ${(viewModel.priorH * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = viewModel.priorH,
                        onValueChange = { viewModel.priorH = it },
                        valueRange = 0.05f..0.95f
                    )
                    Text("2. Si realmente le gustas, ¿con qué probabilidad verías esta conducta? P(E|H): ${(viewModel.truePositive * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = viewModel.truePositive,
                        onValueChange = { viewModel.truePositive = it },
                        valueRange = 0.05f..0.95f
                    )
                    Text("3. Si sólo es amable y NO hay interés, ¿con qué probabilidad verías esta conducta? P(E|~H): ${(viewModel.falsePositive * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(
                        value = viewModel.falsePositive,
                        onValueChange = { viewModel.falsePositive = it },
                        valueRange = 0.05f..0.95f
                    )
                }
            }

            item {
                CriterioCard {
                    Text("Regla de Oro contra la Paranoia", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RawModeAmber)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Si no tienes datos empíricos suficientes, la respuesta racional es: «NO HAY DATOS SUFICIENTES PARA CALCULAR UNA PROBABILIDAD NUMÉRICA EXACTA. DECLARO INCERTIDUMBRE Y CONTINÚO CON MI VIDA».",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (showBayesTutorial) {
            ScreenTutorialDialog(
                title = "Tutorial: Laboratorio Bayesiano",
                objective = "Entiende cómo el cerebro humano sobreinterpreta señales aisladas y cómo corregirlo matemáticamente.",
                steps = listOf(
                    TutorialStep(
                        stepNumber = 1,
                        title = "Establece la Probabilidad Base P(H)",
                        description = "¿Qué tan probable es que una persona aleatoria o con quien apenas hablaste 2 minutos sienta atracción? (Usualmente 10%-20%)."
                    ),
                    TutorialStep(
                        stepNumber = 2,
                        title = "Evalúa la Tasa de Falsos Positivos P(E|~H)",
                        description = "Una sonrisa amable o responder 'hola' ocurre muy seguido incluso sin atracción romántica (alta tasa de falsos positivos)."
                    ),
                    TutorialStep(
                        stepNumber = 3,
                        title = "Mira el resultado actualizado",
                        description = "Observa que una señal común solo sube la probabilidad de 20% a 33%, no al 90%. Para certezas reales se requieren patrones consistentes en el tiempo."
                    )
                ),
                practicalExample = "Señal: 'Se rio de mi chiste'. ¿Significa amor eterno? Bayes demuestra que la amabilidad social común solo aporta evidencia débil. Disfruta el momento sin conclusiones prematuras.",
                commonMistakes = listOf(
                    "No creas que una sola mirada o 'me gusta' define interés romántico.",
                    "No caigas en la falacia de tasa base olvidando el contexto."
                ),
                onDismiss = { showBayesTutorial = false }
            )
        }
    }
}

// 2. INDICADOR DE RECIPROCIDAD
class ReciprocityViewModel(
    private val calculator: ReciprocityCalculator = ReciprocityCalculator()
) : ViewModel() {
    var initiative by mutableStateOf(3f)
    var effort by mutableStateOf(3f)
    var consistency by mutableStateOf(3f)
    var communication by mutableStateOf(3f)

    val assessment: ReciprocityAssessment
        get() = calculator.assessReciprocity(
            initiative = initiative.toInt(),
            effort = effort.toInt(),
            consistency = consistency.toInt(),
            communication = communication.toInt()
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReciprocityScreen(
    viewModel: ReciprocityViewModel = remember { ReciprocityViewModel() },
    onNavigateBack: () -> Unit
) {
    val assessment = viewModel.assessment
    var showReciprocityTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indicador de Reciprocidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showReciprocityTutorial = true }) {
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
                QuickTutorialBanner(
                    title = "Tutorial: Cómo evaluar el balance relacional",
                    shortDesc = "Mide los 4 pilares observables de reciprocidad sin caer en cálculos rígidos.",
                    onOpenFullTutorial = { showReciprocityTutorial = true }
                )
            }

            item {
                CriterioCard {
                    Text("Evaluación de Equilibrio Relacional", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "La reciprocidad no es una fórmula rígida 50/50 al centavo, sino el equilibrio observable en iniciativa, tiempo, consistencia y esfuerzo mutuo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ReciprocityGauge(level = assessment.balanceLevel)
                        Text(assessment.explanation, style = MaterialTheme.typography.bodyMedium)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        assessment.observations.forEach { obs ->
                            Text("• $obs", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item {
                CriterioCard {
                    Text("Iniciativa en Contactos & Planes: ${viewModel.initiative.toInt()}/5", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.initiative, onValueChange = { viewModel.initiative = it }, valueRange = 1f..5f, steps = 3)

                    Text("Esfuerzo Logístico y Desplazamiento: ${viewModel.effort.toInt()}/5", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.effort, onValueChange = { viewModel.effort = it }, valueRange = 1f..5f, steps = 3)

                    Text("Consistencia (Palabras vs Hechos): ${viewModel.consistency.toInt()}/5", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.consistency, onValueChange = { viewModel.consistency = it }, valueRange = 1f..5f, steps = 3)

                    Text("Calidad y Escucha en la Comunicación: ${viewModel.communication.toInt()}/5", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Slider(value = viewModel.communication, onValueChange = { viewModel.communication = it }, valueRange = 1f..5f, steps = 3)
                }
            }
        }

        if (showReciprocityTutorial) {
            ScreenTutorialDialog(
                title = "Tutorial: Indicador de Reciprocidad",
                objective = "Aprende a medir si una relación tiene bases sólidas de mutuo interés o si estás sobreinvirtiendo unilateralmente.",
                steps = listOf(
                    TutorialStep(
                        stepNumber = 1,
                        title = "Iniciativa (¿Quién propone?)",
                        description = "Evalúa si ambas partes inician conversaciones y proponen salidas, o si tú siempre debes empujar la interacción."
                    ),
                    TutorialStep(
                        stepNumber = 2,
                        title = "Esfuerzo y Desplazamiento",
                        description = "¿Ambos hacen espacio en sus horarios y se desplazan distancias similares para verse?"
                    ),
                    TutorialStep(
                        stepNumber = 3,
                        title = "Consistencia y Comunicación",
                        description = "¿Lo que dice concuerda con lo que hace? ¿Hay interés activo por conocerte o sólo respuestas pasivas?"
                    )
                ),
                practicalExample = "Si tienes Iniciativa 1/5 y Esfuerzo 1/5, el indicador marcará Reciprocidad Baja: la recomendación es pausar la iniciativa y observar si la otra persona la retoma.",
                commonMistakes = listOf(
                    "No lleves una libreta contable en una cita; evalúa la vibra y esfuerzo general.",
                    "No compenses la falta de interés del otro esforzándote el triple."
                ),
                onDismiss = { showReciprocityTutorial = false }
            )
        }
    }
}

// 3. ENTRENADOR DE COMUNICACIÓN & RESPUESTAS ASERTIVAS (100% TEXTO / SIN MICRÓFONO)
data class CommunicationPrompt(
    val id: String,
    val category: String,
    val situationTitle: String,
    val promptQuestion: String,
    val goalHint: String,
    val calibratedExample: String
)

class VoiceCoachViewModel : ViewModel() {

    val prompts = listOf(
        CommunicationPrompt(
            id = "cp_1",
            category = "Presentación & Pasión",
            situationTitle = "¿A qué te dedicas?",
            promptQuestion = "Te preguntan en una cita a qué te dedicas. Explica tu trabajo enfocándote en lo que te divierte, desafía o apasiona sin sonar como un currículum aburrido.",
            goalHint = "Meta: 15-40 palabras, tono relajado, sin sonar jactancioso ni disculparte por tu profesión.",
            calibratedExample = "Diseño software para empresas. Lo que más disfruto es resolver problemas complejos que le ahorran horas de estrés a la gente. ¿Y tú, qué es lo que más te divierte de lo que haces?"
        ),
        CommunicationPrompt(
            id = "cp_2",
            category = "Storytelling & Anécdotas",
            situationTitle = "Cuéntame algo curioso que te haya pasado",
            promptQuestion = "Te piden una anécdota. Redacta una historia breve con inicio, conflicto ligero y remate con humor sin monopolizar la conversación.",
            goalHint = "Meta: Breve, con gancho emocional, sin detalles irrelevantes.",
            calibratedExample = "El mes pasado intenté cocinar paella por primera vez para unos amigos. Casi quemo la cocina, pero el arroz quedó sorprendentemente bueno. Desde entonces me nombraron chef oficial bajo supervisión."
        ),
        CommunicationPrompt(
            id = "cp_3",
            category = "Límites & Asertividad",
            situationTitle = "Desacuerdo sobre una película o tema",
            promptQuestion = "A la otra persona le encantó una película que a ti te pareció floja. Expresa tu punto de vista con humor y seguridad sin pedir disculpas por opinar distinto.",
            goalHint = "Meta: Tono cálido pero firme; no digas 'perdón' ni 'bueno, es solo mi opinión'.",
            calibratedExample = "A mí la verdad me pareció predecible en el segundo acto, aunque la fotografía estuvo increíble. Me da curiosidad, ¿qué fue lo que más te atrapó de ese final?"
        ),
        CommunicationPrompt(
            id = "cp_4",
            category = "Iniciativa & Liderazgo",
            situationTitle = "Proponer cambiar de lugar",
            promptQuestion = "Llevan 1 hora en una cafetería y quieres invitarla a caminar a un parque o a probar unos helados cercanos. Haz la propuesta con naturalidad.",
            goalHint = "Meta: Propuesta clara y relajada, sin sonar suplicante ni autoritario.",
            calibratedExample = "El café estuvo genial, pero el día está muy agradable afuera. Conozco una heladería a dos cuadras con una terraza increíble, vamos a probarla."
        ),
        CommunicationPrompt(
            id = "cp_5",
            category = "Cumplido Calibrado",
            situationTitle = "Hacer un cumplido genuino",
            promptQuestion = "Quieres elogiar su energía, su sentido del humor o un detalle de su estilo sin caer en adulación superficial.",
            goalHint = "Meta: Breve, específico, sin esperar validación inmediata.",
            calibratedExample = "Me gusta mucho tu sentido del humor, tienes una energía muy espontánea que hace que la charla sea muy fácil."
        ),
        CommunicationPrompt(
            id = "cp_6",
            category = "Cierre Asertivo",
            situationTitle = "Concluir la cita dejando interés claro",
            promptQuestion = "La cita ha terminado y la pasaste muy bien. Despídete comunicando tu interés de volver a verla de forma relajada y clara.",
            goalHint = "Meta: Calidez, claridad de intenciones y sin apresurar compromisos.",
            calibratedExample = "Me la pasé increíble hoy contigo. Te escribo en la semana para coordinar la segunda vuelta."
        )
    )

    var currentPromptIndex by mutableStateOf(0)
    val currentPrompt get() = prompts[currentPromptIndex]

    var textInput by mutableStateOf("")
    var analysisResult by mutableStateOf<CommunicationAnalysisResult?>(null)

    data class CommunicationAnalysisResult(
        val wordCount: Int,
        val concisenessQuality: String,
        val detectedFillerWords: List<String>,
        val feedbackSummary: String,
        val calibratedExample: String
    )

    fun skipToNextPrompt() {
        currentPromptIndex = (currentPromptIndex + 1) % prompts.size
        analysisResult = null
        textInput = ""
    }

    fun evaluateResponse() {
        if (textInput.isBlank()) return
        val words = textInput.trim().split(Regex("\\s+"))
        val fillerList = listOf("este", "o sea", "tipo", "literal", "como que", "eh", "mmm", "pues", "la verdad es que")
        val foundFillers = words.filter { w -> fillerList.any { it.equals(w, ignoreCase = true) } }

        val feedback = buildString {
            if (words.size in 15..55) {
                append("Longitud óptima (${words.size} palabras): Dinámica, concreta y fácil de responder. ")
            } else if (words.size < 15) {
                append("Respuesta muy breve (${words.size} palabras): Podría sonar cortante o desinteresada; añade un detalle o pregunta de rebote. ")
            } else {
                append("Respuesta extensa (${words.size} palabras): En una conversación cara a cara podría sonar como monólogo. ")
            }

            if (foundFillers.isNotEmpty()) {
                append("Detectamos posibles muletillas (${foundFillers.joinToString(", ")}). Reemplázalas por silencios naturales. ")
            } else {
                append("Lenguaje limpio sin muletillas detectadas. ")
            }

            val textLower = textInput.lowercase()
            if (textLower.contains("perdón") || textLower.contains("disculpa si") || textLower.contains("no sé si me explico")) {
                append("⚠️ Evita pedir disculpas o dudar de tu propia opinión cuando expreses un gusto o perspectiva.")
            } else {
                append("Tono asertivo y seguro.")
            }
        }

        analysisResult = CommunicationAnalysisResult(
            wordCount = words.size,
            concisenessQuality = if (words.size in 15..55) "Calibración Óptima" else "Calibración Mejorable",
            detectedFillerWords = foundFillers,
            feedbackSummary = feedback,
            calibratedExample = currentPrompt.calibratedExample
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCoachScreen(
    viewModel: VoiceCoachViewModel = remember { VoiceCoachViewModel() },
    onNavigateBack: () -> Unit
) {
    var showVoiceTutorial by remember { mutableStateOf(false) }
    val prompt = viewModel.currentPrompt

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrenador de Comunicación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showVoiceTutorial = true }) {
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
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                QuickTutorialBanner(
                    title = "Tutorial: Calibración y respuestas asertivas",
                    shortDesc = "Aprende a responder con dinamismo, sin rodeos ni disculpas innecesarias.",
                    onOpenFullTutorial = { showVoiceTutorial = true }
                )
            }

            // Tarjeta de la Pregunta / Situación con botón de Omitir / Siguiente
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = prompt.category.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Botón de Omitir / Siguiente Pregunta
                            OutlinedButton(
                                onClick = { viewModel.skipToNextPrompt() },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Text("Omitir / Siguiente", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        Text(
                            text = prompt.situationTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = prompt.promptQuestion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                        Text(
                            text = "💡 ${prompt.goalHint}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Entrada de Respuesta Escrita
            item {
                CriterioCard {
                    Text("Tu Respuesta a la Situación", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Escribe con naturalidad cómo responderías a esta situación en la vida real. Analizaremos tu estructura, concisión y asertividad.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.textInput,
                        onValueChange = { viewModel.textInput = it },
                        placeholder = { Text("Escribe tu respuesta aquí...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.evaluateResponse() },
                        enabled = viewModel.textInput.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Text("Evaluar Calibración de Respuesta", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Resultado del Análisis
            viewModel.analysisResult?.let { res ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("EVALUACIÓN DE RESPUESTA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text("${res.wordCount} Palabras — ${res.concisenessQuality}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(res.feedbackSummary, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(4.dp))

                            Text("EJEMPLO DE RESPUESTA CALIBRADA:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("«${res.calibratedExample}»", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.skipToNextPrompt() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Probar Siguiente Situación ➔")
                            }
                        }
                    }
                }
            }
        }

        if (showVoiceTutorial) {
            ScreenTutorialDialog(
                title = "Tutorial: Entrenador de Comunicación",
                objective = "Entrena tu capacidad para responder con seguridad, concisión y empatía en situaciones sociales reales.",
                steps = listOf(
                    TutorialStep(
                        stepNumber = 1,
                        title = "Lee el contexto y objetivo",
                        description = "Cada situación simula una interacción cotidiana (citas, límites, desacuerdos, presentaciones)."
                    ),
                    TutorialStep(
                        stepNumber = 2,
                        title = "Escribe tu respuesta espontánea",
                        description = "Responde como lo harías en la vida real. Evita sobre-analizar."
                    ),
                    TutorialStep(
                        stepNumber = 3,
                        title = "Revisa tu diagnóstico y compara",
                        description = "Evalúa si caíste en rodeos, disculpas innecesarias o monólogos, y compáralo con el ejemplo calibrado."
                    )
                ),
                practicalExample = "Si te preguntan algo donde opinas distinto, en lugar de pedir perdón o fingir acuerdo, expresas tu gusto con humor y preguntas por la perspectiva del otro.",
                commonMistakes = listOf(
                    "No des explicaciones no solicitadas ni te justifiques de más.",
                    "No memorices respuestas palabra por palabra; interioriza la asertividad y la naturalidad."
                ),
                onDismiss = { showVoiceTutorial = false }
            )
        }
    }
}
