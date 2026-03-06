package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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


    // Logic filter tetap sama
    val filteredProducts = viewModel.savedProducts.filter {
        it.product_name.contains(searchQuery, ignoreCase = true) ||
                it.brand.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- PINK HEADER SECTION ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD1E3))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search product", fontSize = 14.sp, fontFamily = MyCustomFontFamily) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color.White, CircleShape),
                shape = CircleShape,
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                categories.forEachIndexed { index, title ->
                    Column(
                        modifier = Modifier
                            .clickable { selectedTab = index }
                            .padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontFamily = MyCustomFontFamily,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color.Black else Color.Gray
                        )
                        if (selectedTab == index) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .width(35.dp)
                                    .height(2.5.dp)
                                    .background(Color.Black, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }
        }

        // --- CONTENT SECTION ---
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
            if (filteredProducts.isEmpty()) {
                EmptyWishlistContent()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredProducts.size} Items",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MyCustomFontFamily
                    )

                    IconButton(onClick = { isGridView = !isGridView }) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Switch View",
                            modifier = Modifier.size(24.dp),
                            tint = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredProducts) { product ->
                            WishlistProductCard(
                                product = product,
                                onRemove = { viewModel.toggleSaveProduct(product) },
                                onClick = {
                                    // Ini yang akan dijalankan saat kartu diklik
                                    navController.navigate("product_detail/${product.product_name}")
                                }

                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredProducts) { product ->
                            WishlistProductListRow(
                                product = product,
                                onRemove = { viewModel.toggleSaveProduct(product) },
                                onClick = {
                                    navController.navigate("product_detail/${product.product_name}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistProductCard(product: Product, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9F9F9)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.image,
                        contentDescription = null,
                        modifier = Modifier.size(85.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clickable { onRemove() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            // .brand sekarang aman karena constructor di MatchViewModels sudah kita perbaiki
            Text(text = product.brand, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily, fontSize = 13.sp)
            Text(text = product.product_name, color = Color.Gray, fontFamily = MyCustomFontFamily, fontSize = 11.sp, maxLines = 2)
        }
    }
}

@Composable
fun WishlistProductListRow(product: Product, onRemove: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F9F9)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.brand, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily, fontSize = 13.sp)
                Text(text = product.product_name, color = Color.Gray, fontFamily = MyCustomFontFamily, fontSize = 11.sp, maxLines = 1)
            }

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyWishlistContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.history_empty),
            contentDescription = null,
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "No Wishlist Yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = MyCustomFontFamily
        )
        Text(
            text = "No products saved yet.\nYour favorite shades will show up here!",
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = MyCustomFontFamily,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}