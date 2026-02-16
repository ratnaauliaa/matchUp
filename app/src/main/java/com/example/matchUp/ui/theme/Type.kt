package com.example.matchUp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.matchUp.R

// 1. DAFTARKAN SEMUA FONT KAMU DI SINI
val MyCustomFontFamily = FontFamily(
    Font(R.font.light_font, FontWeight.Light),
    Font(R.font.regular_font, FontWeight.Normal),
    Font(R.font.medium_font, FontWeight.Medium),
    Font(R.font.semibold_font, FontWeight.SemiBold),
    Font(R.font.bold_font, FontWeight.Bold)
)

// 2. ATUR TYPOGRAPHY STANDAR
val AppTypography = Typography( // Ganti nama variabel jadi AppTypography
    titleLarge = TextStyle(
        fontFamily = MyCustomFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp
    ),

    // MEDIUM - Untuk Sub-judul atau Nama Field
    titleMedium = TextStyle(
        fontFamily = MyCustomFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),

    // REGULAR - Untuk Isi Tulisan Standar
    bodyLarge = TextStyle(
        fontFamily = MyCustomFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    // LIGHT - Kita masukkan ke bodySmall agar bisa dipanggil sistem
    bodySmall = TextStyle(
        fontFamily = MyCustomFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),

    // SEMIBOLD - Untuk Label Kecil tapi Tegas
    labelSmall = TextStyle(
        fontFamily = MyCustomFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
    )
)