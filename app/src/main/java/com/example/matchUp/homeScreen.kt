package com.example.matchUp

import com.example.matchUp.ui.theme.MyCustomFontFamily
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainContent(
    userName: String,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onStartMatchClick: () -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeScreen(
                userName = userName,
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick,
                onStartMatchClick = onStartMatchClick
            )
        }
    }
}

@Composable
fun HomeScreen(
    userName: String,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onStartMatchClick: () -> Unit
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
                            append(userName) // Nama akan berubah sesuai yang didaftarkan
                        }
                    },
                    fontSize = 24.sp,
                    fontFamily = MyCustomFontFamily,
                    color = Color.Black
                )
                Text(
                    text = "Let's discover your perfect shade!",
                    fontSize = 14.sp,
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
                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
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
            shape = RoundedCornerShape(16.dp),
            onClick = { /* Aksi klik banner ke quiz undertone */ }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Don't know your Undertone? Find out now!",
                    fontSize = 13.sp,
                    fontFamily = MyCustomFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
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
                onClick = onStartMatchClick // Ini akan navigasi ke Step 1
            )
            Spacer(modifier = Modifier.width(16.dp))
            CategoryCard(
                title = "Lips",
                imageRes = R.drawable.ic_lips,
                containerColor = Color(0xFFFFD1E3),
                modifier = Modifier.weight(1f),
                onClick = { /* Implementasi Lips Match */ }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

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

        Spacer(modifier = Modifier.height(30.dp))

        // --- FOR YOU SECTION ---
        SectionHeader(title = "For you", actionText = "View more", onShowAllClick = {})
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            ArticleCard(
                title = "10 Tips on How To Choose Your Perfect Foundation Shade",
                imageRes = R.drawable.img_article1
            )
            Spacer(modifier = Modifier.width(16.dp))
            ArticleCard(
                title = "How To Identify Your Skin Undertone",
                imageRes = R.drawable.img_article2
            )
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun CategoryCard(title: String, imageRes: Int, containerColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            modifier = Modifier.size(100.dp)
        )
        Text(text = title, fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily, fontSize = 16.sp, color = Color.Black)
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
fun ArticleCard(title: String, imageRes: Int) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
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

@Composable
fun BottomNavigationBar() {
    NavigationBar(containerColor = Color.White) {
        // List menu agar kode lebih rapi
        val items = listOf("Home", "Insights", "Wishlist", "Profile")
        val icons = listOf(Icons.Default.Home, Icons.Outlined.Widgets, Icons.Outlined.FavoriteBorder, Icons.Outlined.Person)

        items.forEachIndexed { index, item ->
            val isSelected = index == 0 // Contoh: Home yang terpilih

            NavigationBarItem(
                selected = isSelected,
                onClick = { /* Navigasi ke screen terkait */ },
                icon = {
                    Icon(
                        imageVector = if (isSelected) icons[index] else icons[index],
                        contentDescription = item
                    )
                },
                label = { Text(item) },
                colors = NavigationBarItemDefaults.colors(
                    // Warna saat terpilih
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color.Black,
                    indicatorColor = Color(0xFFFFD1E3), // Background lonjong pink

                    // WARNA ABU GANTI HITAM DI SINI:
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black
                )
            )
        }
    }
}