package com.hermes.android.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * A single nav-graph entry. A feature exposes one of these (see the
 * `xxxDestination()` helpers in each feature module) and the app shell assembles
 * the list — keeping feature → core dependency direction clean (features depend
 * on core:navigation; core:navigation knows nothing about features).
 */
data class HermesDestination(
    val route: String,
    val content: @Composable (NavHostController) -> Unit,
)

/**
 * The app's single [NavHost]. The shell passes the assembled [destinations] and
 * the [startDestination]; individual composable screens stay unaware of routing.
 */
@Composable
fun HermesNavGraph(
    navController: NavHostController,
    startDestination: String,
    destinations: List<HermesDestination>,
    modifier: Modifier = Modifier.fillMaxSize(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        destinations.forEach { dest ->
            composable(dest.route) { dest.content(navController) }
        }
    }
}
