package com.hermes.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hermes.android.presentation.ui.screen.AgentsScreen
import com.hermes.android.presentation.ui.screen.ChatScreen
import com.hermes.android.presentation.ui.screen.MainScreen
import com.hermes.android.presentation.ui.screen.SettingsScreen
import com.hermes.android.presentation.ui.screen.SetupScreen
import com.hermes.android.presentation.ui.screen.SkillsScreen
import com.hermes.android.presentation.ui.screen.SplashScreen

@Composable
fun HermesAppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.Splash.route) { SplashScreen(navController) }
        composable(Routes.Setup.route) { SetupScreen(navController) }
        composable(Routes.Main.route) { MainScreen() }
    }
}
