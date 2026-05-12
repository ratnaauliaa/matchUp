package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun UndertoneTestScreen(onBack: () -> Unit, onFinish: (String) -> Unit) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 5

    var warmScore by remember { mutableStateOf(0) }
    var coolScore by remember { mutableStateOf(0) }
    var neutralScore by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // --- TOP BAR (Sesuai Gambar) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Progress Bar Segments
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (index + 1 <= currentStep) Color.Black else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Step $currentStep of $totalSteps",
                    fontSize = 12.sp,
                    fontFamily = MyCustomFontFamily,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Back Button
            IconButton(
                onClick = {
                    if (currentStep > 1) currentStep -= 1 else onBack()
                },
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- CONTENT SECTION ---
        when (currentStep) {
            1 -> TestStepContent(
                title = "Vein Test",
                desc = "Look at your wrist veins under natural light",
                question = "What color are your veins?",
                options = listOf("Blue", "Green", "Bluish - green", "Purple"),
                onOptionSelected = { selected ->
                    when(selected) {
                        "Green" -> warmScore += 2
                        "Blue", "Purple" -> coolScore += 2
                        "Bluish - green" -> neutralScore += 2
                    }
                    currentStep = 2
                }
            )
            2 -> TestStepContent(
                title = "White Fabric Test",
                desc = "Hold pure white fabric near your face",
                question = "How does your skin look?",
                options = listOf("Skin looks grayish/dull", "Skin looks yellowish", "Skin looks pinkish", "No strong tint"),
                onOptionSelected = { selected ->
                    when(selected) {
                        "Skin looks yellowish" -> warmScore += 1
                        "Skin looks pinkish" -> coolScore += 1
                        else -> neutralScore += 1
                    }
                    currentStep = 3
                }
            )
            3 -> TestStepContent(
                title = "Jewelry Test",
                desc = "Try metals and see which suits you",
                question = "What jewelry metal looks best on you?",
                options = listOf("Gold", "Silver", "Both Gold - Silver", "Rosegold"),
                onOptionSelected = { selected ->
                    when(selected) {
                        "Gold" -> warmScore += 1
                        "Silver" -> coolScore += 1
                        else -> neutralScore += 1
                    }
                    currentStep = 4
                }
            )
            4 -> TestStepContent(
                title = "Sun Reaction",
                desc = "Your skin's reaction under sunlight",
                question = "How does your skin tan/burn?",
                options = listOf("Burns easily, rarely tans", "Tans easily", "Burns, mostly tans", "Tans evenly"),
                onOptionSelected = { selected ->
                    when(selected) {
                        "Tans easily" -> warmScore += 1
                        "Burns easily, rarely tans" -> coolScore += 1
                        else -> neutralScore += 1
                    }
                    currentStep = 5
                }
            )
            5 -> TestStepContent(
                title = "Skintone",
                desc = "Your natural skin color without makeup",
                question = "How would you describe your skin tone?",
                options = listOf("Fair", "Light", "Medium", "Dark", "Deep"),
                onOptionSelected = {
                    val result = when {
                        warmScore > coolScore && warmScore > neutralScore -> "Warm"
                        coolScore > warmScore && coolScore > neutralScore -> "Cool"
                        else -> "Neutral"
                    }
                    onFinish(result)
                }
            )
        }
    }
}

@Composable
fun TestStepContent(
    title: String,
    desc: String,
    question: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    // State lokal untuk menampung pilihan yang baru saja diklik
    var selectedOption by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = MyCustomFontFamily)
        Text(desc, fontSize = 15.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)

        Spacer(modifier = Modifier.height(20.dp))
        Text(question, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, fontFamily = MyCustomFontFamily)
        Spacer(modifier = Modifier.height(16.dp))

        options.forEach { option ->
            val isCurrentSelection = selectedOption == option

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        selectedOption = option
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            onOptionSelected(option)
                            selectedOption = null // Reset untuk step berikutnya
                        }, 300)
                    },
                shape = RoundedCornerShape(30.dp),

                color = if (isCurrentSelection) Color(0xFFE8EAF6) else Color(0xFFF5F5F5),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isCurrentSelection) Color(0xFFE8EAF6) else Color(0xFFEEEEEE)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = option,
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = MyCustomFontFamily,
                        modifier = Modifier.weight(1f)
                    )

                    // Custom Radio Button
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = if (isCurrentSelection) Color.Black else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = if (isCurrentSelection) 0.dp else 1.5.dp,
                                color = if (isCurrentSelection) Color.Black else Color.LightGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Titik putih kecil di tengah kalau terpilih
                        if (isCurrentSelection) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.White, CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}