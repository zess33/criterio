package com.urielhuerta.criterio.ui.screens.settings

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
import androidx.lifecycle.viewModelScope
import com.urielhuerta.criterio.data.preferences.UserPreferences
import com.urielhuerta.criterio.data.preferences.UserPreferencesRepository
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.components.RawModeBanner
import com.urielhuerta.criterio.ui.theme.RawModeAmber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    var apiKeyInput by mutableStateOf("")

    fun toggleRawMode(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setRawModeEnabled(enabled) }
    }

    fun saveApiKey() {
        viewModelScope.launch { preferencesRepository.setGeminiApiKey(apiKeyInput) }
    }

    fun setThemeMode(isDark: Boolean?) {
        viewModelScope.launch { preferencesRepository.setDarkMode(isDark) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val prefs by viewModel.preferences.collectAsState()

    LaunchedEffect(prefs.geminiApiKey) {
        viewModel.apiKeyInput = prefs.geminiApiKey
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
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
            // Modo Crudo
            item {
                RawModeBanner(
                    isEnabled = prefs.isRawModeEnabled,
                    onToggle = { viewModel.toggleRawMode(it) }
                )
            }

            // Gemini API Key
            item {
                CriterioCard {
                    Text("Integración de Inteligencia Artificial", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "La aplicación cuenta con un potente motor cognitivo offline. Opcionalmente puedes conectar tu API Key de Google Gemini para expandir respuestas dinámicas en el simulador.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = viewModel.apiKeyInput,
                        onValueChange = { viewModel.apiKeyInput = it },
                        label = { Text("Gemini API Key (Opcional)") },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { viewModel.saveApiKey() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Clave")
                    }
                }
            }

            // Tema Visual
            item {
                CriterioCard {
                    Text("Apariencia & Tema", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.setThemeMode(null) },
                            modifier = Modifier.weight(1f),
                            colors = if (prefs.isDarkMode == null) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Sistema")
                        }
                        OutlinedButton(
                            onClick = { viewModel.setThemeMode(false) },
                            modifier = Modifier.weight(1f),
                            colors = if (prefs.isDarkMode == false) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Claro")
                        }
                        OutlinedButton(
                            onClick = { viewModel.setThemeMode(true) },
                            modifier = Modifier.weight(1f),
                            colors = if (prefs.isDarkMode == true) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
                        ) {
                            Text("Oscuro")
                        }
                    }
                }
            }

            // Manifiesto de Criterio
            item {
                CriterioCard {
                    Text("Manifiesto de Criterio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "«No se trata de conseguir a cualquier persona a través de trucos o manipulación. Se trata de aprender a comunicarte mejor, reconocer reciprocidad real, elegir personas compatibles y saber cuándo avanzar, cuándo esperar y cuándo retirarte con dignidad.»",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
