package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun UndertoneTestScreen(onBack: () -> Unit, onFinish: (String) -> Unit) {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 5

    // Variabel Penampung Skor (Gunakan remember agar tidak reset saat recompose)
    var warmScore by remember { mutableStateOf(0) }
    var coolScore by remember { mutableStateOf(0) }
    var neutralScore by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // --- TOP BAR CUSTOM ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    if (currentStep > 1) {
                        currentStep -= 1 // Mundur ke step sebelumnya
                    } else {
                        onBack() // Keluar ke Home
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Row {
                repeat(totalSteps) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(width = 30.dp, height = 4.dp)
                            .background(
                                if (index + 1 <= currentStep) Color.Black else Color.LightGray,
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            Text("Step $currentStep of $totalSteps", fontSize = 10.sp, fontFamily = MyCustomFontFamily, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(30.dp))

        // --- CONTENT BERDASARKAN STEP ---
        when (currentStep) {
            1 -> TestStepContent(
                title = "Vein Test",
                desc = "Look at your wrist veins under natural light",
                question = "What color are your veins?",
                options = listOf("Blue", "Green", "Bluish - Green", "Purple", "I don't know"),
                onOptionSelected = { selected ->
                    when(selected) {
                        "Green" -> warmScore += 2
                        "Blue", "Purple" -> coolScore += 2
                        "Bluish - Green" -> neutralScore += 2
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
                options = listOf("Fair", "Light", "Medium", "Olive", "Dark", "Deep"),
                onOptionSelected = {
                    // Langsung Hitung Hasil Akhir di step terakhir
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
    Column(modifier = Modifier.fillMaxSize()) {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = MyCustomFontFamily)
        Text(desc, fontSize = 12.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)

        Spacer(modifier = Modifier.height(24.dp))
        Text(question, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black, fontFamily = MyCustomFontFamily)
        Spacer(modifier = Modifier.height(16.dp))

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOptionSelected(option) } // Langsung pindah step
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = false, // Tidak perlu state selected karena langsung pindah
                    onClick = { onOptionSelected(option) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black)
                )
                Text(
                    text = option,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}