package com.example.matchUp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.nav.SetupNavGraph // Import ini pastikan benar
import com.example.matchUp.ui.theme.MyApplication1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication1Theme {
                val navController = rememberNavController()

                // ViewModel diinisialisasi di sini agar datanya bertahan selama aplikasi hidup
                val matchViewModel: MatchViewModel = viewModel()
                val context = LocalContext.current

                // Load data JSON hanya satu kali saat aplikasi pertama jalan
                LaunchedEffect(Unit) {
                    matchViewModel.loadData(context)
                }

                // Memanggil SetupNavGraph yang ada di package com.example.matchUp.nav
                SetupNavGraph(
                    navController = navController,
                    matchViewModel = matchViewModel
                )
            }
        }
    }
}