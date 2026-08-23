package com.urielhuerta.criterio.ui.screens.darkpsychology

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.urielhuerta.criterio.domain.engine.DarkAnalysisResult
import com.urielhuerta.criterio.domain.engine.DarkPsychologyEngine
import com.urielhuerta.criterio.domain.engine.DarkTactic
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RawModeAmber
import com.urielhuerta.criterio.ui.theme.RiskRed

class DarkPsychologyViewModel(
    private val darkEngine: DarkPsychologyEngine = DarkPsychologyEngine()
) : ViewModel() {
    val tactics = darkEngine.tacticsCatalog
    var testInput by mutableStateOf("")
    var analysisResult by mutableStateOf<DarkAnalysisResult?>(null)
    var selectedTab by mutableStateOf(0) // 0: Radar / Analizador, 1: Catálogo de Tácticas, 2: Búnker de Contramedidas

    fun analyzeInput() {
        if (testInput.isBlank()) return
        analysisResult = darkEngine.analyzeManipulativeInput(testInput)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkPsychologyScreen(
    viewModel: DarkPsychologyViewModel = remember { DarkPsychologyViewModel() },
    onNavigateBack: () -> Unit
) {
    var showDarkTutorial by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = RiskRed)
                        Text("Psicología Oscura & Contramedidas", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showDarkTutorial = true }) {
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
        ) {
            // Tab Selector
            TabRow(selectedTabIndex = viewModel.selectedTab) {
                Tab(
                    selected = viewModel.selectedTab == 0,
                    onClick = { viewModel.selectedTab = 0 },
                    text = { Text("Radar Manipulación") },
                    icon = { Icon(Icons.Default.Radar, contentDescription = null) }
                )
                Tab(
                    selected = viewModel.selectedTab == 1,
                    onClick = { viewModel.selectedTab = 1 },
                    text = { Text("Grimorio Tácticas") },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) }
                )
                Tab(
                    selected = viewModel.selectedTab == 2,
                    onClick = { viewModel.selectedTab = 2 },
                    text = { Text("Búnker Inmunidad") },
                    icon = { Icon(Icons.Default.Shield, contentDescription = null) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                when (viewModel.selectedTab) {
                    0 -> {
                        // RADAR DE MANIPULACIÓN
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1116)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RiskRed.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = RiskRed)
                                        Text("DETECTOR DE JUEGOS SUCIOS & CHANTAJES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                                    }
                                    Text(
                                        text = "Pega un mensaje sospechoso, frase hiriente o dinámica de control para identificar la táctica oscura subyacente y obtener la respuesta quirúrgica para neutralizarla.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = viewModel.testInput,
                                        onValueChange = { viewModel.testInput = it },
                                        placeholder = { Text("Ej: 'Estás loco, eso nunca pasó' o 'Si me quisieras no saldrías con ellos'...") },
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { viewModel.analyzeInput() },
                                        enabled = viewModel.testInput.isNotBlank(),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = RiskRed)
                                    ) {
                                        Text("Escanear Vector de Ataque")
                                    }
                                }
                            }
                        }

                        viewModel.analysisResult?.let { res ->
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (res.manipulationLevel == "CRÍTICO") RiskRed else RawModeAmber)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(res.detectedTacticName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RiskRed)
                                            Surface(
                                                color = if (res.manipulationLevel == "CRÍTICO") RiskRed.copy(alpha = 0.2f) else RawModeAmber.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    text = "PELIGRO: ${res.manipulationLevel}",
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (res.manipulationLevel == "CRÍTICO") RiskRed else RawModeAmber
                                                )
                                            }
                                        }

                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                                        Text("1. Objetivo del Manipulador:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Text(res.psychologicalObjective, style = MaterialTheme.typography.bodyMedium)

                                        Text("2. Vulnerabilidad que Intenta Explotar:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = RawModeAmber)
                                        Text(res.victimVulnerability, style = MaterialTheme.typography.bodyMedium)

                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.12f)),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("CONTRAMEDIDA QUIRÚRGICA INMEDIATA (Qué Responder):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                                                Text(res.immediateSurgicalResponse, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Text("3. Acción Estratégica a Largo Plazo:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                        Text(res.longTermAction, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // GRIMORIO DE TÁCTICAS OSCURAS
                        item {
                            CriterioCard {
                                Text("Catálogo de Tácticas Maquiavélicas & Oscuras", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "El conocimiento de estas técnicas es la única vacuna real. Conoce su anatomía para identificarlas al primer síntoma y desactivarlas sin desgastarte.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        items(viewModel.tactics) { tactic ->
                            var expanded by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { expanded = !expanded },
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(tactic.darkCategory, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                                            Text(tactic.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        }
                                        IconButton(onClick = { expanded = !expanded }) {
                                            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                                        }
                                    }

                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text("Mecanismo Psicológico:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        Text(tactic.psychologicalMechanism, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text("Frases Típicas:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RawModeAmber)
                                        tactic.examplePhrases.forEach { ph ->
                                            Text("• \"$ph\"", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.1f)),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh.copy(alpha = 0.4f))
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Text("Contramedida: ${tactic.counterMeasureName}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                                                Text(tactic.surgicalResponse, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text("Lo que NUNCA debes hacer:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                                        Text(tactic.whatNeverToDo, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // BÚNKER DE INMUNIDAD PSICOLÓGICA
                        item {
                            CriterioCard {
                                Text("Protocolos de Inmunidad Psicológica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Los manipuladores se alimentan de tu reactividad emocional. Estos cuatro protocolos desarman cualquier intento de juego sucio.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        item {
                            DefenseCard(
                                title = "1. Técnica de la Piedra Gris (Gray Rock)",
                                subtitle = "Desactiva Narcisistas y Maquiavélicos",
                                desc = "Conviértete en el ser más aburrido, plano y poco reactivo del mundo. Responde con monosílabos tranquilos ('Ya veo', 'Entiendo', 'Interesante'). Sin drama que consumir, el manipulador se aburre y busca otra presa."
                            )
                        }

                        item {
                            DefenseCard(
                                title = "2. La Regla Anti-JADE",
                                subtitle = "No Justifiques, No Argumentes, No Defiendas, No Expliques",
                                desc = "Ante un chantaje o reclamo absurdo, NO uses JADE. Explicar demasiado le entrega al manipulador munición para rebatir cada palabra. Di tu 'No' firme y cállate."
                            )
                        }

                        item {
                            DefenseCard(
                                title = "3. Técnica del Disco Rayado",
                                subtitle = "Inmunidad ante Desvíos de Conversación",
                                desc = "Cuando intenten cambiar de tema o culparte a ti, repite exactamente la misma frase calmada sin variar: 'Entiendo eso, pero el hecho de hoy es que cancelaste sin avisar'. Repite 3 veces con serenidad robótica."
                            )
                        }

                        item {
                            DefenseCard(
                                title = "4. Banco de Niebla (Fogging)",
                                subtitle = "Neutraliza Insultos o Críticas Manipulativas",
                                desc = "Acepta la parte objetiva o posible de una crítica sin alterarte: 'Es posible que a veces sea reservado, pero esta decisión no cambia'. Absorbe el golpe sin rebotar hostilidad."
                            )
                        }
                    }
                }
            }
        }

        if (showDarkTutorial) {
            com.urielhuerta.criterio.ui.components.ScreenTutorialDialog(
                title = "Tutorial: Psicología Oscura & Búnker de Inmunidad",
                objective = "Aprende a neutralizar cualquier juego sucio, chantaje emocional o táctica maquiavélica sin desgastarte.",
                steps = listOf(
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 1,
                        title = "Usa el Radar de Manipulación",
                        description = "Pega un mensaje sospechoso o frase hiriente. El motor te dirá el vector de ataque y la respuesta quirúrgica exacta."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 2,
                        title = "Aplica la Técnica de la Piedra Gris (Gray Rock)",
                        description = "Conviértete en alguien aburrido e inexpresivo ante provocaciones. Los manipuladores buscan tu reacción emocional para alimentarse; sin ella, se retiran."
                    ),
                    com.urielhuerta.criterio.ui.components.TutorialStep(
                        stepNumber = 3,
                        title = "Nunca uses JADE ante un manipulador",
                        description = "No Justifiques, no Argumentes, no Defiendas, no Expliques. Dar explicaciones largas le da munición para manipularte."
                    )
                ),
                practicalExample = "Ataque: 'Si me quisieras no irías con tus amigos'. Respuesta Quirúrgica: 'Te quiero, pero mi decisión de salir está tomada'. Sin discutir ni justificarte.",
                commonMistakes = listOf(
                    "No uses la psicología oscura para manipular a otros; úsala como escudo y vacuna para defenderte.",
                    "No intentes hacer 'entrar en razón' a una persona con rasgos narcisistas o maquiavélicos mediante debates infinitos."
                ),
                onDismiss = { showDarkTutorial = false }
            )
        }
    }
}

@Composable
fun DefenseCard(
    title: String,
    subtitle: String,
    desc: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(subtitle.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
