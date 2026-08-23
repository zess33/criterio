package com.urielhuerta.criterio.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urielhuerta.criterio.domain.model.EvidenceLevel
import com.urielhuerta.criterio.domain.model.ReciprocityLevel
import com.urielhuerta.criterio.ui.theme.*

@Composable
fun EvidenceBadge(
    evidenceLevel: EvidenceLevel,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (evidenceLevel) {
        EvidenceLevel.HIGH_EVIDENCE -> Pair(EvidenceHigh.copy(alpha = 0.15f), EvidenceHigh)
        EvidenceLevel.MODERATE_EVIDENCE -> Pair(EvidenceModerate.copy(alpha = 0.15f), EvidenceModerate)
        EvidenceLevel.LIMITED_EVIDENCE -> Pair(EvidenceLimited.copy(alpha = 0.15f), EvidenceLimited)
        EvidenceLevel.OPINION -> Pair(EvidenceOpinion.copy(alpha = 0.15f), EvidenceOpinion)
        EvidenceLevel.BELIEF_SYSTEM -> Pair(Color(0xFF6B7280).copy(alpha = 0.15f), Color(0xFF9CA3AF))
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = evidenceLevel.label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RawModeBanner(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) RawModeAmber.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isEnabled) {
            androidx.compose.foundation.BorderStroke(1.dp, RawModeAmber.copy(alpha = 0.4f))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Modo Crudo",
                    tint = if (isEnabled) RawModeAmber else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = if (isEnabled) "MODO CRUDO ACTIVO" else "Modo Crudo (Desactivado)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) RawModeAmber else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isEnabled) "Análisis directo y honesto sin complacencia" else "Toca para activar honestidad radical y sin filtros",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RawModeAmber,
                    checkedTrackColor = RawModeAmber.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun CriterioCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun ReciprocityGauge(
    level: ReciprocityLevel,
    modifier: Modifier = Modifier
) {
    val (color, fraction, icon) = when (level) {
        ReciprocityLevel.ALTA -> Triple(EvidenceHigh, 1.0f, Icons.Default.CheckCircle)
        ReciprocityLevel.MEDIA -> Triple(EvidenceLimited, 0.6f, Icons.Default.ChangeHistory)
        ReciprocityLevel.BAJA -> Triple(RiskRed, 0.25f, Icons.Default.WarningAmber)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Text(
                    text = level.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        LinearProgressIndicator(
            progress = fraction,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun ProbabilityBar(
    label: String,
    probability: Double,
    modifier: Modifier = Modifier
) {
    val pct = (probability * 100).toInt().coerceIn(0, 100)
    val color = when {
        pct > 70 -> EvidenceHigh
        pct in 35..70 -> EvidenceLimited
        else -> MaterialTheme.colorScheme.primary
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "$pct%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = color)
        }
        LinearProgressIndicator(
            progress = (probability.toFloat()).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

data class TutorialStep(
    val stepNumber: Int,
    val title: String,
    val description: String
)

@Composable
fun ScreenTutorialDialog(
    title: String,
    objective: String,
    steps: List<TutorialStep>,
    practicalExample: String,
    commonMistakes: List<String>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = objective,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Text("Paso a Paso:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                items(steps.size) { index ->
                    val s = steps[index]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${s.stepNumber}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(s.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EvidenceHigh.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Ejemplo Real:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                            Text(practicalExample, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (commonMistakes.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Qué Evitar:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RiskRed)
                            commonMistakes.forEach { m ->
                                Text("• $m", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("¡Entendido, vamos a practicar!")
            }
        }
    )
}

@Composable
fun QuickTutorialBanner(
    title: String,
    shortDesc: String,
    onOpenFullTutorial: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenFullTutorial),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(shortDesc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            TextButton(onClick = onOpenFullTutorial) {
                Text("Ver Guía", fontWeight = FontWeight.Bold)
            }
        }
    }
}

