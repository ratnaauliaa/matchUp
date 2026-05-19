
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
        title = "Confused About Your Shade?",
        description = "Find shades that truly match your skin tone with confidence.",
        imageRes = R.drawable.onbo1,
    ),
    OnboardingPage(
        title = "Find Your Perfect Shade",
        description = "Start with your shade,\nmatch across brands.",
        imageRes = R.drawable.onbo2
    ),
    OnboardingPage(
        title = "Discover Your Undertone",
        description = "Say goodbye to shade confusion,\nstart with your undertone.",
        imageRes = R.drawable.onbo3,
        buttonText = "Get Started"
    )
)