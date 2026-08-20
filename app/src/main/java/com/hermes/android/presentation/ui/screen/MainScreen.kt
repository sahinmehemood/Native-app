package com.hermes.android.presentation.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryFlow
import androidx.navigation.compose.rememberNavController
import com.hermes.android.presentation.navigation.Routes
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesSpacing

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { HermesBottomNav(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Chat.route,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable(Routes.Chat.route) { ChatScreen() }
            composable(Routes.Agents.route) { AgentsScreen() }
            composable(Routes.Skills.route) { SkillsScreen() }
            composable(Routes.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun HermesBottomNav(navController: NavHostController) {
    val items = listOf(
        Triple(Routes.Chat, "Chat", Icons.Filled.ChatBubble),
        Triple(Routes.Agents, "Agents", Icons.Filled.SmartToy),
        Triple(Routes.Skills, "Skills", Icons.Filled.Extension),
        Triple(Routes.Settings, "Settings", Icons.Filled.Settings)
    )
    val current by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(
        initialValue = navController.currentBackStackEntry
    )
    val currentRoute = current?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                selected = currentRoute == route.route,
                onClick = { navController.navigate(route.route) { launchSingleTop = true } },
                icon = { androidx.compose.material3.Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = HermesColors.Primary,
                    selectedTextColor = HermesColors.Primary,
                    indicatorColor = HermesColors.PrimaryContainer
                )
            )
        }
    }
}
