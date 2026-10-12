package com.stadiolinks.kitabu.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.stadiolinks.kitabu.R
import com.stadiolinks.kitabu.ui.bookings.BookingsRoute
import com.stadiolinks.kitabu.ui.catalog.CatalogRoute
import com.stadiolinks.kitabu.ui.theme.StadioColourStrip

object Routes {
    const val CATALOG = "catalog"
    const val BOOKINGS = "bookings"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitabuApp() {
    // The NavController remembers which screen is showing and moves between them
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            // The title follows whichever screen is open
                            Text(
                                text = stringResource(
                                    if (currentRoute == Routes.BOOKINGS) R.string.nav_bookings else R.string.app_name
                                ),
                                style = MaterialTheme.typography.titleLarge
                            )
                            // STADIO line under the title
                            Text(
                                text = stringResource(R.string.app_tagline),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
                // Strip of all 8 school colours under the bar
                StadioColourStrip()
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == Routes.CATALOG,
                    onClick = { navController.navigateToTab(Routes.CATALOG) },
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_catalog)) }
                )
                NavigationBarItem(
                    selected = currentRoute == Routes.BOOKINGS,
                    onClick = { navController.navigateToTab(Routes.BOOKINGS) },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_bookings)) }
                )
            }
        }
    ) { innerPadding ->
        // innerPadding keeps the screen clear of the two bars.
        // NavHost shows whichever screen matches the current route.
        NavHost(
            navController = navController,
            startDestination = Routes.CATALOG,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.CATALOG) { CatalogRoute() }
            composable(Routes.BOOKINGS) { BookingsRoute() }
        }
    }
}

/** Switches tab without piling up a new copy of the screen every time a tab is tapped. */
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
