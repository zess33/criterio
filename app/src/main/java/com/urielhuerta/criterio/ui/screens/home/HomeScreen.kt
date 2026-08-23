package com.urielhuerta.criterio.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urielhuerta.criterio.data.local.dao.ModuleWithLessons
import com.urielhuerta.criterio.data.local.entities.LessonEntity
import com.urielhuerta.criterio.data.local.entities.SpacedRepetitionCardEntity
import com.urielhuerta.criterio.data.preferences.UserPreferences
import com.urielhuerta.criterio.data.preferences.UserPreferencesRepository
import com.urielhuerta.criterio.data.repository.EducationRepository
import com.urielhuerta.criterio.data.repository.SpacedRepetitionRepository
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.components.EvidenceBadge
import com.urielhuerta.criterio.ui.components.RawModeBanner
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RawModeAmber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val educationRepository: EducationRepository,
    private val spacedRepetitionRepository: SpacedRepetitionRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val appUpdateManager: com.urielhuerta.criterio.data.updater.AppUpdateManager = com.urielhuerta.criterio.data.updater.AppUpdateManager()
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val modulesWithLessons: StateFlow<List<ModuleWithLessons>> = educationRepository.getModulesWithLessons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cardsDueCount: StateFlow<Int> = spacedRepetitionRepository.getCardsDueCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cardsDue: StateFlow<List<SpacedRepetitionCardEntity>> = spacedRepetitionRepository.getCardsDueForReview()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var updateCheckResult by mutableStateOf<com.urielhuerta.criterio.data.updater.UpdateCheckResult?>(null)
    var isDownloadingUpdate by mutableStateOf(false)
    var downloadProgress by mutableStateOf(0f)
    var showUpdateDialog by mutableStateOf(false)

    fun checkUpdatesOnLaunch() {
        viewModelScope.launch {
            try {
                val result = appUpdateManager.checkForUpdates()
                updateCheckResult = result
                if (result.isUpdateAvailable) {
                    showUpdateDialog = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadAndInstallUpdate(context: android.content.Context) {
        val downloadUrl = updateCheckResult?.downloadUrl ?: return
        if (isDownloadingUpdate) return
        isDownloadingUpdate = true
        downloadProgress = 0f

        viewModelScope.launch {
            appUpdateManager.downloadAndInstallApk(
                context = context,
                downloadUrl = downloadUrl,
                onProgress = { p -> downloadProgress = p },
                onSuccess = {
                    isDownloadingUpdate = false
                    showUpdateDialog = false
                },
                onError = {
                    isDownloadingUpdate = false
                }
            )
        }
    }

    fun toggleRawMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setRawModeEnabled(enabled)
        }
    }

    fun reviewCard(card: SpacedRepetitionCardEntity, rating: Int) {
        viewModelScope.launch {
            spacedRepetitionRepository.reviewCard(card, rating)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToAnalyzer: () -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToTestMe: () -> Unit,
    onNavigateToInternetAdvice: () -> Unit,
    onNavigateToBayes: () -> Unit,
    onNavigateToReciprocity: () -> Unit,
    onNavigateToVoiceCoach: () -> Unit,
    onNavigateToDarkPsychology: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val prefs by viewModel.preferences.collectAsState()
    val modules by viewModel.modulesWithLessons.collectAsState()
    val dueCount by viewModel.cardsDueCount.collectAsState()
    val dueCards by viewModel.cardsDue.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkUpdatesOnLaunch()
    }

    var showReviewDialog by remember { mutableStateOf(false) }
    var currentReviewIndex by remember { mutableStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    var showTutorialDialog by remember { mutableStateOf(false) }

    // Encontrar la siguiente lección no completada
    val nextLesson = remember(modules) {
        modules.flatMap { it.lessons }.firstOrNull { !it.isCompleted }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "CRITERIO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            "Plan: ${prefs.primaryGoal}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = { showTutorialDialog = true }) {
                            Icon(Icons.Default.HelpOutline, contentDescription = "Tutorial", tint = MaterialTheme.colorScheme.primary)
                        }
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Racha",
                            tint = RawModeAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${prefs.streakDays}d",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = RawModeAmber
                        )
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                        }
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
            // Banner de Actualización Disponible (si hay una nueva versión)
            if (viewModel.updateCheckResult?.isUpdateAvailable == true) {
                item {
                    val update = viewModel.updateCheckResult!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.showUpdateDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.15f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EvidenceHigh)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = EvidenceHigh, modifier = Modifier.size(28.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "¡Nueva versión disponible (v${update.latestVersion})!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EvidenceHigh
                                )
                                Text(
                                    text = "Toca para ver novedades y actualizar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { viewModel.showUpdateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EvidenceHigh),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Actualizar", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Banner de Inicio Rápido / Tutorial
            item {
                com.urielhuerta.criterio.ui.components.QuickTutorialBanner(
                    title = "Guía de Inicio: ¿Cómo funciona Criterio?",
                    shortDesc = "Descubre en 1 minuto cómo aprovechar las herramientas científicas y el Modo Crudo.",
                    onOpenFullTutorial = { showTutorialDialog = true }
                )
            }

            // Banner de Modo Crudo
            item {
                RawModeBanner(
                    isEnabled = prefs.isRawModeEnabled,
                    onToggle = { viewModel.toggleRawMode(it) }
                )
            }

            // Siguiente Lección Recomendada
            item {
                Text(
                    text = "Continuar Aprendizaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (nextLesson != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToLesson(nextLesson.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SIGUIENTE LECCIÓN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                EvidenceBadge(evidenceLevel = nextLesson.evidenceLevel)
                            }
                            Text(
                                text = nextLesson.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = nextLesson.conceptExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 3
                            )
                            Button(
                                onClick = { onNavigateToLesson(nextLesson.id) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Comenzar Lección")
                            }
                        }
                    }
                } else {
                    CriterioCard {
                        Text(
                            text = "¡Todos los módulos iniciales completados!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EvidenceHigh
                        )
                        Text(
                            text = "Excelente dominio. Continúa reforzando con simulaciones y el laboratorio de pensamiento crítico.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Repaso Espaciado SM-2
            if (dueCount > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentReviewIndex = 0
                                showAnswer = false
                                showReviewDialog = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.Repeat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text(
                                        text = "Repaso Espaciado Activo",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$dueCount conceptos listos para reforzar hoy (SM-2)",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                            FilledTonalButton(onClick = {
                                currentReviewIndex = 0
                                showAnswer = false
                                showReviewDialog = true
                            }) {
                                Text("Repasar")
                            }
                        }
                    }
                }
            }

            // Herramientas y Laboratorios Rápidos
            item {
                Text(
                    text = "Herramientas de Análisis & Práctica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionCard(
                            title = "Analizador",
                            desc = "Hechos vs Interpretaciones",
                            icon = Icons.Default.Psychology,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToAnalyzer
                        )
                        ActionCard(
                            title = "Simulador",
                            desc = "Práctica interactiva",
                            icon = Icons.Default.ChatBubble,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToSimulator
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionCard(
                            title = "Ponme a Prueba",
                            desc = "Examen situacional",
                            icon = Icons.Default.Quiz,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToTestMe
                        )
                        ActionCard(
                            title = "Consejos Web",
                            desc = "Desmitificar redes",
                            icon = Icons.Default.FactCheck,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToInternetAdvice
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionCard(
                            title = "Lab Bayesiano",
                            desc = "Calcular probabilidades",
                            icon = Icons.Default.Calculate,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToBayes
                        )
                        ActionCard(
                            title = "Reciprocidad",
                            desc = "Evaluar equilibrio",
                            icon = Icons.Default.Balance,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReciprocity
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ActionCard(
                            title = "Psicología Oscura",
                            desc = "Búnker anti-manipulación",
                            icon = Icons.Default.Security,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToDarkPsychology
                        )
                        ActionCard(
                            title = "Entrenador de Voz",
                            desc = "Cadencia & pausas",
                            icon = Icons.Default.Mic,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToVoiceCoach
                        )
                    }
                }
            }

            // Principio Científico del Día
            item {
                CriterioCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = RawModeAmber)
                        Text(
                            text = "PRINCIPIO CENTRAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = RawModeAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "«Ninguna señal aislada demuestra atracción o desinterés definitivo. La única evidencia confiable es el patrón consolidado de reciprocidad e inversión a lo largo del tiempo.»",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Modal de Repaso Espaciado SM-2
    if (showReviewDialog && dueCards.isNotEmpty() && currentReviewIndex < dueCards.size) {
        val currentCard = dueCards[currentReviewIndex]
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text(text = currentCard.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (showAnswer) {
                        Text(text = currentCard.explanation, style = MaterialTheme.typography.bodyMedium)
                        Divider()
                        Text(text = "¿Qué tan bien recordabas este principio?", style = MaterialTheme.typography.labelSmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = {
                                viewModel.reviewCard(currentCard, 1)
                                if (currentReviewIndex + 1 < dueCards.size) currentReviewIndex++ else showReviewDialog = false
                                showAnswer = false
                            }) { Text("Difícil (1)") }
                            TextButton(onClick = {
                                viewModel.reviewCard(currentCard, 3)
                                if (currentReviewIndex + 1 < dueCards.size) currentReviewIndex++ else showReviewDialog = false
                                showAnswer = false
                            }) { Text("Bien (3)") }
                            TextButton(onClick = {
                                viewModel.reviewCard(currentCard, 5)
                                if (currentReviewIndex + 1 < dueCards.size) currentReviewIndex++ else showReviewDialog = false
                                showAnswer = false
                            }) { Text("Fácil (5)") }
                        }
                    } else {
                        Button(
                            onClick = { showAnswer = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Mostrar Explicación")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // Modal Tutorial: Cómo Funciona Criterio
    if (showTutorialDialog) {
        com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
            title = "Tutorial: Cómo usar la Academia Criterio",
            objective = "Criterio es un gimnasio cognitivo para aprender a comunicarte, detectar reciprocidad y tomar decisiones racionales en relaciones y citas.",
            steps = listOf(
                com.urielhuerta.criterio.ui.components.TutorialStep(
                    stepNumber = 1,
                    title = "Completa tu plan curricular",
                    description = "Avanza lección por lección en la pestaña Academia. Lee los conceptos con evidencia científica y valida tu comprensión con el examen de cada tema."
                ),
                com.urielhuerta.criterio.ui.components.TutorialStep(
                    stepNumber = 2,
                    title = "Activa el Modo Crudo cuando necesites honestidad",
                    description = "El Modo Crudo no te dice lo que quieres escuchar: te protege del autoengaño pero también de la paranoia injustificada."
                ),
                com.urielhuerta.criterio.ui.components.TutorialStep(
                    stepNumber = 3,
                    title = "Usa las herramientas de análisis en situaciones reales",
                    description = "Cuando tengas dudas con alguien, entra al Analizador de Situaciones, al Laboratorio Bayesiano o al Búnker de Psicología Oscura."
                ),
                com.urielhuerta.criterio.ui.components.TutorialStep(
                    stepNumber = 4,
                    title = "Refuerza diariamente con Repaso Espaciado (SM-2)",
                    description = "Cada día la app seleccionará los conceptos clave que tu cerebro está a punto de olvidar para consolidarlos en tu memoria a largo plazo."
                )
            ),
            practicalExample = "Si alguien no te responde en horas, en lugar de angustiarte o mandar reclamos, abres el Analizador de Situaciones para separar hechos de suposiciones.",
            commonMistakes = listOf(
                "No intentes memorizar frases hechas; aprende los principios de reciprocidad y límites.",
                "No uses las herramientas para 'perseguir' a quien no muestra interés; úsalas para calibrar tu propia inversión."
            ),
            onDismiss = { showTutorialDialog = false }
        )
    }

    // Modal de Notificación de Actualización In-App
    if (viewModel.showUpdateDialog && viewModel.updateCheckResult?.isUpdateAvailable == true) {
        val res = viewModel.updateCheckResult!!
        AlertDialog(
            onDismissRequest = { if (!viewModel.isDownloadingUpdate) viewModel.showUpdateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = EvidenceHigh)
                    Text("¡Nueva Actualización!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Versión actual: v${res.currentVersion}  ➔  Nueva: v${res.latestVersion}",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = res.changelog,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (viewModel.isDownloadingUpdate) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Descargando actualización: ${(viewModel.downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = viewModel.downloadProgress,
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = EvidenceHigh
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.downloadAndInstallUpdate(context) },
                    enabled = !viewModel.isDownloadingUpdate,
                    colors = ButtonDefaults.buttonColors(containerColor = EvidenceHigh)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Text(if (viewModel.isDownloadingUpdate) "Descargando..." else "Actualizar Ahora")
                    }
                }
            },
            dismissButton = {
                if (!viewModel.isDownloadingUpdate) {
                    TextButton(onClick = { viewModel.showUpdateDialog = false }) {
                        Text("Recordar Más Tarde")
                    }
                }
            }
        )
    }
}

@Composable
fun ActionCard(
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
