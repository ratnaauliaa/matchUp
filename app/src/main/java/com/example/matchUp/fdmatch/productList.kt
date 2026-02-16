package com.example.matchUp.fdmatch

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage // Pastikan library Coil sudah terpasang

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    brandName: String,
    productList: List<Product>, // Menggunakan model Product yang berisi nama dan URL gambar
    onProductSelected: (Product) -> Unit,
    onClose: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    // Filter produk berdasarkan input pencarian
    val filteredProducts = productList.filter {
        it.product_name.contains(searchText, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- Search Bar Header ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search", color = Color.Gray) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFF0F0F0),
                    focusedBorderColor = Color.Black
                )
            )
            TextButton(onClick = onClose) {
                Text("Cancel", color = Color.Black, fontWeight = FontWeight.Medium)
            }
        }

        // --- List Produk dengan Gambar ---
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredProducts) { product ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProductSelected(product) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Nama Produk
                        Text(
                            text = product.product_name,
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            fontFamily = MyCustomFontFamily,
                            lineHeight = 20.sp
                        )

                        // Gambar Produk (Thumbnail Kanan)
                        AsyncImage(
                            model = product.image, // URL Gambar dari JSON
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}