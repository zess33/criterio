package com.urielhuerta.criterio.ui.screens.predictions

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urielhuerta.criterio.data.local.entities.PredictionEntity
import com.urielhuerta.criterio.data.repository.PredictionRepository
import com.urielhuerta.criterio.domain.model.CalibrationStats
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.components.ProbabilityBar
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RiskRed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PredictionsViewModel(
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    val allPredictions: StateFlow<List<PredictionEntity>> = predictionRepository.getAllPredictions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var calibrationStats by mutableStateOf(CalibrationStats(0.0, 0, 0, 0.0, "Sin resolver"))

    var newScenario by mutableStateOf("")
    var newHypothesis by mutableStateOf("")
    var newEstimatedProb by mutableStateOf(0.5f)

    fun refreshStats() {
        viewModelScope.launch {
            calibrationStats = predictionRepository.getCalibrationStats()
        }
    }

    fun savePrediction(onSaved: () -> Unit) {
        if (newScenario.isBlank() || newHypothesis.isBlank()) return
        viewModelScope.launch {
            predictionRepository.addPrediction(
                scenario = newScenario,
                hypothesis = newHypothesis,
                estimatedProbability = newEstimatedProb.toDouble()
            )
            newScenario = ""
            newHypothesis = ""
            newEstimatedProb = 0.5f
            refreshStats()
            onSaved()
        }
    }

    fun resolvePrediction(id: Long, outcome: Boolean) {
        viewModelScope.launch {
            predictionRepository.resolvePrediction(id, outcome)
            refreshStats()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionsScreen(
    viewModel: PredictionsViewModel,
    onNavigateToNewPrediction: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val predictions by viewModel.allPredictions.collectAsState()
    var showPredictionsTutorial by remember { mutableStateOf(false) }

    LaunchedEffect(predictions) {
        viewModel.refreshStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Predicciones & Brier Score", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showPredictionsTutorial = true }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Tutorial", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onNavigateToNewPrediction) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewPrediction) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Predicción")
            }
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
                    title = "Tutorial: ¿Cómo calibrar tu intuición con Brier Score?",
                    shortDesc = "Aprende a cuantificar probabilidades y evitar la ilusión de certeza.",
                    onOpenFullTutorial = { showPredictionsTutorial = true }
                )
            }
            // Dashboard de Calibración
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("CALIBRACIÓN PROBABILÍSTICA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Brier Score: ${viewModel.calibrationStats.brierScore}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text("Diagnóstico: ${viewModel.calibrationStats.tendencyLabel}", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${viewModel.calibrationStats.resolvedPredictions} Resueltas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("${viewModel.calibrationStats.accuracyPercentage}% Acierto", style = MaterialTheme.typography.bodySmall, color = EvidenceHigh)
                            }
                        }
                    }
                }
            }

            if (predictions.isEmpty()) {
                item {
                    CriterioCard {
                        Text("¿Por qué registrar predicciones?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Antes de tomar una decisión social, asigna un porcentaje de probabilidad a lo que crees que ocurrirá. Al comparar con la realidad, aprenderás a no sobreestimar (arrogancia) ni infraestimar (paranoia).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToNewPrediction, modifier = Modifier.fillMaxWidth()) {
                            Text("Registrar Primera Predicción")
                        }
                    }
                }
            } else {
                items(predictions) { pred ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(pred.scenario, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("Hipótesis: ${pred.hypothesis}", style = MaterialTheme.typography.bodyMedium)
                            ProbabilityBar(label = "Probabilidad Asignada", probability = pred.estimatedProbability)

                            if (pred.actualOutcome == null) {
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                Text("¿Se cumplió la hipótesis en la realidad?", style = MaterialTheme.typography.labelSmall)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.resolvePrediction(pred.id, true) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Sí Ocurrió")
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.resolvePrediction(pred.id, false) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("No Ocurrió")
                                    }
                                }
                            } else {
                                Text(
                                    text = if (pred.actualOutcome == true) "✓ Resultado Real: SÍ ocurrió" else "✗ Resultado Real: NO ocurrió",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (pred.actualOutcome == true) EvidenceHigh else RiskRed
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showPredictionsTutorial) {
            com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
                title = "Tutorial: Predicciones & Brier Score",
                objective = "Aprende a pensar en probabilidades y descubre si sufres de exceso de confianza o de paranoia infundada.",
                steps = listOf(
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 1,
                        title = "Formula una predicción medible",
                        description = "Ejemplo: 'La invitaré a salir este sábado y aceptará la propuesta'."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 2,
                        title = "Asigna un porcentaje de probabilidad honesto",
                        description = "No uses 0% ni 100% a la ligera. Considera la tasa base y la reciprocidad previa (ej. 40%, 65%)."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 3,
                        title = "Registra el resultado real cuando ocurra",
                        description = "Marca si ocurrió o no. El algoritmo calculará tu Brier Score (0.0 = perfección predictiva, 1.0 = descalibración total)."
                    )
                ),
                practicalExample = "Predicción: 'Me responderá antes de medianoche (70%)'. Si ocurre, tu Brier Score mejora. Si no ocurre, aprendes a reajustar tu calibración sin culparte.",
                commonMistakes = listOf(
                    "No trates de 'adivinar el futuro'; el objetivo es cuantificar tu incertidumbre con madurez.",
                    "Un error del 50% en una probabilidad de 50% no es un fracaso: es exactamente lo esperado."
                ),
                onDismiss = { showPredictionsTutorial = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPredictionScreen(
    viewModel: PredictionsViewModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Predicción Probabilística", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = viewModel.newScenario,
                onValueChange = { viewModel.newScenario = it },
                label = { Text("Situación Social") },
                placeholder = { Text("Ej: Le propuse una salida el sábado") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = viewModel.newHypothesis,
                onValueChange = { viewModel.newHypothesis = it },
                label = { Text("Hipótesis a Predecir") },
                placeholder = { Text("Ej: Responderá proponiendo una hora exacta") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )

            CriterioCard {
                val pct = (viewModel.newEstimatedProb * 100).toInt()
                Text("Grado de Certeza Asignado: $pct%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Slider(
                    value = viewModel.newEstimatedProb,
                    onValueChange = { viewModel.newEstimatedProb = it },
                    valueRange = 0.05f..0.95f
                )
                Text(
                    text = when {
                        pct >= 80 -> "Muy Alta Certeza (Requiere fuerte evidencia previa)"
                        pct in 40..70 -> "Incertidumbre Moderada (Mente abierta)"
                        else -> "Baja Probabilidad Estimada"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.savePrediction(onNavigateBack) },
                enabled = viewModel.newScenario.isNotBlank() && viewModel.newHypothesis.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrar Predicción")
            }
        }
    }
}
