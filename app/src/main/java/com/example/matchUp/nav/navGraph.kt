package com.example.matchUp.nav

import OnboardingScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matchUp.auth.*
import com.example.matchUp.fdmatch.*
import com.example.matchUp.*

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    matchViewModel: MatchViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // --- 1-5: Auth & Intro ---
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } }
            })
        }
        composable("onboarding") {
            OnboardingScreen(onFinished = {
                navController.navigate("login") { popUpTo("onboarding") { inclusive = true } }
            })
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("home") { popUpTo("login") { inclusive = true } } },
                onForgotPassword = { navController.navigate("forgot_password") },
                onRegisterClick = { navController.navigate("register") },
                onBack = { navController.popBackStack() }
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
                userName = matchViewModel.userName,
                onProfileClick = { /* Navigasi Profil */ },
                onNotificationsClick = { navController.navigate("notifications") },
                onStartMatchClick = { navController.navigate("match_step1") }
            )
        }

        // --- ALUR MATCHUP (FOUNDATION) ---

        // STEP 1: Pilih Brand
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

        // STEP 2: Pilih Produk
        composable("match_step2") {
            val brand = matchViewModel.selectedBrandName

            // 1. Jika brand kosong (misal app restart), jangan lanjut, balik ke step 1
            if (brand.isEmpty()) {
                LaunchedEffect(Unit) {
                    navController.navigate("match_step1") { popUpTo("match_step1") { inclusive = true } }
                }
            } else {
                // 2. Ambil produk
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

        // STEP 3: Pilih Shade
        composable("match_step3") {
            val currentProduct = matchViewModel.selectedProduct

            if (currentProduct != null) {
                ShadeSelectionScreen(
                    product = currentProduct,
                    viewModel = matchViewModel,
                    onBack = { navController.popBackStack() },
                    onAddAnother = {
                        // HAPUS pemanggilan addMatch di sini karena sudah dipanggil
                        // di dalam ShadeSelectionScreen saat user klik warna.
                        navController.navigate("match_step1") {
                            popUpTo("match_step1") { inclusive = true }
                        }
                    },
                    onFindMatches = {
                        // Cukup navigasi saja ke halaman hasil
                        navController.navigate("match_result")
                    }
                )
            } else {
                // Balik ke awal jika produk null (misal terjadi proses kill process)
                LaunchedEffect(Unit) {
                    navController.navigate("match_step1") {
                        popUpTo("match_home") { inclusive = false }
                    }
                }
            }
        }
        composable("match_result") {
            ResultScreen(
                viewModel = matchViewModel,
                onBack = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onAddMore = { navController.navigate("match_step1") }
            )
        }

        composable("notifications") {
            NotificationScreen(onBack = { navController.popBackStack() })
        }
    }
}