package com.example.matchUp

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onForgotPassword: () -> Unit,
    onRegisterClick: () -> Unit,
    onBack: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Logic: Pesan error hilang otomatis setelah 3 detik (biar seragam)
    if (errorMessage.isNotEmpty()) {
        LaunchedEffect(errorMessage) {
            delay(3000)
            errorMessage = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Hi, Welcome!",
            fontSize = 24.sp,
            fontFamily = MyCustomFontFamily,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Good to see you again! Please enter your details.",
            fontSize = 15.sp,
            color = Color.Gray,
            fontFamily = MyCustomFontFamily
        )

        Spacer(modifier = Modifier.height(40.dp))

        CustomSmallTextField(
            label = "Email",
            value = email,
            onValueChange = {
                email = it
                if (errorMessage.isNotEmpty()) errorMessage = "" // Hapus error saat ngetik
            },
            placeholder = "example@gmail.com",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomSmallTextField(
            label = "Password",
            value = password,
            onValueChange = {
                password = it
                if (errorMessage.isNotEmpty()) errorMessage = "" // Hapus error saat ngetik
            },
            placeholder = "enter your password",
            isPassword = true
        )

        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp) // Sedikit padding agar tidak terlalu mepet field password
        ) {
            Text(
                text = "Forgot password?",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily
            )
        }

        // PERBAIKAN DISINI: Feedback Error (Gaya ForgotPW - rata kiri/Start)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(start = 4.dp), // Beri sedikit space agar sejajar teks field
            contentAlignment = Alignment.Center
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = MyCustomFontFamily,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                isLoading = true
                authViewModel.loginUser(
                    email = email.trim(),
                    pass = password,
                    onSuccess = {
                        isLoading = false
                        onLoginSuccess(email)
                    },
                    onError = { firebaseError ->
                        isLoading = false
                        errorMessage = when {
                            firebaseError.contains("all_empty") -> "Please fill in all details."
                            firebaseError.contains("email_empty") -> "Please enter your email."
                            firebaseError.contains("pass_empty") -> "Please enter your password."
                            firebaseError.contains("credential") || firebaseError.contains("password") ->
                                "Invalid email or password."
                            firebaseError.contains("user-not-found") ->
                                "No account found with this email."
                            firebaseError.contains("network") ->
                                "Network error. Check your connection."
                            else -> "Authentication failed."
                        }
                    }
                )
            },
            modifier = Modifier
                .width(200.dp)
                .height(45.dp)
                .align(Alignment.CenterHorizontally),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
            shape = RoundedCornerShape(20.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Sign In",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyCustomFontFamily,
                    fontSize = 15.sp
                )
            }
        }


        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Or continue with", fontFamily = MyCustomFontFamily, modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SocialCircleButton(imageResId = R.drawable.google)
            Spacer(modifier = Modifier.width(16.dp))
            SocialCircleButton(imageResId = R.drawable.fb)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily
            )
            Text(
                text = "Sign Up",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}