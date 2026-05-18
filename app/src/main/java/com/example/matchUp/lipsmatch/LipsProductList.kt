package com.example.matchUp.lipsmatch

import com.example.matchUp.fdmatch.Product
import androidx.compose.foundation.BorderStroke
import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun LipsProductListScreen(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 0.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            BasicTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontFamily = MyCustomFontFamily
                ),
                decorationBox = { innerTextField ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (searchText.isNotEmpty()) Color.Black else Color(0xFFDEDEDE)
                        ),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Search product",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    fontFamily = MyCustomFontFamily
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
        } // --- BATAS ROW (Header Selesai) ---

        // --- DIBAIKI: Divider dipindah ke luar Row (di dalam Column induk) agar memotong lurus dengan sempurna ---
        Spacer(modifier = Modifier.height(2.dp))
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            color = Color(0xFFF0F0F0),
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(2.dp))

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
                            fontSize = 15.sp,
                            color = Color.Black,
                            fontFamily = MyCustomFontFamily,
                            lineHeight = 20.sp
                        )

                        // Gambar Produk (Thumbnail Kanan)
                        AsyncImage(
                            model = product.image, // URL Gambar dari JSON
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                }
            }
        }
    }
}