package com.jose.pantrypal.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import com.jose.pantrypal.auth.LoginRoute
import com.jose.pantrypal.auth.SignUpRoute
import com.jose.pantrypal.dashboard.DashboardScreen
import com.jose.pantrypal.inventory.InventoryScreen
import com.jose.pantrypal.profile.ProfileRoute
import com.jose.pantrypal.storage.StorageScreen
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.jose.pantrypal.auth.FirebaseAuthRepository
import com.jose.pantrypal.inventory.InventoryViewModel
import com.jose.pantrypal.inventory.ItemDetailScreen
import com.jose.pantrypal.inventory.AddItemScreen


data class BottomNavItem(
    val route: String,
    val label: String
)

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun PantryPalApp() {
    val authRepository = remember {
        FirebaseAuthRepository(FirebaseAuth.getInstance())
    }

    val navController = rememberNavController()

    val startDestination = remember {
        if (authRepository.getCurrentUser() != null) {
            Routes.DASHBOARD
        } else {
            Routes.LOGIN
        }
    }

    LaunchedEffect(authRepository.getCurrentUser()) {
        if (authRepository.getCurrentUser() == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

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
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = false
                                }
                            },
                            label = { Text(item.label) },
                            icon = { Text(item.label.first().toString()) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginRoute(
                    onNavigateToSignUp = {
                        navController.navigate(Routes.SIGN_UP)
                    },
                    onLoginSuccess = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Routes.SIGN_UP) {
                SignUpRoute(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Routes.ITEM_DETAIL) {
                backStackEntry ->

                val itemId = backStackEntry.arguments
                    ?.getString("itemId")

                val inventoryViewModel: InventoryViewModel =
                    viewModel(navController.getBackStackEntry(Routes.INVENTORY))

                val item = itemId?.let { inventoryViewModel.getItemById(it) }

                if (item != null) {
                    ItemDetailScreen(
                        item = item,
                        viewModel = inventoryViewModel,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    Text(
                        text = "Loading item...",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            composable(route = Routes.ADD_ITEM) {

                val inventoryViewModel: InventoryViewModel =
                    viewModel(navController.getBackStackEntry(Routes.INVENTORY))

                AddItemScreen(
                    viewModel = inventoryViewModel,
                    onItemAdded = { navController.popBackStack() }
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onExpiringTodayClick = {
                        navController.navigate(Routes.INVENTORY) {
                            launchSingleTop = true
                        }
                    },
                    onExpiringSoonClick = {
                        navController.navigate(Routes.INVENTORY) {
                            launchSingleTop = true
                        }
                    },
                    onTotalItemsClick = {
                        navController.navigate(Routes.INVENTORY) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.INVENTORY) {
                InventoryScreen(
                    onItemClick = { itemId ->
                        navController.navigate(Routes.itemDetail(itemId))
                    },
                    onAddItemClick = {
                        navController.navigate(Routes.ADD_ITEM)
                    }
                )
            }

            composable(Routes.STORAGE) {
                StorageScreen()
            }

            composable(Routes.PROFILE) {
                ProfileRoute(
                    onSignedOut = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
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