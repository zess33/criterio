package com.urielhuerta.criterio.ui.screens.academy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.urielhuerta.criterio.data.local.dao.ModuleWithLessons
import com.urielhuerta.criterio.data.local.entities.LessonEntity
import com.urielhuerta.criterio.data.repository.EducationRepository
import com.urielhuerta.criterio.domain.model.EvidenceLevel
import com.urielhuerta.criterio.domain.model.QuizQuestion
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.components.EvidenceBadge
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RiskRed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AcademyViewModel(
    private val educationRepository: EducationRepository
) : ViewModel() {

    val modulesWithLessons: StateFlow<List<ModuleWithLessons>> = educationRepository.getModulesWithLessons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedCount: StateFlow<Int> = educationRepository.getCompletedLessonsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = educationRepository.getTotalLessonsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    var currentLesson by mutableStateOf<LessonEntity?>(null)
    var selectedQuizOption by mutableStateOf<Int?>(null)
    var isQuizAnswered by mutableStateOf(false)

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            currentLesson = educationRepository.getLessonById(lessonId)
            selectedQuizOption = null
            isQuizAnswered = false
        }
    }

    fun completeCurrentLesson(completed: Boolean = true) {
        currentLesson?.let { lesson ->
            viewModelScope.launch {
                educationRepository.completeLesson(lesson.id, completed)
                currentLesson = lesson.copy(isCompleted = completed)
            }
        }
    }

    fun markLessonCompleted(lessonId: String, completed: Boolean) {
        viewModelScope.launch {
            educationRepository.completeLesson(lessonId, completed)
            if (currentLesson?.id == lessonId) {
                currentLesson = currentLesson?.copy(isCompleted = completed)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyScreen(
    viewModel: AcademyViewModel,
    onNavigateToLesson: (String) -> Unit
) {
    val modules by viewModel.modulesWithLessons.collectAsState()
    val completed by viewModel.completedCount.collectAsState()
    val total by viewModel.totalCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Academia de Criterio", fontWeight = FontWeight.Bold) },
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
            // Header con progreso general
            item {
                CriterioCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Progreso Curricular", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("$completed de $total lecciones completadas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        val pct = if (total > 0) ((completed.toFloat() / total) * 100).toInt() else 0
                        Text("$pct%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = if (total > 0) completed.toFloat() / total else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Lista de los 11 niveles
            items(modules) { item ->
                ModuleCard(
                    moduleWithLessons = item,
                    onLessonClick = onNavigateToLesson
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    moduleWithLessons: ModuleWithLessons,
    onLessonClick: (String) -> Unit
) {
    val module = moduleWithLessons.module
    val lessons = moduleWithLessons.lessons
    var isExpanded by remember { mutableStateOf(module.levelIndex == 0) }

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
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NIVEL ${module.levelIndex}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = module.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isExpanded) 4 else 1
                    )
                }
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                lessons.forEach { lesson ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLessonClick(lesson.id) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (lesson.isCompleted) EvidenceHigh else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (lesson.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                                )
                                Text(
                                    text = lesson.evidenceLevel.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lessonId: String,
    viewModel: AcademyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNextLesson: (String) -> Unit = {}
) {
    LaunchedEffect(lessonId) {
        viewModel.loadLesson(lessonId)
    }

    val lesson = viewModel.currentLesson
    val modules by viewModel.modulesWithLessons.collectAsState()
    val gson = remember { Gson() }

    // Calcular la siguiente lección en orden secuencial
    val allLessons = remember(modules) { modules.flatMap { it.lessons } }
    val currentIndex = remember(allLessons, lessonId) { allLessons.indexOfFirst { it.id == lessonId } }
    val nextLesson = remember(allLessons, currentIndex) {
        if (currentIndex != -1 && currentIndex + 1 < allLessons.size) allLessons[currentIndex + 1] else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson?.title ?: "Lección", maxLines = 1, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (lesson == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val examples: List<String> = remember(lesson.examplesJson) {
                try {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson(lesson.examplesJson, type) ?: emptyList()
                } catch (e: Exception) { emptyList() }
            }

            val counterExamples: List<String> = remember(lesson.counterExamplesJson) {
                try {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson(lesson.counterExamplesJson, type) ?: emptyList()
                } catch (e: Exception) { emptyList() }
            }

            val commonErrors: List<String> = remember(lesson.commonErrorsJson) {
                try {
                    val type = object : TypeToken<List<String>>() {}.type
                    gson.fromJson(lesson.commonErrorsJson, type) ?: emptyList()
                } catch (e: Exception) { emptyList() }
            }

            val quiz: QuizQuestion? = remember(lesson.quizDataJson) {
                lesson.quizDataJson?.let {
                    try { gson.fromJson(it, QuizQuestion::class.java) } catch (e: Exception) { null }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                // Insignia de Evidencia
                item {
                    EvidenceBadge(evidenceLevel = lesson.evidenceLevel)
                }

                // Explicación Central del Concepto
                item {
                    CriterioCard {
                        Text("Marco Conceptual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(lesson.conceptExplanation, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                // Ejemplos Factuales
                if (examples.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = EvidenceHigh)
                                    Text("Ejemplos Factuales & Calibrados", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                                }
                                examples.forEach { ex ->
                                    Text("• $ex", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                // Contraejemplos y Distorsiones
                if (counterExamples.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = RiskRed.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RiskRed.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.HighlightOff, contentDescription = null, tint = RiskRed)
                                    Text("Contraejemplos & Conductas Poco Saludables", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                                }
                                counterExamples.forEach { cex ->
                                    Text("• $cex", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                // Errores Comunes
                if (commonErrors.isNotEmpty()) {
                    item {
                        CriterioCard {
                            Text("Errores Frecuentes de Interpretación", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            commonErrors.forEach { err ->
                                Text("⚠️ $err", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Examen / Comprobación de Comprensión
                if (quiz != null) {
                    item {
                        CriterioCard {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text("Comprobación de Criterio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(quiz.question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(12.dp))

                            quiz.options.forEachIndexed { index, option ->
                                val isSelected = viewModel.selectedQuizOption == index
                                val isCorrect = index == quiz.correctIndex
                                val letter = when (index) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    else -> "D"
                                }

                                val containerColor = when {
                                    !viewModel.isQuizAnswered -> if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                    isCorrect -> EvidenceHigh.copy(alpha = 0.18f)
                                    isSelected -> RiskRed.copy(alpha = 0.18f)
                                    else -> MaterialTheme.colorScheme.surface
                                }

                                val borderColor = when {
                                    !viewModel.isQuizAnswered -> if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                    isCorrect -> EvidenceHigh
                                    isSelected -> RiskRed
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable(enabled = !viewModel.isQuizAnswered) {
                                            viewModel.selectedQuizOption = index
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = containerColor),
                                    border = androidx.compose.foundation.BorderStroke(if (isSelected || (viewModel.isQuizAnswered && isCorrect)) 2.dp else 1.dp, borderColor)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = letter,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (viewModel.isQuizAnswered) {
                                            if (isCorrect) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Correcto", tint = EvidenceHigh)
                                            } else if (isSelected) {
                                                Icon(Icons.Default.Cancel, contentDescription = "Incorrecto", tint = RiskRed)
                                            }
                                        } else {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { viewModel.selectedQuizOption = index }
                                            )
                                        }
                                    }
                                }
                            }

                            if (!viewModel.isQuizAnswered) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        viewModel.isQuizAnswered = true
                                        if (viewModel.selectedQuizOption == quiz.correctIndex) {
                                            viewModel.completeCurrentLesson(true)
                                        }
                                    },
                                    enabled = viewModel.selectedQuizOption != null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Validar Respuesta", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (viewModel.selectedQuizOption == quiz.correctIndex) EvidenceHigh.copy(alpha = 0.12f) else RiskRed.copy(alpha = 0.12f)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (viewModel.selectedQuizOption == quiz.correctIndex) EvidenceHigh else RiskRed)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = if (viewModel.selectedQuizOption == quiz.correctIndex) "✓ ¡Respuesta Calibrada y Correcta!" else "⚠️ Revisa el Razonamiento:",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (viewModel.selectedQuizOption == quiz.correctIndex) EvidenceHigh else RiskRed
                                        )
                                        Text(
                                            text = quiz.explanation,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Botones de Acción & Avance a la Siguiente Lección
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Botón de Siguiente Lección (si existe)
                        if (nextLesson != null) {
                            Button(
                                onClick = {
                                    viewModel.completeCurrentLesson(true)
                                    onNavigateToNextLesson(nextLesson.id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Siguiente Lección: ${nextLesson.title.take(24)}...",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = EvidenceHigh)
                                    Text(
                                        "🎉 ¡Has completado todas las lecciones de la Academia!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EvidenceHigh
                                    )
                                }
                            }
                        }

                        // Botón de Alternar Estado de Lección
                        OutlinedButton(
                            onClick = {
                                val newState = !lesson.isCompleted
                                viewModel.completeCurrentLesson(newState)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (lesson.isCompleted) ButtonDefaults.outlinedButtonColors(contentColor = EvidenceHigh) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked, contentDescription = null)
                                Text(if (lesson.isCompleted) "Lección Completada ✓ (Toca para desmarcar)" else "Marcar como Completada")
                            }
                        }

                        // Botón de Volver al Menú
                        TextButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver al Índice de la Academia")
                        }
                    }
                }
            }
        }
    }
}
