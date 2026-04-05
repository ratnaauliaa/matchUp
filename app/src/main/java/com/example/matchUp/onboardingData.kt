
package com.example.matchUp
import androidx.annotation.DrawableRes


// Model data untuk setiap halaman
data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val buttonText: String = "Next"
)

// Daftar konten untuk 3 halaman onboarding sesuai gambar Anda
val onboardingPages = listOf(
    OnboardingPage(
        title = "Discover Your Undertone",
        description = "Say goodbye to shade confusion,\nstart with your undertone.",
        imageRes = R.drawable.onbo_1
    ),
    OnboardingPage(
        title = "Find Your Perfect Shade",
        description = "Start with your shade,\nmatch across brands.",
        imageRes = R.drawable.onbo_2
    ),
    OnboardingPage(
        title = "No More Trial And Error",
        description = "Save time and skip the trial-and-error",
        imageRes = R.drawable.onbo_3,
        buttonText = "Get Started"
    )
)