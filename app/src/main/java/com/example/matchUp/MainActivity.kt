package com.example.matchUp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
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

                // Ambil rute saat ini untuk menyembunyikan BottomBar di Splash/Login/Register
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
        Triple("Home", "home", Icons.Default.Home),
        Triple("Insights", "insights", Icons.Outlined.Widgets),
        Triple("Wishlist", "wishlist", Icons.Outlined.FavoriteBorder),
        Triple("Profile", "profile", Icons.Outlined.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp // MENGHILANGKAN GARIS HITAM TIPIS DI ATAS NAVBAR
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { (label, route, icon) ->
            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != route) {
                        if ((route == "wishlist" || route == "profile") && !matchViewModel.isLoggedIn) {
                            // PERBAIKAN: Tambahkan blok navigasi agar tidak membuka login berkali-kali
                            navController.navigate("login") {
                                launchSingleTop = true // Kunci agar aplikasi tidak buka banyak layar login
                                restoreState = true
                            }
                        } else {
                            // Navigasi normal untuk Home & Insights
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontFamily = MyCustomFontFamily) },
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