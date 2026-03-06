package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun ProfileScreen(
    viewModel: MatchViewModel,
    onLogout: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // --- HEADER: FOTO & NAMA ---
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = "https://ui-avatars.com/api/?name=${viewModel.userName}&background=FFD1E3&color=fff",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFD1E3),
                modifier = Modifier
                    .size(28.dp)
                    .offset(x = (-4).dp, y = (-4).dp)
                    .clickable { onNavigateToEditProfile() }
            ) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.padding(6.dp), tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = viewModel.userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = MyCustomFontFamily)
        Text(text = viewModel.userEmail, fontSize = 14.sp, color = Color.Gray, fontFamily = MyCustomFontFamily)

        Spacer(modifier = Modifier.height(32.dp))

        // --- MENU GROUP: MY ACCOUNT (SESUAI GAMBAR) ---
        ProfileSectionHeader("My Account")
        ProfileMenuItem(Icons.Default.Person, "Edit Profile", onClick = onNavigateToEditProfile)
        ProfileMenuItem(Icons.Default.Key, "Change Password")
        // TETEP ADA HISTORY DI SINI
        ProfileMenuItem(Icons.Default.History, "History", onClick = onNavigateToHistory)
        ProfileMenuItem(Icons.Default.Translate, "Languages")
        ProfileMenuItem(Icons.Default.Logout, "Sign Out", textColor = Color.Red, onClick = onLogout)

        Spacer(modifier = Modifier.height(24.dp))
        ProfileSectionHeader("App Info")
        ProfileMenuItem(Icons.Default.HelpOutline, "FAQ")
        ProfileMenuItem(Icons.Default.Info, "Privacy Policy")

        // --- FOOTER ---
        Spacer(modifier = Modifier.height(40.dp))
        Text("matchUp", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Version 1.4", fontSize = 10.sp, color = Color.Gray)
        Text("Created with beauty of love.", fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        fontSize = 12.sp,
        color = Color.Gray,
        fontFamily = MyCustomFontFamily
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    textColor: Color = Color.Black,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = if(textColor == Color.Red) Color.Red else Color.Black
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            color = textColor,
            fontFamily = MyCustomFontFamily
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = Color.LightGray
        )
    }
}