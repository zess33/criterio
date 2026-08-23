package com.urielhuerta.criterio.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urielhuerta.criterio.data.preferences.UserPreferences
import com.urielhuerta.criterio.data.preferences.UserPreferencesRepository
import com.urielhuerta.criterio.data.updater.AppUpdateManager
import com.urielhuerta.criterio.data.updater.UpdateCheckResult
import com.urielhuerta.criterio.ui.components.CriterioCard
import com.urielhuerta.criterio.ui.components.RawModeBanner
import com.urielhuerta.criterio.ui.theme.EvidenceHigh
import com.urielhuerta.criterio.ui.theme.RawModeAmber
import com.urielhuerta.criterio.ui.theme.RiskRed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val appUpdateManager: AppUpdateManager = AppUpdateManager()
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    var apiKeyInput by mutableStateOf("")

    // In-App Updater State
    var isCheckingUpdates by mutableStateOf(false)
    var updateResult by mutableStateOf<UpdateCheckResult?>(null)
    var isDownloading by mutableStateOf(false)
    var downloadProgress by mutableStateOf(0f)
    var updateErrorMessage by mutableStateOf<String?>(null)

    fun toggleRawMode(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setRawModeEnabled(enabled) }
    }

    fun saveApiKey() {
        viewModelScope.launch { preferencesRepository.setGeminiApiKey(apiKeyInput) }
    }

    fun setThemeMode(isDark: Boolean?) {
        viewModelScope.launch { preferencesRepository.setDarkMode(isDark) }
    }

    fun checkForUpdates() {
        if (isCheckingUpdates || isDownloading) return
        isCheckingUpdates = true
        updateErrorMessage = null
        viewModelScope.launch {
            updateResult = appUpdateManager.checkForUpdates()
            isCheckingUpdates = false
        }
    }

    fun downloadAndInstall(context: Context) {
        val downloadUrl = updateResult?.downloadUrl ?: return
        if (isDownloading) return
        isDownloading = true
        downloadProgress = 0f
        updateErrorMessage = null

        viewModelScope.launch {
            appUpdateManager.downloadAndInstallApk(
                context = context,
                downloadUrl = downloadUrl,
                onProgress = { p -> downloadProgress = p },
                onSuccess = { isDownloading = false },
                onError = { err ->
                    isDownloading = false
                    updateErrorMessage = err
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val prefs by viewModel.preferences.collectAsState()
    val context = LocalContext.current

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

            // Actualizaciones In-App desde GitHub
            item {
                CriterioCard {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Actualizaciones de la Aplicación", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Comprueba directamente en GitHub si hay nuevas lecciones, simulaciones o mejoras y descárgalas al instante desde la app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Versión Instalada: v1.0.0 (Build 1)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.checkForUpdates() },
                        enabled = !viewModel.isCheckingUpdates && !viewModel.isDownloading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (viewModel.isCheckingUpdates) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consultando GitHub...")
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Text("Buscar Actualizaciones en GitHub")
                            }
                        }
                    }

                    viewModel.updateResult?.let { res ->
                        Spacer(modifier = Modifier.height(12.dp))
                        if (res.isUpdateAvailable) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = EvidenceHigh.copy(alpha = 0.12f)),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, EvidenceHigh)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.NewReleases, contentDescription = null, tint = EvidenceHigh)
                                        Text("¡Nueva Versión Disponible: v${res.latestVersion}!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = EvidenceHigh)
                                    }
                                    Text(res.changelog, style = MaterialTheme.typography.bodySmall)

                                    if (viewModel.isDownloading) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Descargando actualización: ${(viewModel.downloadProgress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                        LinearProgressIndicator(
                                            progress = viewModel.downloadProgress,
                                            modifier = Modifier.fillMaxWidth().height(6.dp),
                                            color = EvidenceHigh
                                        )
                                    } else {
                                        Button(
                                            onClick = { viewModel.downloadAndInstall(context) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = EvidenceHigh)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Icon(Icons.Default.Download, contentDescription = null)
                                                Text("Descargar e Instalar Actualización")
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EvidenceHigh)
                                    Text(
                                        "Tu aplicación está al día (v${res.currentVersion}).",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    viewModel.updateErrorMessage?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("❌ $err", style = MaterialTheme.typography.bodySmall, color = RiskRed)
                    }
                }
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
