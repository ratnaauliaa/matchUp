package com.example.matchUp

import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.ui.theme.MyCustomFontFamily

// --- Model Data  ---
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
    resultData: UndertoneResult,
    onBackToHome: () -> Unit,
    onStartMatchClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Back Kotak Melengkung
            IconButton(
                onClick = onBackToHome,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = "Test Result",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily,
                color = Color.Black
            )

            // Tombol Share Fungsional
            IconButton(onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "I just found out my undertone is ${resultData.undertone_id.uppercase()}! ✨\nCheck yours on MatchUp App!"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share via"))
            }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Scroll dipindah ke sini
        ) {

            // --- IMAGE BANNER ---
            Image(
                painter = painterResource(id = R.drawable.undertone_guide),
                contentDescription = "Undertone Guide",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- RESULT TITLE WITH INDICATOR ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            color = if (resultData.undertone_id.lowercase() == "warm") Color(
                                0xFFE9573F
                            ) else Color(0xFF5D9CEC),
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "You have ${resultData.undertone_id.lowercase()} undertones!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyCustomFontFamily,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = resultData.description,
                fontSize = 15.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- RECOMMENDATION SECTION ---
            Text(
                text = "Perfect for you",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = MyCustomFontFamily
            )

            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = resultData.recommendations.foundations,
                fontSize = 13.sp,
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = resultData.recommendations.concealers,
                fontSize = 13.sp,
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = resultData.recommendations.lipsticks,
                fontSize = 13.sp,
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFF0F0F0),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(10.dp))


            // --- AVOID SECTION ---
            Text(
                text = "Avoid",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F),
                fontFamily = MyCustomFontFamily
            )
            resultData.avoid.forEach { item ->
                Text(
                    text = "$item",
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- ACTION BUTTON ---
            Button(
                onClick = onBackToHome,
                modifier = Modifier
                    .width(200.dp)
                    .height(45.dp)
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "back to Home",
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyCustomFontFamily
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

