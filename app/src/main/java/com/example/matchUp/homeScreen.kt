package com.example.matchUp


import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.matchUp.fdmatch.MatchViewModel


@Composable
fun MainContent(
    userName: String,
    onProfileClick: () -> Unit,
    viewModel: MatchViewModel,
    navController: NavHostController,
    onNotificationClick: () -> Unit,
    onStartMatchClick: () -> Unit,
    onLipsMatchClick: () -> Unit,
    onUndertoneClick: () -> Unit,
    isLoggedIn: Boolean,
    onInsightClick: () -> Unit,
) {
    HomeScreen(
        userName = userName,
        onProfileClick = onProfileClick,
        viewModel = viewModel,
        navController = navController,
        onNotificationClick = onNotificationClick,
        onStartMatchClick = onStartMatchClick,
        onLipsMatchClick = onLipsMatchClick,
        onUndertoneClick = onUndertoneClick,
        isLoggedIn = isLoggedIn,
        onInsightClick = onInsightClick

    )
}

@Composable
fun HomeScreen(
    userName: String,
    onProfileClick: () -> Unit,
    viewModel: MatchViewModel,
    navController: NavHostController,
    onNotificationClick: () -> Unit,
    onStartMatchClick: () -> Unit,
    onLipsMatchClick: () -> Unit,
    isLoggedIn: Boolean,
    onUndertoneClick: () -> Unit,
    onInsightClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // --- HEADER SECTION ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = buildAnnotatedString {
                        append("Hello, ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                            append(userName)
                        }
                    },
                    fontSize = 24.sp,
                    fontFamily = MyCustomFontFamily,
                    color = Color.Black
                )
                Text(
                    text = "Let's discover your perfect shade!",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNotificationClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                AsyncImage(
                    model = R.drawable.profile_pic,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    contentScale = ContentScale.Crop
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        // --- CATEGORY SECTION ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(
                title = "Match Foundation",
                imageRes = R.drawable.ic_fd,
                containerColor = Color(0xFFFFD1E3),
                modifier = Modifier.weight(1f),
                onClick = {
                    if (viewModel.isLoggedIn) {
                        onStartMatchClick()
                    } else {
                        navController.navigate("login")
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryCard(
                title = "Match Lips",
                imageRes = R.drawable.ic_lips,
                containerColor = Color(0xFFFFD1E3),
                modifier = Modifier.weight(1f),
                onClick = {
                    if (viewModel.isLoggedIn) {
                        onLipsMatchClick()
                    } else {
                        navController.navigate("login")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BANNER UNDERTONE ---
        val density = LocalDensity.current

        val surfaceColor = Color(0xFFE8EAF6)

        val shadowColor = Color(0xFFC5CAE9).copy(alpha = 0.7f)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val radiusPx = 20.dp.toPx()
                    drawRoundRect(
                        color = shadowColor,
                        topLeft = Offset(0f, 10f), // Bayangan turun ke bawah
                        size = size,
                        cornerRadius = CornerRadius(radiusPx, radiusPx)
                    )
                },
            color = surfaceColor,
            shape = RoundedCornerShape(20.dp),
            onClick = {
                if (isLoggedIn) onUndertoneClick() else navController.navigate("login")
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't know your Undertone? Find out now!",
                    fontSize = 15.sp,
                    fontFamily = MyCustomFontFamily,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))


        SectionHeader(title = "Recommendation for You", onShowAllClick = {})

// Di dalam HomeScreen.kt

// Jalankan filter HANYA saat finalUndertone berubah
        // Di HomeScreen.kt

// 1. Picu perhitungan rekomendasi HANYA saat undertone berubah
        LaunchedEffect(viewModel.finalUndertone) {
            viewModel.updateRecommendations(viewModel.finalUndertone)
        }

// 2. Ambil data dari state yang sudah dikunci di ViewModel
        val recommendedProducts = viewModel.filteredRecommendations

        if (recommendedProducts.isEmpty()) {
            Text(
                text = "Take the undertone test to see personalized recommendations!",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            Column {
                recommendedProducts.forEach { (brandName, product, matchedShade) ->
                    // Di HomeScreen.kt
                    RecommendationItem(
                        brand = brandName,
                        name = "${product.product_name} (${matchedShade.shade_name})",
                        imageUrl = product.image,
                        onItemClick = {
                            // Kirim nama produk dan nama shade yang cocok
                            navController.navigate("product_detail/${product.product_name}|${matchedShade.shade_name}")
                        }
                    )
                }
            }
        }

        // --- FOR YOU SECTION ---
        SectionHeader(
            title = "For you",
            actionText = "View more",
            onShowAllClick = onInsightClick
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            ArticleCard(
                title = "10 Tips on How To Choose Your Perfect Foundation Shade",
                imageRes = R.drawable.img_shade,
                onClick = onInsightClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            ArticleCard(
                title = "How To Identify Your Skin Undertone",
                imageRes = R.drawable.img_undertone,
                onClick = onInsightClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            ArticleCard(
                title = "4 Tips To Make Your Makeup Look Smooth",
                imageRes = R.drawable.img_skin,
                onClick = onInsightClick
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CategoryCard(
    title: String,
    imageRes: Int,
    containerColor: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    // Ambil density agar .toPx() bisa jalan
    val density = LocalDensity.current

    val shadowColor = Color(0xFFE08EB3).copy(alpha = 0.5f)


    Box(
        modifier = modifier
            .height(160.dp)
            .drawBehind {
                val radiusPx = 32.dp.toPx()
                drawRoundRect(
                    color = shadowColor,
                    // Samakan dengan yang biru (10f) agar konsisten melayang
                    topLeft = Offset(0f, 10f),
                    size = size,
                    cornerRadius = CornerRadius(radiusPx, radiusPx)
                )
            }
            .clip(RoundedCornerShape(32.dp))
            .background(containerColor)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            AsyncImage(
                model = imageRes,
                contentDescription = title,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontFamily = MyCustomFontFamily,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun RecommendationItem(brand: String, name: String, imageUrl: String, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).error(R.drawable.prod_smth).build(),
            contentDescription = name,
            modifier = Modifier.size(65.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F5F5)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.weight(1f).height(65.dp)) {
            Column(modifier = Modifier.align(Alignment.TopStart)) {
                Text(text = brand, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily, fontSize = 14.sp, color = Color.Black)
                Text(text = name, color = Color.Gray, fontFamily = MyCustomFontFamily, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.BottomEnd)) {
                Text(text = "Details", color = Color.Gray, fontSize = 11.sp)
                Icon(Icons.Default.ChevronRight, null, Modifier.size(14.dp), tint = Color.Gray)
            }
        }
    }
}


@Composable
fun ArticleCard(title: String, imageRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageRes)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(text = title, color = Color.White, fontSize = 14.sp,fontFamily = MyCustomFontFamily, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String = "Show all", onShowAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title,fontWeight = FontWeight.SemiBold, fontFamily = MyCustomFontFamily, fontSize = 18.sp, color = Color.Black)
        TextButton(onClick = onShowAllClick) {
            Text(actionText, color = Color.Gray, fontFamily = MyCustomFontFamily, fontSize = 13.sp)
        }
    }
}