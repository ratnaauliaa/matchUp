package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
// --- FIX IMPORT: Menggunakan alias agar tidak bentrok ---
import androidx.compose.foundation.lazy.grid.items as itemsGrid
import androidx.compose.foundation.lazy.items as itemsList
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.fdmatch.Product
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun WishlistScreen(viewModel: MatchViewModel, navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    val categories = listOf("All", "Face", "Lips")
    var isGridView by remember { mutableStateOf(true) }

    // PERBAIKAN LOGIKA: Sekarang memfilter berdasarkan kategori JSON secara dinamis
    val filteredProducts by remember(viewModel.savedProducts.size, searchQuery, selectedTab) {
        derivedStateOf {
            viewModel.savedProducts.filter { product ->
                // 1. Saring berdasarkan tab kategori yang dipilih
                val categoryMatch = when (selectedTab) {
                    1 -> product.category.equals("foundation", ignoreCase = true) // Tab Face
                    2 -> product.category.equals("lips", ignoreCase = true)       // Tab Lips
                    else -> true                                                  // Tab All
                }

                // 2. Saring berdasarkan query pencarian nama/brand
                val nameMatch = product.product_name?.contains(searchQuery, ignoreCase = true) ?: false
                val brandMatch = product.brand?.contains(searchQuery, ignoreCase = true) ?: false
                val textMatch = nameMatch || brandMatch

                categoryMatch && textMatch
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD1E3))
                .padding(top = 30.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search product", fontSize = 14.sp, color = Color.Gray, fontFamily = MyCustomFontFamily) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                trailingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            categories.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Column(
                    modifier = Modifier.weight(1f).clickable { selectedTab = index },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontFamily = MyCustomFontFamily
                    )
                    if (isSelected) {
                        Box(modifier = Modifier.width(40.dp).height(2.dp).background(Color.Black))
                    }
                }
            }
        }

        if (filteredProducts.isEmpty()) {
            EmptyWishlistContent()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${filteredProducts.size} Items", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 14.sp, fontFamily = MyCustomFontFamily)
                IconButton(onClick = { isGridView = !isGridView }) {
                    Icon(imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView, contentDescription = null, tint = Color.Black)
                }
            }

            if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsGrid(filteredProducts, key = { it.product_name ?: "" }) { product ->
                        WishlistProductCard(
                            product = product,
                            onRemove = { viewModel.toggleSaveProduct(product, product.brand ?: "") },
                            onClick = {
                                // PERBAIKAN NAVIGASI: Mengarahkan detail screen sesuai kategori produknya
                                if (product.category.equals("lips", ignoreCase = true)) {
                                    navController.navigate("lips_product_detail/${product.product_name}")
                                } else {
                                    navController.navigate("product_detail/${product.product_name}")
                                }
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsList(filteredProducts, key = { it.product_name ?: "" }) { product ->
                        WishlistProductListRow(
                            product = product,
                            onRemove = { viewModel.toggleSaveProduct(product, product.brand ?: "") },
                            onClick = {
                                // PERBAIKAN NAVIGASI: Mengarahkan detail screen sesuai kategori produknya
                                if (product.category.equals("lips", ignoreCase = true)) {
                                    navController.navigate("lips_product_detail/${product.product_name}")
                                } else {
                                    navController.navigate("product_detail/${product.product_name}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistProductCard(product: Product, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(110.dp).clip(RoundedCornerShape(12.dp)).background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = null,
                        modifier = Modifier.size(85.dp),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.kosong)
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(30.dp).clickable { onRemove() },
                    shadowElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(product.brand ?: "Brand", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp, fontFamily = MyCustomFontFamily)
            Text(product.product_name ?: "Product", color = Color.Gray, fontSize = 11.sp, maxLines = 2, textAlign = TextAlign.Center, modifier = Modifier.heightIn(min = 28.dp), fontFamily = MyCustomFontFamily)
        }
    }
}

@Composable
fun WishlistProductListRow(product: Product, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.White),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.brand ?: "Brand", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp, fontFamily = MyCustomFontFamily)
                Text(product.product_name ?: "Product", color = Color.Gray, fontSize = 11.sp, maxLines = 1, fontFamily = MyCustomFontFamily)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Favorite, null, tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmptyWishlistContent() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.kosong), contentDescription = null, modifier = Modifier.size(180.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text("No Wishlist Yet", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = MyCustomFontFamily)
        Text("Your favorite shades will show up here!", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, fontFamily = MyCustomFontFamily)
    }
}