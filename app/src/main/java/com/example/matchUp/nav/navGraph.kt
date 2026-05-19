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
import com.example.matchUp.lipsmatch.BrandLipsSelectionScreen
import com.example.matchUp.lipsmatch.LipsProductDetailScreen
import com.example.matchUp.lipsmatch.LipsProductSelectionScreen
import com.example.matchUp.lipsmatch.LipsShadeSelectionScreen

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
                onNotificationClick = {
                    navController.navigate("notification")
                },
                onStartMatchClick = { // Ini tetap untuk Foundation Match
                    if (matchViewModel.isLoggedIn) {
                        matchViewModel.clearMatchSelection()
                        navController.navigate("match_step1")
                    } else {
                        navController.navigate("login")
                    }
                },
                // TAMBAHAN BARU: Aksi ketika tombol Lips Match di halaman Home diklik
                onLipsMatchClick = {
                    if (matchViewModel.isLoggedIn) {
                        matchViewModel.clearMatchSelection()
                        navController.navigate("lips_step1") // Pergi ke alur lips yang kita buat tadi
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

        composable("notification") {
            NotificationScreen(
                onBack = { navController.popBackStack() }
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
                LaunchedEffect(Unit) { navController.popBackStack("match_step1", false) }
            } else {
                ProductSelectionScreen(
                    selectedBrandName = brand,
                    // PERBAIKAN: Ganti menjadi getProductsByBrandAndCategory dan beri parameter "foundation"
                    productListFromDb = matchViewModel.getProductsByBrandAndCategory(brand, "foundation"),
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

        // --- 4. PENINGKATAN: ALUR LIPS MATCH (LIPS MATCHING) ---
        composable("lips_step1") {
            // Menggunakan BrandSelectionScreen yang sama agar hemat kode (Reusability)
            BrandLipsSelectionScreen(
                matchViewModel = matchViewModel,
                brandListFromDb = matchViewModel.allBrandList,
                onBack = { navController.popBackStack() },
                onNext = { brandName ->
                    matchViewModel.selectedBrandName = brandName
                    navController.navigate("lips_step2")
                }
            )
        }

        composable("lips_step2") {
            val brand = matchViewModel.selectedBrandName
            if (brand.isEmpty()) {
                LaunchedEffect(Unit) { navController.popBackStack("lips_step1", false) }
            } else {
                // Menggunakan ProductSelectionScreen tapi data produk difilter khusus "lips"
                LipsProductSelectionScreen(
                    selectedBrandName = brand,
                    productListFromDb = matchViewModel.getProductsByBrandAndCategory(brand, "lips"),
                    onBack = { navController.popBackStack() },
                    onNext = { product ->
                        matchViewModel.selectedProduct = product
                        navController.navigate("lips_step3")
                    }
                )
            }
        }

        composable("lips_step3") {
            val currentProduct = matchViewModel.selectedProduct
            if (currentProduct != null) {
                // Menggunakan ShadeSelectionScreen bawaan
                LipsShadeSelectionScreen(
                    product = currentProduct,
                    viewModel = matchViewModel,
                    onBack = { navController.popBackStack() },
                    onAddAnother = {
                        navController.navigate("lips_step1") {
                            popUpTo("lips_step1") { inclusive = true }
                        }
                    },
                    onFindMatches = { navController.navigate("lips_result") }
                )
            } else {
                LaunchedEffect(Unit) { navController.popBackStack("lips_step1", false) }
            }
        }

        composable("lips_result") {
            // Memanggil ResultScreen milik package lipsmatch yang sudah difilter kategori lips
            com.example.matchUp.lipsmatch.LipsResultScreen(
                viewModel = matchViewModel,
                onBack = {
                    navController.navigate("home") { popUpTo("home") { inclusive = true } }
                },
                onAddMore = { navController.navigate("lips_step1") },
                onNavigateToDetail = { productName ->
                    navController.navigate("lips_product_detail/$productName")
                }
            )
        }

        // --- 4. DETAILS & FEATURES ---
        // --- UPDATE RUTE DETAIL FOUNDATION JUGA AGAR AMAN ---
        composable("product_detail/{productPath}") { backStackEntry ->
            val productPath = backStackEntry.arguments?.getString("productPath") ?: ""

            val pName: String
            val sName: String?

            if (productPath.contains("|")) {
                val parts = productPath.split("|")
                pName = parts[0]
                sName = if (parts.size > 1) parts[1] else null
            } else {
                pName = productPath
                sName = null
            }

            ProductDetailScreen(
                productName = pName,
                initialShadeName = sName,
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- UPDATE RUTE DETAIL LIPS DI SETUPNAVGRAPH ---
        composable("lips_product_detail/{productPath}") { backStackEntry ->
            val productPath = backStackEntry.arguments?.getString("productPath") ?: ""

            // Ambil data nama produk dan nama shade secara aman
            val pName: String
            val sName: String?

            if (productPath.contains("|")) {
                // Jika dikirim dari Rekomendasi/Alur Match (ada tanda |)
                val parts = productPath.split("|")
                pName = parts[0]
                sName = if (parts.size > 1) parts[1] else null
            } else {
                // Jika dikirim dari Wishlist (hanya nama produk saja)
                pName = productPath
                sName = null
            }

            // Panggil Screen Detail Lips dengan parameter yang sudah disaring aman
            LipsProductDetailScreen(
                productName = pName,
                initialShadeName = sName,
                viewModel = matchViewModel,
                onBack = { navController.popBackStack() }
            )
        }


        composable("insights") {
            InsightsScreen(
                viewModel = matchViewModel,
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
                    // PERBAIKAN: Arahkan ke history_detail dengan membawa indeksnya
                    navController.navigate("history_detail/$index")
                },
                onNavigateToLipsDetail = { index ->
                    // PERBAIKAN: Lips history juga diarahkan ke rute detail yang sama karena di dalam historyDetails.kt kodenya sudah dinamis mengenali kategori lips
                    navController.navigate("history_detail/$index")
                }
            )
        }

        // --- PERIKSA DAN PERBAIKI BAGIAN RUTE FOUNDATION INI DI SETUPNAVGRAPH.KT ---
        composable("product_list/{brandName}") { backStackEntry ->
            val brandName = backStackEntry.arguments?.getString("brandName") ?: ""

            ProductListScreen(
                brandName = brandName,
                // PERBAIKAN: Samakan namanya menjadi productListFromDb
                productListFromDb = matchViewModel.getProductsByBrandAndCategory(brandName, "foundation"),
                onProductSelected = { product ->
                    matchViewModel.selectedProduct = product
                    navController.navigate("shade_list")
                },
                onClose = { navController.popBackStack() }
            )
        }


        composable("history_detail/{historyIndex}") { backStackEntry ->
            val indexStr = backStackEntry.arguments?.getString("historyIndex")
            val index = indexStr?.toIntOrNull() ?: 0

            HistoryDetailScreen(
                viewModel = matchViewModel,
                historyIndex = index,
                onBack = { navController.popBackStack() },
                // 1. Jika yang diklik adalah produk foundation, arahkan ke detail foundation
                onNavigateToProductDetail = { productName ->
                    // Pastikan rute "product_detail" ini sesuai dengan yang ada di NavGraph kamu
                    navController.navigate("product_detail/$productName")
                },
                // PERBAIKAN: Tambahkan callback ini agar item rekomendasi LIPS di dalam history bisa diklik!
                onNavigateToLipsProductDetail = { productName ->
                    // Diarahkan ke layar detail produk lips kamu
                    navController.navigate("lips_product_detail/$productName")
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

                )
            }
        }
    }
}