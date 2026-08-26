package com.hermes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.hermes.android.core.data.ConnectionState
import com.hermes.android.core.design.theme.HermesTheme
import com.hermes.android.core.navigation.HermesNavGraph
import com.hermes.android.core.navigation.Route
import com.hermes.android.core.ui.states.OfflineBanner
import com.hermes.android.di.appModule
import com.hermes.android.feature.activity.activityDestination
import com.hermes.android.feature.automations.automationsDestination
import com.hermes.android.feature.chat.chatDestination
import com.hermes.android.feature.home.homeDestination
import com.hermes.android.feature.nous.nousDestination
import com.hermes.android.feature.sessions.sessionDetailDestination
import com.hermes.android.feature.sessions.sessionsDestination
import com.hermes.android.feature.settings.settingsDestination
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule())
        }
        // Reduced-motion follows the system accessibility flag; Settings can override later.
        val reduceMotion = android.provider.Settings.Global.getInt(
            contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1,
        ) == 0
        setContent {
            HermesTheme(darkTheme = isSystemInDarkTheme(), reducedMotion = reduceMotion) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

/**
 * App shell: owns the [androidx.navigation.NavHostController], renders the shared
 * connection banner (driven by [ConnectionState]), and hosts the nav graph
 * assembled from each feature's [com.hermes.android.core.navigation.HermesDestination].
 */
@Composable
private fun AppRoot() {
    val navController = rememberNavController()
    val connectionState: ConnectionState = koinInject()
    val connection by connectionState.status.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        OfflineBanner(connection = connection, onReconnect = connectionState::probe)
        HermesNavGraph(
            navController = navController,
            startDestination = Route.Home.route,
            destinations = listOf(
                homeDestination(
                    onNavigateToChat = { id -> navController.navigate(Route.Chat.createRoute(id)) },
                    onNavigateToSettings = { navController.navigate(Route.Settings.route) },
                ),
                chatDestination(),
                settingsDestination(onNavigateUp = { navController.popBackStack() }),
                activityDestination(),
                sessionsDestination(
                    onNavigateToDetail = { id -> navController.navigate(Route.SessionDetail.createRoute(id)) },
                    onNavigateUp = { navController.popBackStack() },
                ),
                sessionDetailDestination(
                    onNavigateUp = { navController.popBackStack() },
                    onOpenInChat = { id -> navController.navigate(Route.Chat.createRoute(id)) },
                ),
                activityDestination(
                    onNavigateUp = { navController.popBackStack() },
                    onOpenSession = { id -> navController.navigate(Route.SessionDetail.createRoute(id)) },
                ),
                nousDestination(
                    onNavigateUp = { navController.popBackStack() },
                    onOpenSession = { id -> navController.navigate(Route.SessionDetail.createRoute(id)) },
                ),
                automationsDestination(
                    onNavigateUp = { navController.popBackStack() },
                    onOpenSession = { id -> navController.navigate(Route.Chat.createRoute(id)) },
                ),
            ),
        )
    }
}
