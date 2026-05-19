package com.example.matchUp.auth

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.CustomSmallTextField
import com.example.matchUp.R
import com.example.matchUp.SocialCircleButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun ForgotPW(
    onBack: () -> Unit,
    onNext: (String) -> Unit,
    onSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (errorMessage.isNotEmpty()) {
        LaunchedEffect(errorMessage) {
            delay(3000)
            errorMessage = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Back Button
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
                text = "Forgot Password",
                color = Color.Black,
                fontSize = 24.sp,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Enter your email to receive a reset link.",
                fontSize = 15.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Email Input
            CustomSmallTextField(
                label = "Email",
                value = email,
                onValueChange = {
                    email = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                placeholder = "example@gmail.com",
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Error Message Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(start = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = MyCustomFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Send Link Button
            Button(
                onClick = {
                    val cleanEmail = email.trim()
                    if (cleanEmail.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
                        isLoading = true
                        FirebaseAuth.getInstance().sendPasswordResetEmail(cleanEmail)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Reset link sent! Check your inbox.", Toast.LENGTH_LONG).show()
                                    onNext(cleanEmail)
                                } else {
                                    val errorMsg = task.exception?.message ?: ""
                                    errorMessage = when {
                                        errorMsg.contains("no user", ignoreCase = true) ||
                                                errorMsg.contains("user-not-found") -> "This email is not registered."
                                        errorMsg.contains("invalid", ignoreCase = true) -> "Invalid email format."
                                        errorMsg.contains("missing", ignoreCase = true) -> "Email is required!"
                                        errorMsg.contains("email", ignoreCase = true) -> "Invalid email format!"
                                        errorMsg.contains("timeout", ignoreCase = true) -> "Request timed out. Please try again."
                                        errorMsg.contains("too many", ignoreCase = true) -> "Too many requests. Try again later."
                                        errorMsg.contains("network", ignoreCase = true) -> "Network error. Check your connection."
                                        else -> "Failed to send reset link. Please try again."
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                e.localizedMessage?.let {
                                    errorMessage = when {
                                        else -> "Failed: System busy."
                                    }
                                }
                            }
                    } else if (cleanEmail.isEmpty()) {
                        errorMessage = "Email is required!"
                    } else {
                        errorMessage = "Invalid email format!"
                    }
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
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Text("Send link", color = Color.Black,fontSize = 15.sp, fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontFamily = MyCustomFontFamily,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Sign In",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyCustomFontFamily,
                    color = Color.Black,
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPreview() {
    ForgotPW(
        onBack = {},
        onNext = {},
        onSignInClick = {}
    )
}