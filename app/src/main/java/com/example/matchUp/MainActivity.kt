package com.example.matchUp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.nav.SetupNavGraph
import com.example.matchUp.ui.theme.MyApplication1Theme
import com.example.matchUp.ui.theme.MyCustomFontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication1Theme {
                val navController = rememberNavController()
                val matchViewModel: MatchViewModel = viewModel()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    matchViewModel.loadData(context)
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBarRoutes = listOf("home", "insights", "wishlist", "profile")

                Scaffold(
                    bottomBar = {
                        if (currentRoute in showBottomBarRoutes) {
                            AppBottomNavigation(navController = navController, matchViewModel = matchViewModel)
                        }
                    }
                ) { innerPadding ->
                    SetupNavGraph(
                        navController = navController,
                        matchViewModel = matchViewModel,
                        paddingValues = innerPadding
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController, matchViewModel: MatchViewModel) {
    val items = listOf(
        Quadruple("Home", "home", Icons.Outlined.Home, Icons.Filled.Home),
        Quadruple("Insights", "insights", Icons.Outlined.Widgets, Icons.Filled.Widgets),
        Quadruple("Wishlist", "wishlist", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite),
        Quadruple("Profile", "profile", Icons.Outlined.Person, Icons.Filled.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { (label, route, unselectedIcon, selectedIcon) ->
            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != route) {
                        // Navigasi langsung dijalankan tanpa menunggu state lain
                        val targetRoute = if ((route == "wishlist" || route == "profile") && !matchViewModel.isLoggedIn) {
                            "login"
                        } else {
                            route
                        }

                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) selectedIcon else unselectedIcon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontFamily = MyCustomFontFamily,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    indicatorColor = Color(0xFFFFD1E3),
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.Black,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)