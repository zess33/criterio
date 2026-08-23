package com.urielhuerta.criterio.ui.screens.testme

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
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RiskRed

data class TestQuestionItem(
    val id: String,
    val scenario: String,
    val options: List<String>,
    val correctIndex: Int,
    val cognitivePrinciple: String,
    val explanation: String
)

class TestMeViewModel : ViewModel() {
    val questions = listOf(
        TestQuestionItem(
            id = "tq_1",
            scenario = "Una persona con la que sales hace 3 semanas no contesta tu mensaje durante 7 horas un día laboral. ¿Cuál es la interpretación racional?",
            options = listOf(
                "Está ocupada con su trabajo o vida personal; no hay evidencia suficiente para asumir desinterés ni juego de manipulación.",
                "Definitivamente está jugando a 'hacerse la difícil' y debo ignorarla 3 días.",
                "Está saliendo con otra persona y ya no le importas en absoluto."
            ),
            correctIndex = 0,
            cognitivePrinciple = "Diferenciación de Hechos vs Interpretaciones",
            explanation = "Un retraso temporal aislado carece de significancia estadística sin un patrón sostenido."
        ),
        TestQuestionItem(
            id = "tq_2",
            scenario = "Le propones una cita para el sábado y te responde: 'El sábado no puedo porque tengo un compromiso familiar'. No ofrece otra fecha. ¿Qué haces?",
            options = listOf(
                "Aceptas con cortesía y permites que la iniciativa de proponer la siguiente fecha recaiga en ella.",
                "Le insistes inmediatamente preguntando por el domingo, lunes y martes.",
                "Le reclamas que siempre pone excusas."
            ),
            correctIndex = 0,
            cognitivePrinciple = "Evaluación de Reciprocidad y Respeto de Autonomía",
            explanation = "La respuesta calibrada ante una negativa sin alternativa es no presionar y observar iniciativa."
        ),
        TestQuestionItem(
            id = "tq_3",
            scenario = "Un video de TikTok afirma: 'Si una mujer te mira a los ojos por más de 3 segundos, tiene 90% de interés sexual'. ¿Qué falla lógica hay aquí?",
            options = listOf(
                "Falacia de estadística inventada, simplificación absurda y sesgo de confirmación.",
                "Ninguna, los videos virales son estudios certificados.",
                "La estadística es válida si sonríe al mismo tiempo."
            ),
            correctIndex = 0,
            cognitivePrinciple = "Pensamiento Crítico y Detección de Pseudociencia",
            explanation = "No existen estadísticas universales ni conductas aisladas que demuestren atracción inequívoca."
        )
    )

    var currentQuestionIndex by mutableStateOf(0)
    var selectedOptionIndex by mutableStateOf<Int?>(null)
    var isAnswerSubmitted by mutableStateOf(false)
    var correctCount by mutableStateOf(0)

    fun submitAnswer() {
        val q = questions[currentQuestionIndex]
        if (selectedOptionIndex == q.correctIndex) {
            correctCount++
        }
        isAnswerSubmitted = true
    }

    fun nextQuestion() {
        if (currentQuestionIndex + 1 < questions.size) {
            currentQuestionIndex++
            selectedOptionIndex = null
            isAnswerSubmitted = false
        }
    }

    fun restart() {
        currentQuestionIndex = 0
        selectedOptionIndex = null
        isAnswerSubmitted = false
        correctCount = 0
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMeScreen(
    viewModel: TestMeViewModel,
    onNavigateBack: () -> Unit
) {
    val qIndex = viewModel.currentQuestionIndex
    val total = viewModel.questions.size
    val currentQ = viewModel.questions[qIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ponme a Prueba", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pregunta ${qIndex + 1} de $total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Aciertos: ${viewModel.correctCount}", style = MaterialTheme.typography.labelLarge, color = EvidenceHigh)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = (qIndex + 1).toFloat() / total,
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                )
            }

            item {
                CriterioCard {
                    Text("Principio Evaluado: ${currentQ.cognitivePrinciple}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentQ.scenario, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentQ.options.forEachIndexed { idx, optionText ->
                        val isSelected = viewModel.selectedOptionIndex == idx
                        val isCorrect = idx == currentQ.correctIndex
                        val cardBg = when {
                            !viewModel.isAnswerSubmitted -> if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            isCorrect -> EvidenceHigh.copy(alpha = 0.2f)
                            isSelected -> RiskRed.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !viewModel.isAnswerSubmitted) {
                                    viewModel.selectedOptionIndex = idx
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Text(text = optionText, modifier = Modifier.padding(14.dp), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            if (!viewModel.isAnswerSubmitted) {
                item {
                    Button(
                        onClick = { viewModel.submitAnswer() },
                        enabled = viewModel.selectedOptionIndex != null,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Confirmar Respuesta")
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (viewModel.selectedOptionIndex == currentQ.correctIndex) EvidenceHigh.copy(alpha = 0.12f) else RiskRed.copy(alpha = 0.12f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = if (viewModel.selectedOptionIndex == currentQ.correctIndex) "✓ ¡Respuesta Calibrada y Correcta!" else "✗ Respuesta con Sesgo o Falacia",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (viewModel.selectedOptionIndex == currentQ.correctIndex) EvidenceHigh else RiskRed
                            )
                            Text(text = currentQ.explanation, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    if (qIndex + 1 < total) {
                        Button(
                            onClick = { viewModel.nextQuestion() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Siguiente Situación")
                        }
                    } else {
                        Button(
                            onClick = { viewModel.restart() },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Reiniciar Examen")
                        }
                    }
                }
            }
        }
    }
}
