package com.example.matchUp

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchUp.fdmatch.MatchViewModel
import com.example.matchUp.ui.theme.MyCustomFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    viewModel: MatchViewModel,
    historyIndex: Int, // Menerima index dari halaman History
    onBack: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit
) {
    // Ambil data spesifik dari list history berdasarkan index yang diklik
    val historyData = viewModel.historyList.getOrNull(historyIndex)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("History Details", fontWeight = FontWeight.Bold, fontFamily = MyCustomFontFamily) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Simpan/Download logic */ }) {
                        Icon(Icons.Default.Download, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (historyData == null) {
            // Jika data tidak sengaja hilang (misal aplikasi restart)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("History data not found", fontFamily = MyCustomFontFamily)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // TANGGAL (Sesuai saat kamu klik 'Find My Match')
                Text(
                    text = historyData.date,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontFamily = MyCustomFontFamily
                )

                // SECTION 1: RECALL INPUT (Data yang tersimpan di history)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp), tint = Color(0xFFD81B60))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("You entered this matches:", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = MyCustomFontFamily)
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                // Menampilkan produk yang dulu di-input (Brand + Nama + Shade)
                historyData.details.forEach { detailText ->
                    HistoryMatchItem(text = detailText)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // SECTION 2: BROWSE RECOMMENDATIONS (Menampilkan ulang rekomendasi terbaik)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(18.dp), tint = Color(0xFFD81B60))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Your best match recommendations:", fontWeight = FontWeight.Bold, fontSize = 15.sp, fontFamily = MyCustomFontFamily)
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

                // LOGIKA: Ambil 5 produk random dari database untuk simulasi rekomendasi di halaman history
                // Di skripsi, ini menunjukkan sistem tetap bisa memberikan saran shade berdasarkan data lama
                val sampleRecommendations = viewModel.productsData.flatMap { it.products }.take(5)

                sampleRecommendations.forEach { product ->
                    RecommendationHistoryItem(
                        brand = "Brand", // Bisa disesuaikan jika ingin lebih detail
                        product = product.product_name,
                        shade = product.shades.firstOrNull()?.shade_name ?: "Universal",
                        onClick = { onNavigateToProductDetail(product.product_name) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun HistoryMatchItem(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        color = Color(0xFFFDFDFD),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFD1E3).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.History, null, modifier = Modifier.size(18.dp), tint = Color(0xFFD81B60))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontFamily = MyCustomFontFamily,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun RecommendationHistoryItem(brand: String, product: String, shade: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder Box untuk Foto Produk
        Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF9F9F9)))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(brand.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(product, fontSize = 13.sp, color = Color.Black, maxLines = 1, fontFamily = MyCustomFontFamily)
            Text("Your Shade: $shade", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD81B60))
        }
        Text("Details >", fontSize = 10.sp, color = Color.LightGray, fontFamily = MyCustomFontFamily)
    }
}