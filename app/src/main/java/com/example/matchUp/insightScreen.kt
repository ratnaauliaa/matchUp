package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.fdmatch.Article
import com.example.matchUp.ui.theme.MyCustomFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: MatchViewModel,
    onNavigateToDetail: (Int) -> Unit
) {
    val categories = listOf("All", "Makeup", "Skin", "Tips")
    val articles = viewModel.getFilteredArticles()

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // --- HEADER & SEARCH BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD1E3))
                .padding(top = 30.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    placeholder = { Text("Search articles", fontSize = 14.sp, color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(25.dp),
                    trailingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp).clickable { /* Bookmark */ },
                    tint = Color.Black
                )
            }
        }

        // --- CATEGORY TABS ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            categories.forEach { category ->
                val isSelected = viewModel.selectedInsightCategory == category
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.selectedInsightCategory = category },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = category,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontFamily = MyCustomFontFamily,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (isSelected) Color.Black else Color.Transparent)
                    )
                }
            }
        }
        // --- ARTICLES LIST ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 0.dp,
                bottom = 80.dp
            )
        ) {
            items(articles) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onNavigateToDetail(article.id) }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article, onClick: () -> Unit) {
    val context = LocalContext.current

    // Optimasi pengambilan resource ID
    val imageResId = remember(article.imageUrl) {
        val id = context.resources.getIdentifier(
            article.imageUrl,
            "drawable",
            context.packageName
        )
        if (id != 0) id else android.R.drawable.ic_menu_gallery
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        // Kategori Artikel
        Text(
            text = article.category,
            color = Color.Gray,
            fontSize = 12.sp,
            fontFamily = MyCustomFontFamily,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top // Gambar sejajar dengan baris pertama judul
        ) {
            // Gambar Artikel dengan Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageResId)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Judul Artikel (Warna Hitam)
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Color.Black,
                    fontFamily = MyCustomFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Deskripsi Artikel
                Text(
                    text = article.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    fontFamily = MyCustomFontFamily,
                    lineHeight = 18.sp,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Row untuk "Read more" dan Icon agar sejajar horizontal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Read more",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = MyCustomFontFamily
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}