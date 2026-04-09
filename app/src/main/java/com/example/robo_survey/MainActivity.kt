package com.example.robo_survey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.robo_survey.ui.RobotViewModel
import com.example.robo_survey.ui.screens.ControlScreen
import com.example.robo_survey.ui.screens.DashboardScreen
import com.example.robo_survey.ui.screens.GalleryScreen
import com.example.robo_survey.ui.screens.LiveFeedScreen
import com.example.robo_survey.ui.screens.MapScreen
import com.example.robo_survey.ui.theme.Robo_surveyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Robo_surveyTheme {
                MainApp()
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Live : Screen("live", "Live", Icons.Default.Videocam)
    object Control : Screen("control", "Control", Icons.Default.SettingsRemote)
    object Map : Screen("map", "Map", Icons.Default.Map)
    object Gallery : Screen("gallery", "Gallery", Icons.Default.PhotoLibrary)
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: RobotViewModel = viewModel()
    val items = listOf(Screen.Dashboard, Screen.Live, Screen.Control, Screen.Map, Screen.Gallery)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(viewModel) }
            composable(Screen.Live.route) { LiveFeedScreen(viewModel) }
            composable(Screen.Control.route) { ControlScreen(viewModel) }
            composable(Screen.Map.route) { MapScreen(viewModel) }
            composable(Screen.Gallery.route) { GalleryScreen(viewModel) }
        }
    }
}
