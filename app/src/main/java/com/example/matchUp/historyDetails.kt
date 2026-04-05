package com.example.matchUp

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext // Tambahkan ini
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.ui.theme.MyCustomFontFamily

@Composable
fun HistoryDetailScreen(
    viewModel: MatchViewModel,
    historyIndex: Int,
    onBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit
) {
    // 1. Ambil context untuk Toast
    val context = LocalContext.current

    // 2. Mengambil data dari historyList yang bertipe HistoryItem
    val historyData = viewModel.historyList.getOrNull(historyIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // --- TOP BAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    modifier = Modifier.size(18.dp),
                    tint = Color.Black,
                    contentDescription = "Back"
                )
            }

            Text(
                text = "History Details",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontFamily = MyCustomFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            IconButton(
                onClick = {
                    Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = "Download",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (historyData == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "History not found",
                    color = Color.Gray,
                    fontFamily = MyCustomFontFamily
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Saved on ${historyData.date}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp),
                    fontFamily = MyCustomFontFamily
                )

                // --- SECTION 1: USER INPUT ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "You previously entered:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = MyCustomFontFamily
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFF5F5F5)
                )

                historyData.inputProducts.forEach { matchData ->
                    HistoryInputWithImage(
                        brandName = matchData.brandName,
                        name = matchData.product.product_name,
                        shade = matchData.shade.shade_name,
                        imageUrl = matchData.product.image
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- SECTION 2: MATCH RESULTS ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Your suggested matches:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black,
                        fontFamily = MyCustomFontFamily
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFF5F5F5)
                )

                // Menampilkan produk yang sudah tersimpan di history
                if (historyData.matchedProducts.isEmpty()) {
                    Text(
                        text = "No recommendation data saved.",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        fontFamily = MyCustomFontFamily,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                } else {
                    historyData.matchedProducts.forEach { item ->
                        RecommendationResultItem(
                            brand = item.brand,
                            product = item.productName,
                            shade = item.shadeName,
                            imageUrl = item.imageUrl,
                            onClick = { onNavigateToProductDetail(item.productName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// Komponen Pendukung
@Composable
fun HistoryMatchItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFFD1E3)))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, color = Color.DarkGray, fontFamily = MyCustomFontFamily)
    }
}

@Composable
fun RecommendationResultItem(brand: String, product: String, shade: String, imageUrl: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF9F9F9)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(brand.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            Text(product, fontSize = 13.sp, color = Color.DarkGray, maxLines = 1, fontFamily = MyCustomFontFamily)
            Text("Your Shade: $shade", fontSize = 13.sp, color = Color.DarkGray, fontFamily = MyCustomFontFamily)
        }
        Icon(Icons.Default.ArrowForwardIos, null, modifier = Modifier.size(14.dp), tint = Color.LightGray)
    }
}

@Composable
fun HistoryInputWithImage(brandName: String, name: String, shade: String, imageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5)),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = brandName.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                fontFamily = MyCustomFontFamily
            )
            Text(
                text = name,
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontFamily = MyCustomFontFamily,
                maxLines = 1
            )
            Text(
                text = "Your Shade: $shade",
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontFamily = MyCustomFontFamily
            )
        }
    }
}