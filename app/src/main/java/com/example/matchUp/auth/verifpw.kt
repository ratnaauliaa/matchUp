package com.example.matchUp.auth

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun VerifyCodeScreen(
    email: String,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    // --- LOGIKA TIMER ---
    var timeLeft by remember { mutableStateOf(240) }
    LaunchedEffect(key1 = Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft -= 1
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerText = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

    // --- LOGIKA OTP & FOCUS ---
    var otpCode by remember { mutableStateOf(listOf("", "", "", "")) }
    // Membuat 4 FocusRequester untuk masing-masing kotak
    val focusRequesters = remember { List(4) { FocusRequester() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Check your email", fontSize = 24.sp, fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Bold)

        Text(
            text = buildAnnotatedString {
                append("We've sent a code to ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily)) {
                    append(email) // Email terpanggil di sini
                }
            },
            fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Row Input OTP dengan Auto-Focus
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            otpCode.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            val newList = otpCode.toMutableList()
                            newList[index] = newValue
                            otpCode = newList

                            // LOGIKA AUTO-FOCUS:
                            if (newValue.isNotEmpty()) {
                                // Jika kotak diisi, pindah ke kotak berikutnya (jika bukan kotak terakhir)
                                if (index < 3) focusRequesters[index + 1].requestFocus()
                            } else {
                                // Jika kotak dihapus (backspace), pindah ke kotak sebelumnya
                                if (index > 0) focusRequesters[index - 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(58.dp, 65.dp)
                        .focusRequester(focusRequesters[index]), // Pasang FocusRequester
                    shape = RoundedCornerShape(16.dp),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color(0xFFFFD1E3)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Timer Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Resend code ", fontSize = 14.sp, fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Bold)
            Text(timerText, fontSize = 14.sp, color = if (timeLeft > 0) Color.Gray else Color(0xFFFFD1E3))
        }

        Spacer(modifier = Modifier.height(48.dp)) // Menggunakan weight agar tombol di bawah fleksibel

        Button(
            onClick = onNext,
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3))
        ) {
            Text("Verify", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyPreview() {
    VerifyCodeScreen(
        email = "bulan@gmail.com",
        onBack = { },
        onNext = { }
    )
}