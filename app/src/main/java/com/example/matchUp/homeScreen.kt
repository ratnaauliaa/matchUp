package com.example.matchUp


import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
    viewModel: MatchViewModel, // Tambahkan ini
    navController: NavHostController,
    onNotificationsClick: () -> Unit,
    onStartMatchClick: () -> Unit,
    onUndertoneClick: () -> Unit,
    isLoggedIn: Boolean,
    onInsightClick: () -> Unit,
) {
    HomeScreen(
        userName = userName,
        onProfileClick = onProfileClick,
        viewModel = viewModel, // Teruskan ke bawah
        navController = navController,
        onNotificationsClick = onNotificationsClick,
        onStartMatchClick = onStartMatchClick,
        onUndertoneClick = onUndertoneClick,
        isLoggedIn = isLoggedIn,
        onInsightClick = onInsightClick
    )
}

@Composable
fun HomeScreen(
    userName: String,
    onProfileClick: () -> Unit,
    viewModel: MatchViewModel, // Tambahkan ini
    navController: NavHostController,
    onNotificationsClick: () -> Unit,
    onStartMatchClick: () -> Unit,
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
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNotificationsClick) {
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

        // --- BANNER UNDERTONE ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE8EAF6),
            shape = RoundedCornerShape(20.dp), // Lebih melengkung (pill shape)
            onClick = {
                if (isLoggedIn) onUndertoneClick() else navController.navigate("login")
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Don't know your Undertone? Find out now!",
                    fontSize = 15.sp,
                    fontFamily = MyCustomFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CATEGORY SECTION ---
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(
                title = "Foundation",
                imageRes = R.drawable.ic_fd,
                containerColor = Color(0xFFFFD1E3),
                modifier = Modifier.weight(1f),
                onClick = {
                    if (viewModel.isLoggedIn) { // Pakai 'viewModel' yang dikirim dari parameter
                        onStartMatchClick() // Panggil callback yang sudah ada
                    } else {
                        navController.navigate("login")
                    }
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryCard(
                title = "Lips",
                imageRes = R.drawable.ic_lips,
                containerColor = Color(0xFFFFD1E3),
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isLoggedIn) {
                        onUndertoneClick()
                    } else {
                        navController.navigate("login")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- RECOMMENDATION SECTION ---
        SectionHeader(title = "Recommendation", onShowAllClick = {})
        RecommendationItem(
            brand = "Somethinc",
            name = "Copy Paste Tinted Sunscreen",
            imageRes = R.drawable.prod_somethinc
        )
        RecommendationItem(
            brand = "3 Concept Eyes (3CE)",
            name = "Glow Cushion",
            imageRes = R.drawable.prod_3ce
        )

        Spacer(modifier = Modifier.height(10.dp))

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
                onClick = onInsightClick // Berhasil karena ArticleCard sudah diupdate
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
    Box(
        modifier = modifier
            .height(160.dp) // Tingginya ditambah agar tidak kepotong
            .clip(RoundedCornerShape(32.dp))
            .background(containerColor)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom // Teks di bawah
        ) {
            AsyncImage(
                model = imageRes,
                contentDescription = title,
                modifier = Modifier.size(100.dp), // Ukuran gambar ilustrasi
                contentScale = ContentScale.Fit
            )

            Text(
                text = title,
                fontWeight = FontWeight.Normal,
                fontFamily = MyCustomFontFamily,
                fontSize = 15.sp, // Ukuran lebih besar seperti Figma
                color = Color.Black
            )
        }

        // TOMBOL PANAH DI SAMPING (Sesuai Figma)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp) // Setengah lingkaran keluar
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp).padding(end = 5.dp),
                tint = Color.Black
            )
        }
    }
}
@Composable
fun RecommendationItem(brand: String, name: String, imageRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { /* Detail Produk */ },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(brand, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily, fontSize = 14.sp, color = Color.Black)
            Text(name, color = Color.Gray, fontFamily = MyCustomFontFamily, fontSize = 12.sp)
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
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
                .crossfade(true) // Menambahkan efek fade-in halus
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
            Text(text = title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String = "Show all", onShowAllClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        TextButton(onClick = onShowAllClick) {
            Text(actionText, color = Color.Gray, fontSize = 12.sp)
        }
    }
}