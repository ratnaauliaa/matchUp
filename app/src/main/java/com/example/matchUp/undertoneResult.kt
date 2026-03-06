package com.example.matchUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.ui.theme.MyCustomFontFamily

// --- Model Data (Sesuaikan dengan struktur JSON-mu) ---
data class UndertoneResult(
    val undertone_id: String,
    val result_title: String,
    val description: String,
    val recommendations: Recommendations,
    val avoid: List<String>
)

data class Recommendations(
    val foundations: String,
    val concealers: String,
    val lipsticks: String
)

@Composable
fun UndertoneResultScreen(
    resultData: UndertoneResult, // Data diambil dari database/JSON
    onBackToHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // --- TITLE ---
        Text(
            text = "Test Result",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = MyCustomFontFamily,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- HASIL UNDERTONE (Contoh: WARM/COOL/NEUTRAL) ---
        // Sesuai lingkaran di atas desain image_249905.png
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFFFD1E3), RoundedCornerShape(60.dp))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = resultData.undertone_id.uppercase(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = MyCustomFontFamily
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- SUBTITLE & DESCRIPTION ---
        Text(
            text = resultData.result_title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = MyCustomFontFamily,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = resultData.description,
            fontSize = 14.sp,
            color = Color.Gray,
            fontFamily = MyCustomFontFamily,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- PERFECT FOR YOU SECTION ---
        Text(
            text = "Perfect for you",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = MyCustomFontFamily
        )
        Spacer(modifier = Modifier.height(10.dp))

        RecommendationItem("Foundations", resultData.recommendations.foundations)
        RecommendationItem("Concealers", resultData.recommendations.concealers)
        RecommendationItem("Blushes & Lipsticks", resultData.recommendations.lipsticks)

        Spacer(modifier = Modifier.height(20.dp))

        // --- AVOID SECTION ---
        Text(
            text = "Avoid",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            fontFamily = MyCustomFontFamily
        )
        Spacer(modifier = Modifier.height(10.dp))

        resultData.avoid.forEach { item ->
            Text(
                text = "• $item",
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // --- BUTTON ---
        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Find my shade",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun RecommendationItem(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            fontFamily = MyCustomFontFamily
        )
        Text(
            text = description,
            fontSize = 13.sp,
            color = Color.DarkGray,
            fontFamily = MyCustomFontFamily
        )
    }
}