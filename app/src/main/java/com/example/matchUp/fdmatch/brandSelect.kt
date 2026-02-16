package com.example.matchUp.fdmatch

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BrandSelectionScreen(
    matchViewModel: MatchViewModel,
    brandListFromDb: List<BrandInfo>,
    onBack: () -> Unit,
    onNext: (String) -> Unit
) {
    var showFullList by remember { mutableStateOf(false) }
    var selectedBrand by remember { mutableStateOf("") }

    if (showFullList) {
        // --- Memanggil BrandListScreen dengan data asli ---
        BrandListScreen(
            brandListFromDbFull = matchViewModel.allBrandList, // Kirim list objek utuh (BrandInfo)
            onBrandSelected = { brand ->
                selectedBrand = brand
                showFullList = false
            },
            onClose = { showFullList = false }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {
            // --- Progress Bar & Back Button ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(15.dp))
                LinearProgressIndicator(
                    progress = { 0.33f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color.Black,
                    trackColor = Color(0xFFF0F0F0),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = "Step 1",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily
            )
            Text(
                text = "Enter a brand of foundation or concealer that you wear:",
                fontSize = 16.sp,
                color = Color.Gray,
                fontFamily = MyCustomFontFamily,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            )

            // --- Search Bar (Read-Only, Trigger List) ---
            OutlinedTextField(
                value = selectedBrand,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showFullList = true },
                enabled = false,
                placeholder = { Text("Search brand", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (selectedBrand.isNotEmpty()) {
                        IconButton(onClick = { selectedBrand = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Memberikan warna hitam jika sudah terpilih agar terlihat aktif
                    disabledBorderColor = if (selectedBrand.isNotEmpty()) Color.Black else Color(0xFFE0E0E0),
                    disabledTextColor = Color.Black,
                    disabledPlaceholderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            if (selectedBrand.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                    FloatingActionButton(
                        onClick = { onNext(selectedBrand) },
                        containerColor = Color(0xFFFFD1E3),
                        contentColor = Color.Black,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
                        modifier = Modifier.padding(bottom = 10.dp).size(56.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
