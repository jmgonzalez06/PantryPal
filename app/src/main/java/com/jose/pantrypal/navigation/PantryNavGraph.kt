package com.jose.pantrypal.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jose.pantrypal.auth.LoginScreen
import com.jose.pantrypal.auth.SignUpScreen
import com.jose.pantrypal.dashboard.DashboardScreen
import com.jose.pantrypal.inventory.InventoryScreen
import com.jose.pantrypal.profile.ProfileScreen
import com.jose.pantrypal.storage.StorageScreen

data class BottomNavItem(
    val route: String,
    val label: String
)

@Composable
fun PantryPalApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(Routes.DASHBOARD, "Dashboard"),
        BottomNavItem(Routes.INVENTORY, "Inventory"),
        BottomNavItem(Routes.STORAGE, "Storage"),
        BottomNavItem(Routes.PROFILE, "Profile")
    )

    val showBottomBar = shouldShowBottomBar(
        currentDestination = currentDestination,
        bottomNavItems = bottomNavItems
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        // Keep dashboard as the root of main graph
                                        popUpTo(Routes.DASHBOARD) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            label = { Text(item.label) },
                            // Simple label in the icon slot for now; we can add real icons later
                            icon = { Text(item.label.first().toString()) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SIGN_UP)
                    },
                    onNavigateToDashboard = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Routes.SIGN_UP) {
                SignUpScreen(
                    onNavigateToLogin = {
                        // Simple behavior for now: go back
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen()
            }

            composable(Routes.INVENTORY) {
                InventoryScreen()
            }

            composable(Routes.STORAGE) {
                StorageScreen()
            }

            composable(Routes.PROFILE) {
                ProfileScreen()
            }
        }
    }
}

private fun shouldShowBottomBar(
    currentDestination: NavDestination?,
    bottomNavItems: List<BottomNavItem>
): Boolean {
    val bottomRoutes = bottomNavItems.map { it.route }
    val currentRoute = currentDestination?.route
    return currentRoute != null && bottomRoutes.contains(currentRoute)
}