package com.example.matchUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    // --- TAMBAHKAN LOGIKA DURASI DI SINI ---
    LaunchedEffect(key1 = true) {
        delay(3000) // Durasi 3000 milidetik = 3 detik
        onTimeout() // Panggil fungsi untuk pindah halaman
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_matchup),
            contentDescription = "MatchUp Logo",
            modifier = Modifier.fillMaxSize(0.6f)
        )
    }
}

// Untuk Preview, kita beri lambda kosong saja
@Preview(showBackground = true)
@Composable
fun PreviewSplash() {
    SplashScreen(onTimeout = {})
}