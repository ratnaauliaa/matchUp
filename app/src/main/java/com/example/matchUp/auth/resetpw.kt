package com.example.matchUp.auth

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.CustomSmallTextField
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State for error handling and loading
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                modifier = Modifier.size(16.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Reset Password",
            fontSize = 24.sp,
            fontFamily = MyCustomFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = "Please type something you'll remember.",
            fontSize = 14.sp,
            color = Color.Gray,
            fontFamily = MyCustomFontFamily,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // New Password Input
        CustomSmallTextField(
            label = "New Password",
            value = newPassword,
            onValueChange = {
                newPassword = it
                errorMessage = null
            },
            placeholder = "Enter your new password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Confirm Password Input
        CustomSmallTextField(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = null
            },
            placeholder = "Confirm your new password",
            isPassword = true
        )

        // Error Message Box with Fixed Height (Prevents layout jumping)
        Box(modifier = Modifier.fillMaxWidth().height(40.dp).padding(top = 8.dp)) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = MyCustomFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Password Button
        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser

                when {
                    newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                        errorMessage = "Please fill in all fields."
                    }
                    newPassword.length < 8 -> {
                        errorMessage = "Password must be at least 8 characters."
                    }
                    newPassword != confirmPassword -> {
                        errorMessage = "Passwords do not match."
                    }
                    else -> {
                        isLoading = true
                        user?.updatePassword(newPassword)
                            ?.addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    onComplete()
                                } else {
                                    val error = task.exception?.message ?: ""
                                    // Logic for checking if new password is same as old
                                    if (error.contains("same as the old password", ignoreCase = true)) {
                                        errorMessage = "New password cannot be the same as the old one."
                                    } else {
                                        errorMessage = "Failed to reset password. Please re-authenticate."
                                    }
                                }
                            }
                    }
                }
            },
            modifier = Modifier
                .width(200.dp)
                .height(45.dp)
                .align(Alignment.CenterHorizontally),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
            } else {
                Text(
                    text = "Reset Password",
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPreview() {
    // Isi parameter dengan kurung kurawal kosong {} agar tidak error
    ResetPasswordScreen(
        onBack = { /* tidak melakukan apa-apa di preview */ },
        onComplete = { /* tidak melakukan apa-apa di preview */ }
    )
}