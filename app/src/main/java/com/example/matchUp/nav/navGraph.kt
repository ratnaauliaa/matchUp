package com.example.matchUp.nav

import OnboardingScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matchUp.auth.*
import com.example.matchUp.fdmatch.*
import com.example.matchUp.*

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    matchViewModel: MatchViewModel,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.padding(paddingValues)
    ) {
        // --- 1-5: Auth & Intro ---
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onFinished = {
                navController.navigate("home") { popUpTo("onboarding") { inclusive = true } }
            })
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email ->
                    val nameFromEmail = email.substringBefore("@")
                    matchViewModel.updateUserName(nameFromEmail)
                    matchViewModel.userEmail = email
                    matchViewModel.isLoggedIn = true

                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = { navController.navigate("forgot_password") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() },
                onSignInClick = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } }
            )
        }
        composable("forgot_password") {
            ForgotPW(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate("login") { popUpTo("forgot_password") { inclusive = true } } },
                onSignInClick = { navController.popBackStack() }
            )
        }

        // --- 6: Home ---
        composable("home") {
            MainContent(
                viewModel = matchViewModel,
                navController = navController,
                userName = matchViewModel.userName,
                isLoggedIn = matchViewModel.isLoggedIn,
                onProfileClick = {
                    // Jika mau Profile juga dikunci:
                    if (matchViewModel.isLoggedIn) {
                      
                    } else {
                        navController.navigate("login")
                    }
                },
                onNotificationsClick = {
                    navController.navigate("notifications")
                },
                onStartMatchClick = {
                    // CEK LOGIN SEBELUM SCAN
                    if (matchViewModel.isLoggedIn) {
                        navController.navigate("match_step1")
                    } else {
                        navController.navigate("login")
                    }
                },
                onUndertoneClick = {
                    // CEK LOGIN SEBELUM TEST UNDERTONE
                    if (matchViewModel.isLoggedIn) {
                        navController.navigate("undertone_test")
                    } else {
                        navController.navigate("login")
                    }
                },
                onInsightClick = {
                    navController.navigate("insights")
                }
            )
        }

        // --- ALUR MATCHUP ---
        composable(route = "match_step1") {
            BrandSelectionScreen(
                matchViewModel = matchViewModel,
                brandListFromDb = matchViewModel.allBrandList,
                onBack = { navController.popBackStack() },
                onNext = { brandName ->
                    matchViewModel.selectedBrandName = brandName
                    navController.navigate("match_step2")
                }
            )
        }

        composable("match_step2") {
            val brand = matchViewModel.selectedBrandName
            if (brand.isEmpty()) {
                LaunchedEffect(Unit) {
                    navController.navigate("match_step1") { popUpTo("match_step1") { inclusive = true } }
                }
            } else {
                val products = matchViewModel.getProductsByBrand(brand)
                ProductSelectionScreen(
                    selectedBrandName = brand,
                    productListFromDb = products,
                    onBack = { navController.popBackStack() },
                    onNext = { product ->
                        matchViewModel.selectedProduct = product
                        navController.navigate("match_step3")
                    }
                )
            }
        }

        composable("match_step3") {
            val currentProduct = matchViewModel.selectedProduct
            if (currentProduct != null) {
                ShadeSelectionScreen(
                    product = currentProduct,
                    viewModel = matchViewModel,
                    onBack = { navController.popBackStack() },
                    onAddAnother = {
                        navController.navigate("match_step1") {
                            popUpTo("match_step1") { inclusive = true }
                        }
                    },
                    onFindMatches = {
                        navController.navigate("match_result")
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("match_step1") { popUpTo("match_home") { inclusive = false } }
                }
            }
        }

        composable("match_result") {
            ResultScreen(
                viewModel = matchViewModel,
                onBack = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onAddMore = {
                    navController.navigate("match_step1")
                },
                onNavigateToDetail = { productName ->
                    navController.navigate("product_detail/$productName")
                }
            )
        }

        composable("wishlist") {
            WishlistScreen(
                viewModel = matchViewModel,
                navController = navController
            )
        }


        composable("product_detail/{productName}") { backStackEntry ->
            val productName = backStackEntry.arguments?.getString("productName") ?: ""
            ProductDetailScreen(
                productName = productName,
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("notifications") {
            NotificationScreen(onBack = { navController.popBackStack() })
        }

        // --- INSIGHTS ---
        composable("insights") {
            InsightsScreen(
                viewModel = matchViewModel,
                onNavigateToDetail = { articleId ->
                    navController.navigate("article_detail/$articleId")
                }
            )
        }


        // --- HISTORY ---
        composable("history") {
            HistoryScreen(
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { index ->
                    navController.navigate("history_detail/$index")
                }
            )
        }

        composable("history_detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull()
            if (index != null) {
                HistoryDetailScreen(
                    viewModel = matchViewModel,
                    historyIndex = index,
                    onBack = { navController.popBackStack() },
                    onNavigateToProductDetail = { productName ->
                        navController.navigate("product_detail/$productName")
                    }
                )
            }
        }

        // --- PROFILE ---
        composable("profile") {
            ProfileScreen(
                viewModel = matchViewModel,
                onLogout = {
                    // 1. Reset status login dan data user di ViewModel
                    matchViewModel.isLoggedIn = false
                    matchViewModel.updateUserName("Guest")

                    // 2. Bersihkan semua history dan pindah ke Login
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToEditProfile = { /* ... */ }
            )
        }

        // --- 7: UNDERTONE FEATURE ---
        composable("undertone_test") {
            UndertoneTestScreen(
                onBack = { navController.popBackStack() },
                onFinish = { result ->
                    matchViewModel.finalUndertone = result
                    navController.navigate("undertone_result")
                }
            )
        }

        composable("undertone_result") {
            val context = LocalContext.current

            // 1. Ambil data dari ViewModel
            val allResults = matchViewModel.loadUndertoneResultsFromJson(context)
            val userResult = matchViewModel.finalUndertone // Misal: "warm"

            // 2. Cari data yang cocok
            val resultData = allResults.find { it.undertone_id == userResult.lowercase() }

            if (resultData != null) {
                UndertoneResultScreen(
                    resultData = resultData,
                    onBackToHome = {
                        // Navigasi balik ke Home dan bersihkan backstack
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onStartMatchClick = {
                        navController.navigate("match_step1")
                    }
                )
            } else {
                // Jika data tidak ditemukan, balik ke home
                LaunchedEffect(Unit) {
                    navController.navigate("home")
                }
            }
        }
    }
}