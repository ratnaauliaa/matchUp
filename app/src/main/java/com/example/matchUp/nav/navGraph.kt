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
        // --- 1. AUTH & INTRO ---
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("onboarding") {
            OnboardingScreen(onFinished = {
                navController.navigate("login") {
                    // Ini benar, agar dari login tidak bisa balik ke onboarding saat baru buka aplikasi
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = { email ->
                    val nameFromEmail = email.substringBefore("@")
                    matchViewModel.updateUserName(nameFromEmail)
                    matchViewModel.userEmail = email
                    matchViewModel.isLoggedIn = true

                    // Ubah rute ke "undertone_test" agar sinkron dengan NavHost kamu
                    navController.navigate("undertone_test") {
                        // Hapus halaman login dari stack agar user tidak bisa back ke login
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
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
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("forgot_password") {
            ForgotPW(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate("login") { popUpTo("forgot_password") { inclusive = true } } },
                onSignInClick = { navController.popBackStack() }
            )
        }

        // --- 2. HOME ---
        composable("home") {
            MainContent(
                viewModel = matchViewModel,
                navController = navController,
                userName = matchViewModel.userName,
                isLoggedIn = matchViewModel.isLoggedIn,
                onProfileClick = {
                    if (matchViewModel.isLoggedIn) {
                        navController.navigate("profile")
                    } else {
                        navController.navigate("login")
                    }
                },
                onNotificationsClick = { navController.navigate("notifications") },
                onStartMatchClick = {
                    if (matchViewModel.isLoggedIn) {
                        navController.navigate("match_step1")
                    } else {
                        navController.navigate("login")
                    }
                },
                onUndertoneClick = {
                    if (matchViewModel.isLoggedIn) {
                        navController.navigate("undertone_test")
                    } else {
                        navController.navigate("login")
                    }
                },
                onInsightClick = { navController.navigate("insights") }
            )
        }

        // --- 3. ALUR MATCHUP (FOUNDATION MATCHING) ---
        composable("match_step1") {
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
                // Safety check: jika brand kosong (backstack issue), balik ke step 1
                LaunchedEffect(Unit) { navController.popBackStack("match_step1", false) }
            } else {
                ProductSelectionScreen(
                    selectedBrandName = brand,
                    productListFromDb = matchViewModel.getProductsByBrand(brand),
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
                    onFindMatches = { navController.navigate("match_result") }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack("match_step1", false) }
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
                onAddMore = { navController.navigate("match_step1") },
                onNavigateToDetail = { productName ->
                    navController.navigate("product_detail/$productName")
                }
            )
        }

        // --- 4. DETAILS & FEATURES ---
        composable("product_detail/{productPath}") { backStackEntry ->
            val productPath = backStackEntry.arguments?.getString("productPath") ?: ""

            // Pisahkan nama produk dan nama shade
            val parts = productPath.split("|")
            val pName = parts[0]
            val sName = if (parts.size > 1) parts[1] else null

            ProductDetailScreen(
                productName = pName,
                initialShadeName = sName, // Kirim ke Screen
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() }
            )
        }


        composable("insights") {
            InsightsScreen(
                viewModel = matchViewModel,
                onNavigateToDetail = { articleId ->
                    navController.navigate("article_detail/$articleId")
                }
            )
        }

        composable("wishlist") {
            WishlistScreen(
                viewModel = matchViewModel,
                navController = navController
            )
        }


        composable("history") {
            HistoryScreen(
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { index ->
                    navController.navigate("history_detail/$index")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                viewModel = matchViewModel,
                onLogout = {
                    matchViewModel.isLoggedIn = false
                    matchViewModel.updateUserName("Guest")

                    // 1. Arahkan ke Onboarding dulu (tapi jangan tampilkan, cuma buat naruh di stack)
                    navController.navigate("onboarding") {
                        popUpTo(0) { inclusive = true } // Bersihkan total
                    }

                    // 2. Baru tumpuk dengan Login
                    navController.navigate("login") {
                        // Jangan pakai popUpTo di sini agar "onboarding" tetap ada di bawah "login"
                        launchSingleTop = true
                    }
                },
                onNavigateToHistory = {
                    navController.navigate("history")
                },
                onNavigateToEditProfile = { }
            )
        }

        // --- 5. UNDERTONE FEATURE ---
        composable("undertone_test") {
            UndertoneTestScreen(
                onBack = {
                    // Jika ingin memastikan kembali ke Home dengan bersih:
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onFinish = { result ->
                    matchViewModel.finalUndertone = result

                    // PENTING: Hapus rekomendasi lama supaya aplikasi mau mengacak ulang untuk undertone baru
                    matchViewModel.resetRecommendations()

                    navController.navigate("undertone_result") {
                        // Menghapus halaman test dari history agar tidak bisa di-back dari halaman result
                        popUpTo("undertone_test") { inclusive = true }
                    }
                }
            )
        }

        // Di dalam SetupNavGraph -> navGraph.kt
        composable("undertone_result") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                matchViewModel.loadUndertoneResultsFromJson(context)
            }

            val userResult = matchViewModel.finalUndertone
            val resultData = matchViewModel.undertoneResults.find {
                it.undertone_id.equals(userResult, ignoreCase = true)
            }

            if (resultData != null) {
                UndertoneResultScreen(
                    resultData = resultData,
                    onBackToHome = {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onStartMatchClick = {
                        navController.navigate("match_step1")
                    }
                    // HAPUS viewModel dan onNavigateToDetail dari sini
                    // karena di file undertoneResult.kt kamu tidak ada parameter itu
                )
            }
        }
    }
}