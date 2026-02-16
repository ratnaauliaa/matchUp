package com.example.matchUp // Sesuaikan dengan package kamu

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationScreen(
onBack: () -> Unit = {} // Tambahkan default value {} agar Preview tidak error
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black
                )
            }

            Text(
                text = "Notification",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Spacer kosong agar judul tetap di tengah
            Spacer(modifier = Modifier.size(40.dp))
        }

        // --- EMPTY STATE CONTENT ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp), // Angkat sedikit ke atas agar tidak terlalu di bawah
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // GANTI NAMA FILE GAMBAR DISINI
            Image(
                painter = painterResource(id = R.drawable.ic_notif),
                contentDescription = null,
                modifier = Modifier.size(250.dp) // Sesuaikan ukuran gambar
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "No Notifications Yet",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "There are no notifications at the moment. All notifications we send will appear here!",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = MyCustomFontFamily,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNotification() {
    NotificationScreen(onBack = {})
}