package com.urielhuerta.criterio.ui.screens.journal

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urielhuerta.criterio.data.local.entities.JournalEntity
import com.urielhuerta.criterio.data.repository.JournalRepository
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RawModeAmber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class JournalViewModel(
    private val journalRepository: JournalRepository
) : ViewModel() {

    val entries: StateFlow<List<JournalEntity>> = journalRepository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var detectedPatterns by mutableStateOf<List<String>>(emptyList())

    // Formulario de Nueva Entrada
    var personAlias by mutableStateOf("")
    var contextDesc by mutableStateOf("")
    var factsInput by mutableStateOf("")
    var interpretationInput by mutableStateOf("")
    var emotionInput by mutableStateOf("Incertidumbre / Calma")
    var actionInput by mutableStateOf("")
    var outcomeInput by mutableStateOf("")

    fun scanPatterns() {
        viewModelScope.launch {
            detectedPatterns = journalRepository.getDetectedPatterns()
        }
    }

    fun saveEntry(onSaved: () -> Unit) {
        if (personAlias.isBlank() || factsInput.isBlank()) return
        viewModelScope.launch {
            journalRepository.addEntry(
                personAlias = personAlias,
                context = contextDesc,
                facts = factsInput,
                interpretation = interpretationInput,
                emotion = emotionInput,
                action = actionInput,
                outcome = outcomeInput
            )
            // Limpiar formulario
            personAlias = ""
            contextDesc = ""
            factsInput = ""
            interpretationInput = ""
            actionInput = ""
            outcomeInput = ""
            scanPatterns()
            onSaved()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel,
    onNavigateToNewEntry: () -> Unit
) {
    val entries by viewModel.entries.collectAsState()
    var showJournalTutorial by remember { mutableStateOf(false) }

    LaunchedEffect(entries) {
        viewModel.scanPatterns()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diario Cognitivo Privado", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showJournalTutorial = true }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Tutorial", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onNavigateToNewEntry) {
                        Icon(Icons.Default.Add, contentDescription = "Nueva Entrada", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewEntry) {
                Icon(Icons.Default.Edit, contentDescription = "Escribir")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Banner Tutorial
            item {
                com.urielhuerta.criterio.ui.components.QuickTutorialBanner(
                    title = "Tutorial: Cómo usar el Diario Cognitivo",
                    shortDesc = "Descubre cómo registrar eventos con formato TCC y detectar patrones.",
                    onOpenFullTutorial = { showJournalTutorial = true }
                )
            }

            // Patrones Detectados
            if (viewModel.detectedPatterns.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = RawModeAmber.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RawModeAmber.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = RawModeAmber)
                                Text("PATRONES RECURRENTES DETECTADOS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = RawModeAmber)
                            }
                            viewModel.detectedPatterns.forEach { p ->
                                Text("• $p", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            if (entries.isEmpty()) {
                item {
                    CriterioCard {
                        Text("Tu Diario está Vacío", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Registra interacciones reales diferenciando lo que ocurrió (Hechos) de lo que pensaste (Interpretación). La app detectará patrones cognitivos de forma privada en tu dispositivo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onNavigateToNewEntry, modifier = Modifier.fillMaxWidth()) {
                            Text("Registrar Primera Experiencia")
                        }
                    }
                }
            } else {
                items(entries) { entry ->
                    val dateStr = remember(entry.timestamp) {
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(entry.personAlias, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (entry.contextDescription.isNotBlank()) {
                                Text("Contexto: ${entry.contextDescription}", style = MaterialTheme.typography.bodySmall)
                            }
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            Text("Hechos Observables:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                            Text(entry.observableFacts, style = MaterialTheme.typography.bodyMedium)
                            Text("Mi Interpretación:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Text(entry.userInterpretation, style = MaterialTheme.typography.bodyMedium)
                            if (entry.primaryEmotion.isNotBlank()) {
                                Text("Emoción: ${entry.primaryEmotion}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        if (showJournalTutorial) {
            com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
                title = "Tutorial: Diario Cognitivo",
                objective = "Aprende a registrar eventos de tu vida social para identificar sesgos inconscientes y patrones de ansiedad.",
                steps = listOf(
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 1,
                        title = "Escribe el Hecho Observable",
                        description = "Solo lo que una cámara de video grabaría: qué palabras se dijeron, qué acción ocurrió, fechas o lugares."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 2,
                        title = "Escribe tu Interpretación y Emoción",
                        description = "Anota lo que pensaste de inmediato (ej. 'creo que se aburrió') y cómo te sentiste (ej. 'inseguridad, decepción')."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 3,
                        title = "Registra tu Acción y el Resultado Real",
                        description = "Qué hiciste al respecto y qué consecuencias tuvo en la interacción."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 4,
                        title = "Consulta los Patrones Recurrentes",
                        description = "Con el tiempo, el motor detectará si caes frecuentemente en Lectura de Mente, Catastrofismo o Paranoia de Tiempos."
                    )
                ),
                practicalExample = "Hecho: Vio mi historia de Instagram pero no respondió mi mensaje. Interpretación: 'Le caigo mal'. Detección: Lectura de Mente y Sobreanálisis de actividad digital.",
                commonMistakes = listOf(
                    "No mezcles lo que sentiste con lo que pasó en el campo de 'Hechos'.",
                    "Toda la información se guarda 100% en tu dispositivo y nunca sale de tu teléfono."
                ),
                onDismiss = { showJournalTutorial = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJournalEntryScreen(
    viewModel: JournalViewModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Registro Cognitivo", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                OutlinedTextField(
                    value = viewModel.personAlias,
                    onValueChange = { viewModel.personAlias = it },
                    label = { Text("Persona / Alias") },
                    placeholder = { Text("Ej: Laura, Cita de Tinder, Amiga de clase") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.contextDesc,
                    onValueChange = { viewModel.contextDesc = it },
                    label = { Text("Contexto") },
                    placeholder = { Text("Ej: Salida de café, Charla por WhatsApp") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.factsInput,
                    onValueChange = { viewModel.factsInput = it },
                    label = { Text("Hechos Objetivos (Lo que una cámara grabaría)") },
                    placeholder = { Text("Ej: Hablamos 20 min, respondió a las 4 horas, propuse un plan y dijo que revisaría su agenda.") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.interpretationInput,
                    onValueChange = { viewModel.interpretationInput = it },
                    label = { Text("Mi Interpretación Automática (Mis pensamientos)") },
                    placeholder = { Text("Ej: Creo que está perdiendo interés o que hice un comentario tonto.") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.emotionInput,
                    onValueChange = { viewModel.emotionInput = it },
                    label = { Text("Emoción Experimentada") },
                    placeholder = { Text("Ej: Ansiedad, Alivio, Confusión, Calma") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.actionInput,
                    onValueChange = { viewModel.actionInput = it },
                    label = { Text("Acción que Tomé") },
                    placeholder = { Text("Ej: Esperé con calma / Mandé otro mensaje") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = viewModel.outcomeInput,
                    onValueChange = { viewModel.outcomeInput = it },
                    label = { Text("Resultado Final Observable") },
                    placeholder = { Text("Ej: Al día siguiente confirmó el plan para el viernes.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            item {
                Button(
                    onClick = { viewModel.saveEntry(onNavigateBack) },
                    enabled = viewModel.personAlias.isNotBlank() && viewModel.factsInput.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar en Diario Privado")
                }
            }
        }
    }
}
