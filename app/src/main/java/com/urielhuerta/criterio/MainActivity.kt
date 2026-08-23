package com.urielhuerta.criterio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.urielhuerta.criterio.ui.navigation.CriterioNavGraph
import com.urielhuerta.criterio.ui.navigation.Screen
import com.urielhuerta.criterio.ui.theme.CriterioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CriterioApp

        setContent {
            val userPrefs by app.userPreferencesRepository.userPreferencesFlow.collectAsState(
                initial = null
            )

            // Esperar a que DataStore cargue las preferencias iniciales
            if (userPrefs != null) {
                val isDarkTheme = when (userPrefs?.isDarkMode) {
                    true -> true
                    false -> false
                    null -> isSystemInDarkTheme()
                }

                CriterioTheme(darkTheme = isDarkTheme) {
                    val navController = rememberNavController()
                    val startDestination = if (userPrefs?.isOnboardingCompleted == true) {
                        Screen.Home.route
                    } else {
                        Screen.Onboarding.route
                    }

                    CriterioNavGraph(
                        navController = navController,
                        app = app,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
