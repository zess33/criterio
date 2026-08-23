package com.urielhuerta.criterio.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "criterio_user_preferences")

data class UserPreferences(
    val isRawModeEnabled: Boolean = false,
    val isOnboardingCompleted: Boolean = false,
    val experienceLevel: String = "Principiante",
    val primaryGoal: String = "Conversación y Seguridad",
    val geminiApiKey: String = "",
    val isDarkMode: Boolean? = null,
    val streakDays: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val RAW_MODE = booleanPreferencesKey("is_raw_mode_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val EXPERIENCE_LEVEL = stringPreferencesKey("experience_level")
        val PRIMARY_GOAL = stringPreferencesKey("primary_goal")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val DARK_MODE = stringPreferencesKey("dark_mode_preference")
        val STREAK_DAYS = intPreferencesKey("streak_days")
        val LAST_ACTIVE_TIMESTAMP = longPreferencesKey("last_active_timestamp")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val isRawMode = preferences[PreferencesKeys.RAW_MODE] ?: false
            val isOnboardingDone = preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
            val level = preferences[PreferencesKeys.EXPERIENCE_LEVEL] ?: "Principiante"
            val goal = preferences[PreferencesKeys.PRIMARY_GOAL] ?: "Conversación y Seguridad"
            val apiKey = preferences[PreferencesKeys.GEMINI_API_KEY] ?: ""
            val darkModeStr = preferences[PreferencesKeys.DARK_MODE]
            val darkMode = when (darkModeStr) {
                "DARK" -> true
                "LIGHT" -> false
                else -> null
            }
            val streak = preferences[PreferencesKeys.STREAK_DAYS] ?: 1
            val lastActive = preferences[PreferencesKeys.LAST_ACTIVE_TIMESTAMP] ?: System.currentTimeMillis()

            UserPreferences(
                isRawModeEnabled = isRawMode,
                isOnboardingCompleted = isOnboardingDone,
                experienceLevel = level,
                primaryGoal = goal,
                geminiApiKey = apiKey,
                isDarkMode = darkMode,
                streakDays = streak,
                lastActiveTimestamp = lastActive
            )
        }

    suspend fun setRawModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.RAW_MODE] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean, level: String, goal: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
            preferences[PreferencesKeys.EXPERIENCE_LEVEL] = level
            preferences[PreferencesKeys.PRIMARY_GOAL] = goal
        }
    }

    suspend fun setGeminiApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_API_KEY] = apiKey.trim()
        }
    }

    suspend fun setDarkMode(isDark: Boolean?) {
        context.dataStore.edit { preferences ->
            if (isDark == null) {
                preferences.remove(PreferencesKeys.DARK_MODE)
            } else {
                preferences[PreferencesKeys.DARK_MODE] = if (isDark) "DARK" else "LIGHT"
            }
        }
    }

    suspend fun updateStreak() {
        context.dataStore.edit { preferences ->
            val lastActive = preferences[PreferencesKeys.LAST_ACTIVE_TIMESTAMP] ?: 0L
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000L
            val currentStreak = preferences[PreferencesKeys.STREAK_DAYS] ?: 0

            val diffDays = (now - lastActive) / dayInMillis
            val newStreak = when {
                diffDays == 1L -> currentStreak + 1
                diffDays > 1L -> 1
                currentStreak == 0 -> 1
                else -> currentStreak
            }

            preferences[PreferencesKeys.STREAK_DAYS] = newStreak
            preferences[PreferencesKeys.LAST_ACTIVE_TIMESTAMP] = now
        }
    }
}
