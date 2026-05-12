package com.example.matchUp

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay



@Composable
fun RegisterScreen(
    viewModel: com.example.matchUp.fdmatch.MatchViewModel,
    onBack: () -> Unit,
    onSignInClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    if (errorMessage.isNotEmpty()) {
        LaunchedEffect(errorMessage) {
            delay(3000)
            errorMessage = ""
        }
    }

    val webClientId = "109685944554-hguaf4pmo075sqdl2omkm7p0e80qu56k.apps.googleusercontent.com"
    val scope = rememberCoroutineScope()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email ?: ""
            if (email.isNotEmpty()) {
                onRegisterSuccess()
            }
        } catch (e: ApiException) {
        }
    }

    // --- PERBAIKAN TOMBOL BACK (START) ---
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. Area Konten yang bisa di-scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Beri Spacer tinggi di awal agar konten tidak tertutup tombol back yang diam di atas
            Spacer(modifier = Modifier.height(100.dp))

            Text(text = "Create account", fontFamily = MyCustomFontFamily, fontSize = 24.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Text(
                text = "Please enter your valid data to create an account.",
                fontSize = 15.sp,
                fontFamily = MyCustomFontFamily,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- INPUT FIELDS ---
            CustomSmallTextField(
                label = "Name",
                value = name,
                onValueChange = {
                    name = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                placeholder = "your full name"
            )
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
            CustomSmallTextField(
                label = "Create a password",
                value = password,
                onValueChange = {
                    password = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                placeholder = "must be 8 characters",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomSmallTextField(
                label = "Confirm password",
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (errorMessage.isNotEmpty()) errorMessage = ""
                },
                placeholder = "re-type password",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                        fontFamily = MyCustomFontFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    when {
                        name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                            errorMessage = "Please fill in all details."
                        }
                        confirmPassword.isEmpty() -> {
                            errorMessage = "Please confirm your password."
                        }

                        password != confirmPassword -> {
                            errorMessage = "Passwords do not match."
                        }
                        !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                            errorMessage = "Please enter a valid email address."
                        }
                        name.length < 3 -> {
                            errorMessage = "Name must be at least 3 characters."
                        }
                        password.length < 8 -> {
                            errorMessage = "Password must be at least 8 characters."
                        }
                        else -> {
                            isLoading = true
                            authViewModel.registerUser(
                                email = email.trim(),
                                pass = password,
                                name = name,
                                onSuccess = {
                                    isLoading = false
                                    viewModel.updateUserName(name)
                                    Toast.makeText(context, "Registered Successfully!", Toast.LENGTH_LONG).show()
                                    onRegisterSuccess()
                                },
                                onError = { firebaseError ->
                                    isLoading = false
                                    errorMessage = when {
                                        firebaseError.contains("already") || firebaseError.contains("in use") ->
                                            "This email is already registered."
                                        firebaseError.contains("invalid-email") ->
                                            "Invalid email format."
                                        firebaseError.contains("network") ->
                                            "Network error. Check your connection."
                                        else -> "Registration failed. Please try again."
                                    }
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(42.dp)
                    .align(Alignment.CenterHorizontally),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD1E3)),
                shape = RoundedCornerShape(20.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                } else {
                    Text(text = "Sign Up", color = Color.Black, fontSize = 15.sp, fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Or continue with", fontFamily = MyCustomFontFamily, modifier = Modifier.align(Alignment.CenterHorizontally), fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialCircleButton(
                    imageResId = R.drawable.ic_google,
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                SocialCircleButton(
                    imageResId = R.drawable.ic_mail,
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                SocialCircleButton(
                    imageResId = R.drawable.ic_fb,
                    onClick = { /* Handle Facebook */ }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Already have an account? ", fontSize = 13.sp, fontFamily = MyCustomFontFamily, color = Color.Gray)
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

        // 2. Tombol Back (Sticky di lapisan atas)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White) // Menutupi konten scroll saat lewat di bawahnya
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSmallTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontFamily = MyCustomFontFamily, fontSize = 13.sp, color = if (isError) Color.Red else Color.Black)

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(42.dp),
            interactionSource = interactionSource,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = MyCustomFontFamily, // <--- TAMBAHKAN INI agar ketikan user juga pakai font custom
                fontWeight = FontWeight.Medium, // Tambahkan agar teks ketikan lebih jelas
                textAlign = TextAlign.Start,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        ) { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = { Text(text = placeholder,fontFamily = MyCustomFontFamily, color = Color.LightGray, fontSize = 10.sp) },
                trailingIcon = {
                    if (isPassword) {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.size(30.dp)) {
                            Icon(imageVector = image, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                        }
                    }
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                container = {
                    OutlinedTextFieldDefaults.ContainerBox(
                        enabled = true,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(21.dp),
                        focusedBorderThickness = 1.dp,
                        unfocusedBorderThickness = 1.dp
                    )
                }
            )
        }
    }
}



