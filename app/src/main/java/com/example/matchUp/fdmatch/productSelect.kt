package com.example.matchUp.fdmatch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun ProductSelectionScreen(
    selectedBrandName: String,
    productListFromDb: List<Product>, // Hasil kiriman dari ViewModel
    onBack: () -> Unit,
    onNext: (Product) -> Unit
) {
    var showFullList by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    // --- PENGAMAN UTAMA: Jika Brand tidak punya produk di database ---
    if (productListFromDb.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Database Belum Tersedia",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MyCustomFontFamily
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Maaf, produk untuk brand $selectedBrandName belum ada di database produk kami.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontFamily = MyCustomFontFamily
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Kembali Pilih Brand", color = Color.White)
            }
        }
    } else {
        // --- TAMPILAN JIKA DATA ADA ---
        if (showFullList) {
            ProductListScreen(
                brandName = selectedBrandName,
                productList = productListFromDb,
                onProductSelected = { product ->
                    selectedProduct = product
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
                // Header & Progress
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp).border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    LinearProgressIndicator(
                        progress = { 0.66f },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = Color.Black,
                        trackColor = Color(0xFFF0F0F0),
                        strokeCap = StrokeCap.Round
                    )
                }

                Spacer(modifier = Modifier.height(35.dp))
                Text("Step 2", fontSize = 32.sp, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily)
                Text(
                    text = "What product do you use from $selectedBrandName?",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // Search Bar Trigger
                Box(modifier = Modifier.fillMaxWidth().clickable { showFullList = true }) {
                    OutlinedTextField(
                        value = selectedProduct?.product_name ?: "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        placeholder = { Text("Search product") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (selectedProduct != null) {
                                IconButton(onClick = { selectedProduct = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = if (selectedProduct != null) Color.Black else Color(0xFFE0E0E0),
                            disabledTextColor = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (selectedProduct != null) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                        FloatingActionButton(
                            onClick = { onNext(selectedProduct!!) },
                            containerColor = Color(0xFFFFD1E3),
                            contentColor = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier.padding(bottom = 10.dp).size(56.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                        }
                    }
                }
            }
        }
    }
}