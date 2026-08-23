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

// 3. ENTRENADOR DE VOZ
class VoiceCoachViewModel(
    private val voiceEngine: VoiceCoachEngine = VoiceCoachEngine()
) : ViewModel() {
    var isRecording by mutableStateOf(false)
    var analysisResult by mutableStateOf<VoiceCoachEngine.VoiceAnalysisResult?>(null)

    fun toggleRecording() {
        if (isRecording) {
            isRecording = false
            analysisResult = voiceEngine.evaluateSpeechSample(
                durationSeconds = 25,
                estimatedWords = 55,
                detectedFillerWords = listOf("este", "o sea")
            )
        } else {
            isRecording = true
            analysisResult = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceCoachScreen(
    viewModel: VoiceCoachViewModel = remember { VoiceCoachViewModel() },
    onNavigateBack: () -> Unit
) {
    var showVoiceTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Entrenador de Cadencia & Voz", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QuickTutorialBanner(
                title = "Tutorial: Cómo calibrar tu voz y presencia",
                shortDesc = "Aprende sobre ritmo (PPM), silencios cómodos y reducción de muletillas.",
                onOpenFullTutorial = { showVoiceTutorial = true }
            )

            CriterioCard {
                Text("Entrenamiento de Comunicación Verbal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Graba una práctica de cómo te presentarías o contarías una anécdota. Analizaremos tu velocidad (palabras por minuto), pausas y muletillas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FilledIconButton(
                onClick = { viewModel.toggleRecording() },
                modifier = Modifier.size(90.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (viewModel.isRecording) RiskRed else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (viewModel.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Grabar",
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = if (viewModel.isRecording) "Grabando muestra de voz... Toca para detener" else "Toca para iniciar práctica de voz",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            viewModel.analysisResult?.let { res ->
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("EVALUACIÓN DE COMUNICACIÓN", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("${res.wordsPerMinute} PPM — ${res.pauseQuality}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(res.feedbackSummary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        if (showVoiceTutorial) {
            ScreenTutorialDialog(
                title = "Tutorial: Entrenador de Voz & Cadencia",
                objective = "Aprende a transmitir seguridad, calma y claridad a través de tu ritmo vocal.",
                steps = listOf(
                    TutorialStep(
                        stepNumber = 1,
                        title = "Habla con ritmo controlado (120 - 150 PPM)",
                        description = "Hablar demasiado rápido transmite nerviosismo o urgencia. Hablar con calma proyecta comodidad y confianza."
                    ),
                    TutorialStep(
                        stepNumber = 2,
                        title = "Tolera los silencios y haz pausas",
                        description = "No llenes cada segundo con muletillas ('ehhh', 'este', 'o sea'). Una pausa de 2 segundos denota seguridad."
                    ),
                    TutorialStep(
                        stepNumber = 3,
                        title = "Graba una anécdota de 20 segundos",
                        description = "Presiona el micrófono, cuenta brevemente qué hiciste el fin de semana y revisa tu diagnóstico."
                    )
                ),
                practicalExample = "En una cita, en lugar de apresurarte a responder antes de que termine, haz una pausa de 1 segundo, sonríe y responde pausadamente.",
                commonMistakes = listOf(
                    "No fuerces una voz grave artificial; la autenticidad y la respiración diafragmática son lo natural.",
                    "No le temas a los silencios compartidos."
                ),
                onDismiss = { showVoiceTutorial = false }
            )
        }
    }
}
